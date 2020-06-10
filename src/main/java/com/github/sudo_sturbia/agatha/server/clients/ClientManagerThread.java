package com.github.sudo_sturbia.agatha.server.clients;

import com.github.sudo_sturbia.agatha.server.database.ConnectorBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * ClientManagerThread is a thread that runs parallel to the
 * main thread of the server and is used to verify clients' credentials.
 * <p>
 * Agatha's server is supposed to deal with ClientManagerThread directly,
 * but should rather interact user ClientManager. ClientManager creates
 * a ClientManagerThread with necessary communication objects and
 * interacts with ClientManagerThread throughout execution of the
 * server.
 * <p>
 * ClientManagerThread verifies credentials, as well as the existence
 * of usernames (to avoid conflicts.) It also enforces a timeout
 * mechanism in case of failure to login. So if a client fails to
 * login (submits an incorrect password) five or more times the
 * account is subsequently locked for 30 minutes starting from
 * the last failed attempt.
 */
public class ClientManagerThread extends Thread
{
    /** Sender/Receiver pair used for communication. */
    private final BlockingQueue<Credentials> receiver;
    private final BlockingQueue<ClientManager.LoginStatus> sender;

    /** List of timed out usernames. */
    private final Map<String, Long> timedOut;

    /** A map of usernames for users that have failed to login
     * mapped to the number of times user has failed. Any user
     * that reaches five or more failed attempts is removed from
     * this map and added to the timeout list for 30 minutes. */
    private final Map<String, Integer> failedLoginCount;

    public ClientManagerThread(BlockingQueue<ClientManager.LoginStatus> sender,
                               BlockingQueue<Credentials> receiver)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.timedOut = new HashMap<>();
        this.failedLoginCount = new HashMap<>();
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                Credentials credentials = receiver.take();

                // User is timed out
                if (credentials.getPassword() != null && timedOut.containsKey(credentials.getUsername()))
                {
                    if (((System.currentTimeMillis() - timedOut.get(credentials.getUsername())) / 1000) < 30)
                    { // Timeout hasn't ended
                        sender.put(ClientManager.LoginStatus.TIMEOUT);
                        continue;
                    }

                    timedOut.remove(credentials.getUsername()); // Timeout ended
                }

                boolean doesExist = credentials.getPassword() == null ?
                        doesExist(credentials.getDatabaseName(), credentials.getUsername()) :
                        doesExist(credentials.getDatabaseName(), credentials.getUsername(), credentials.getPassword());

                sender.put(doesExist ? ClientManager.LoginStatus.SUCCEEDED : ClientManager.LoginStatus.FAILED);
            }
        }
        catch (InterruptedException e)
        {
            // Server finished execution ..
        }
    }

    /**
     * Verify that username exists in the database.
     *
     * @param dbName name of application's database.
     * @param username username to verify.
     * @return True if username exists, false otherwise.
     */
    private boolean doesExist(String dbName, String username)
    {
        boolean exists = true; // Assume that username exists for safety
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM ?.Users WHERE username = '?';");
        ) {
            statement.setString(1, dbName);
            statement.setString(2, username);

            try (ResultSet set = statement.executeQuery())
            {
                // If username doesn't exist
                if (!set.next())
                {
                    exists = false;
                }
            }
        }
        catch (SQLException e)
        {
            return true; // Safety fallback
        }

        return exists;
    }

    /**
     * Verify client's credentials (username, and password.)
     *
     * @param dbName name of application's database.
     * @param username username to verify.
     * @param password password to verify.
     * @return True if given credentials exist.
     */
    private boolean doesExist(String dbName, String username, String password)
    {
        // Encrypt password using sha256
        String encrypted = DigestUtils.sha256Hex(password);

        boolean exists = false; // Assume that credentials are wrong for safety
        try (
                Connection connection = ConnectorBuilder.get().get();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM ?.Users WHERE username = '?';");
        ) {
            statement.setString(1, dbName);
            statement.setString(2, username);

            try (ResultSet set = statement.executeQuery())
            {
                // Verify credentials
                if (set.next())
                {
                    if (set.getString("username").equals(username) &&
                            set.getString("password").equals(encrypted))
                    {
                        exists = true;
                    }
                }
            }
        }
        catch (SQLException e)
        {
            return false; // Safety fallback
        }

        if (!exists)
        {
            this.loginFailed(username);
        }

        return exists;
    }

    /**
     * Handle client's failure to login. Increments number of
     * failed attempts for the user. Gives the client a timeout
     * if login attempt failed five or more times.
     *
     * @param username client's username.
     */
    private void loginFailed(String username)
    {
        this.failedLoginCount.put(username, this.failedLoginCount.get(username) == null ? 1 : this.failedLoginCount.get(username) + 1);

        if (this.failedLoginCount.get(username) >= 5)
        {
            this.timedOut.put(username, System.currentTimeMillis());

        }
    }
}
