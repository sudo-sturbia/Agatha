package com.github.sudo_sturbia.agatha.client.model;

/**
 * ClientVerifier is responsible for verification of client's
 * credentials when logging in.
 * <p>
 * It's main objective is to return an instance of user's Library
 * to be used by application's GUI. It acts as a builder class for
 * Library objects and uses chaining for better code readability.
 */
public class ClientVerifier
{
    /** Client's username. */
    private String username;

    /** Client's password. */
    private String password;

    /** Private constructor. */
    private ClientVerifier() { }

    /**
     * Create and return a new verifier instance. Should be
     * the first called method.
     *
     * @return A new verifier instance.
     */
    public static ClientVerifier newVerifier()
    {
        return new ClientVerifier();
    }

    /**
     * Verify given credentials and get an instance of client's
     * Library if credentials are correct.
     * <p>
     * Should be called last as Client's information is deleted
     * after this method is called.
     *
     * @return Client's Library if credentials are correct, null
     *         otherwise.
     */
    public Library library()
    {
        //
        // TODO ..
        // Verify credentials and return Library.
        //
        return null;
    }

    /**
     * Delete client's credentials from verifier object.
     */
    private void clean()
    {
        this.username = null;
        this.password = null;
    }

    /**
     * Set username of client to be verified.
     *
     * @param username client's username.
     * @return A ClientVerifier object with username set.
     */
    public ClientVerifier username(final String username)
    {
        this.username = username;
        return this;
    }

    /**
     * Set password of client to be verified.
     *
     * @param password client's password.
     * @return A ClientVerifier object with password set.
     */
    public ClientVerifier password(final String password)
    {
        this.password = password;
        return this;
    }
}
