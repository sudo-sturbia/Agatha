package com.github.sudo_sturbia.agatha.server.clients;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        return this.doesExist(dbName, username, null);
    }

    /**
     * Verify that given credentials exist in the database.
     *
     * @param dbName name of application's database.
     * @param username client's username.
     * @param password client's password.
     * @return True if credentials are correct, False otherwise.
     * @throws LoginTimeoutException
     *      if client has failed to access the account (gave an
     *      incorrect password) five or more times in the last
     *      30 minutes. Time is calculated starting from the last
     *      failed attempt.
     */
    public boolean doesExist(String dbName, String username, String password) throws LoginTimeoutException
    {
        try
        {
            this.sender.put(new Credentials(dbName, username, password));

            switch(this.receiver.take())
            {
                case TIMEOUT:
                    throw new LoginTimeoutException();
                case SUCCEEDED:
                    return true;
                case FAILED:
                default: // Safety
                    return false;
            }
        }
        catch (InterruptedException e)  // Shouldn't happen, Returns false for safety
        {
            return false;
        }
    }
}
