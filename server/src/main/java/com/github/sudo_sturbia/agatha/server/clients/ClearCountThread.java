package com.github.sudo_sturbia.agatha.server.clients;

import java.util.List;
import java.util.Map;

/**
 * ClearCountThread is responsible for clearing the number of failed
 * logins attached to a username after a specified amount of time.
 */
public class ClearCountThread extends Thread
{
    /**
     * Usernames mapped to number of failed login counts.
     * @see ClientManagerThread
     */
    private final Map<String, Integer> failedLoginCount;

    /**
     * A list of all current counter threads.
     * @see ClientManagerThread
     */
    private final List<ClearCountThread> countThreads;

    /** Username to be cleared by this thread. */
    private final String username;

    public ClearCountThread(Map<String, Integer> failedLoginCount,
                            List<ClearCountThread> countThreads,
                            String username)
    {
        this.failedLoginCount = failedLoginCount;
        this.countThreads = countThreads;
        this.username = username;
    }

    @Override
    public void run()
    {
        final long SLEEP = 5 * 3600000;
        try
        {
            ClearCountThread.sleep(SLEEP); // Wait 5 hours before executing
            this.failedLoginCount.remove(username);
            this.countThreads.remove(this);
        }
        catch (InterruptedException e)
        {
            // Return ..
        }
    }
}
