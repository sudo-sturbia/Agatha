package com.github.sudo_sturbia.agatha.server.clients;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ClientManager manages client related information. It can be used
 * to verify a client's credentials, or the existence of a username
 * (to avoid conflicts.)
 * <p>
 * ClientManager manages, and communicates with a ClientManagerThread
 * that runs parallel to the main thread throughout the execution of
 * the server.
 */
public class ClientManager
{
    /** Singleton manager instance. */
    private static ClientManager manager;

    /** Thread to manage client credentials. */
    private final ClientManagerThread thread;

    /** Sender/Receiver pair used for communication with thread. */
    private final BlockingQueue<Credentials> sender;
    private final BlockingQueue<LoginStatus> receiver;

    /**
     * Status of login, depends on given credentials.
     */
    public enum LoginStatus
    {
        SUCCEEDED, FAILED, TIMEOUT
    }

    /** Private singleton constructor. */
    private ClientManager()
    {
        this.sender = new LinkedBlockingQueue<>();
        this.receiver = new LinkedBlockingQueue<>();

        this.thread = new ClientManagerThread(this.receiver, this.sender);
        this.thread.start();
    }

    /**
     * Get singleton instance of ClientManager.
     *
     * @return returns a singleton ClientManager object.
     */
    public static ClientManager get()
    {
        if (ClientManager.manager == null)
        {
            ClientManager.manager = new ClientManager();
        }

        return ClientManager.manager;
    }

    /**
     * End execution of ClientManagerThread.
     */
    public void stopManagerThread()
    {
        ClientManager.manager = null;
        this.thread.interrupt();
    }

    /**
     * Verify that a username exists in the database.
     *
     * @param dbName name of application's database.
     * @param username client's username.
     * @return True if username exists, False otherwise.
     */
    public boolean doesExist(String dbName, String username)
    {
        try
        {
            this.sender.put(new Credentials(dbName, username, null));

            switch(this.receiver.take())
            {
                case FAILED:
                    return false;
                case SUCCEEDED:
                case TIMEOUT: // Can't happen
                default: // Can't happen
                    return true;
            }
        }
        catch (InterruptedException e)  // Shouldn't happen, Returns true for safety
        {
            return true;
        }
    }

    /**
     * Verify that given credentials exist in the database.
     *
     * @param dbName name of application's database.
     * @param username client's username.
     * @param password client's password.
     * @return
     *      LoginStatus.SUCCEEDED: if credentials are correct.
     *      LoginStatus.FAILED: if credentials are incorrect.
     *      LoginStatus.TIMEOUT: if client has failed to access
     *      the account (gave an incorrect password) five or more
     *      times in the last 30 minutes. Time is calculated starting
     *      from the last failed attempt.
     */
    public LoginStatus doesExist(String dbName, String username, String password)
    {
        try
        {
            this.sender.put(new Credentials(dbName, username, password));
            return this.receiver.take();
        }
        catch (InterruptedException e) // Shouldn't happen, Returns failed for safety
        {
            return LoginStatus.FAILED;
        }
    }
}
