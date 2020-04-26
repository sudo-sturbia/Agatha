package com.github.sudo_sturbia.agatha.server;

import com.github.sudo_sturbia.agatha.server.database.ConnectionPool;
import com.github.sudo_sturbia.agatha.server.database.Connector;
import com.github.sudo_sturbia.agatha.server.database.DriverConnector;

/**
 * ServerBuilder provides more configuration options when building
 * A Server object.
 * <p>
 * <pre>
 *     Server server = ServerBuilder.newServer()
 *                                  .dbName("Agatha")
 *                                  .port(54321)
 *                                  .dbServerUsername("root")
 *                                  .dbServerPass("")
 *                                  .connector(ServerBuilder.ConnectorType.POOL)
 *                                  .build();
 * </pre>
 */
public class ServerBuilder
{
    /** Available types of connectors. */
    public enum ConnectorType
    {
        NORMAL, POOL
    }

    /** Name of Application's database. */
    private String dbName;

    /** Port to listen to. */
    private int port;

    /** Username for database server (MySQL). */
    private String dbServerUsername;

    /** Password for database server (MySQL). */
    private String dbServerPass;

    /** Type of connector to use. */
    private ConnectorType connector;

    /** Private constructor. Sets values to default. */
    private ServerBuilder()
    {
        this.dbName = "Agatha";
        this.port = 54321;
        this.dbServerUsername = "root";
        this.dbServerPass = "";
        this.connector = ConnectorType.POOL;
    }

    /**
     * Get a new Builder instance.
     *
     * @return A new ServerBuilder instance with default settings.<br/>
     * <pre>
     *     database name = "Agatha"
     *     port = 54321
     *     database server username = "root"
     *     database server password = ""
     *     connector = pool
     * </pre>
     */
    public static ServerBuilder newServer()
    {
        return new ServerBuilder();
    }

    /**
     * Build a new server object with given configurations. If any
     * of the options are set to an invalid value (null or "" for
     * strings except dbServerPass, -ve for integers.) default values
     * are used.
     *
     * @return A newly created Server object with specified configs.
     */
    public Server build()
    {
        // Validate values
        if (this.dbName == null || this.dbName.isEmpty())
        {
            this.dbName = "Agatha";
        }

        if (this.port < 0)
        {
            this.port = 54321;
        }

        if (this.dbServerUsername == null || this.dbServerUsername.isEmpty())
        {
            this.dbServerUsername = "root";
        }

        if (this.dbServerPass == null)
        {
            this.dbServerPass = "";
        }

        if (this.connector == null)
        {
            this.connector = ConnectorType.POOL;
        }

        // Build Server
        Connector connector;
        switch (this.connector)
        {
            case NORMAL:
                connector = new DriverConnector(this.dbName,
                        this.dbServerUsername, this.dbServerPass);
                break;
            case POOL:
                connector = new ConnectionPool(this.dbName,
                        this.dbServerUsername, this.dbServerPass);
                break;
            default:
                // CAN'T HAPPEN ..
                connector = null;
        }

        return new ServerImp(this.dbName, this.port, connector);
    }

    /**
     * Set database's name.
     *
     * @param name name of database to use.
     * @return A ServerBuilder instance with dbName set.
     */
    public ServerBuilder dbName(String name)
    {
        this.dbName = name;
        return this;
    }

    /**
     * Set port number.
     *
     * @param port port number to use.
     * @return A ServerBuilder instance with port set.
     */
    public ServerBuilder port(int port)
    {
        this.port = port;
        return this;
    }

    /**
     * Set username of database server (MySQL.)
     *
     * @param username database server's username.
     * @return A ServerBuilder instance with dbServerUsername set.
     */
    public ServerBuilder dbServerUsername(String username)
    {
        this.dbServerUsername = username;
        return this;
    }

    /**
     * Set password for database server (MySQL.)
     *
     * @param password database server's password.
     * @return A ServerBuilder instance with dbServerPass set.
     */
    public ServerBuilder dbServerPass(String password)
    {
        this.dbServerPass = password;
        return this;
    }

    /**
     * Set type of connector to use.
     *
     * @param connector type of connector to use.
     * @return A ServerBuilder instance with connector set.
     */
    public ServerBuilder connector(ConnectorType connector)
    {
        this.connector = connector;
        return this;
    }
}
