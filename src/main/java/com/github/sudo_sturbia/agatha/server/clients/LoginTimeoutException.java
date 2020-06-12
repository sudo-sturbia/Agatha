package com.github.sudo_sturbia.agatha.server.clients;

/**
 * LoginTimeoutException is thrown as an indication that a client
 * can't access the account in the meantime for security reasons.
 */
public class LoginTimeoutException extends RuntimeException
{
    /**
     * Construct a LoginTimeoutException with no message.
     */
    public LoginTimeoutException()
    {
        super();
    }

    /**
     * Construct a LoginTimeoutException with a given message.
     *
     * @param message detail message.
     */
    public LoginTimeoutException(String message)
    {
        super(message);
    }

    /**
     * Construct a LoginTimeoutException with a given message and cause.
     *
     * @param message detail message.
     * @param cause exception's cause.
     */
    public LoginTimeoutException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * Construct a LoginTimeoutException with a cause and no message.
     *
     * @param cause exception's cause.
     */
    public LoginTimeoutException(Throwable cause)
    {
        super(cause);
    }
}
