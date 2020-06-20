package com.github.sudo_sturbia.agatha.server;

/**
 * ServerSetupException is thrown to indicate an error while
 * performing initial server (or database) setup.
 */
public class ServerSetupException extends RuntimeException
{
    /**
     * Construct a ServerSetupException with no message.
     */
    public ServerSetupException()
    {
        super();
    }

    /**
     * Construct a ServerSetupException with a given message.
     *
     * @param message detail message.
     */
    public ServerSetupException(String message)
    {
        super(message);
    }

    /**
     * Construct a ServerSetupException with a given message and cause.
     *
     * @param message detail message.
     * @param cause exception's cause.
     */
    public ServerSetupException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Construct a ServerSetupException with a cause and no message.
     *
     * @param cause exception's cause.
     */
    public ServerSetupException(Throwable cause)
    {
        super(cause);
    }
}
