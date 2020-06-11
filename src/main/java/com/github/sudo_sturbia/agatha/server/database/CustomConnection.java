package com.github.sudo_sturbia.agatha.server.database;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * CustomConnection is a wrapper around Connection object to be used
 * by a Connector. Its purpose is to facilitate closing of connections.
 * <p>
 * All methods except <code>close</code> are simple delegations.
 */
public class CustomConnection implements Connection
{
    /** A database connection. */
    private final Connection connection;

    /**
     * PoolConnection's constructor.
     *
     * @param connection an instantiated connection object.
     * @throws IllegalArgumentException if connection is null.
     */
    public CustomConnection(final Connection connection) throws IllegalArgumentException
    {
        if (connection == null)
        {
            throw new IllegalStateException("Connection not instantiated.");
        }

        this.connection = connection;
    }

    @Override
    public void close() throws SQLException
    {
        // Return connection without a wrapper
        ConnectorBuilder.get().close(this.connection);
    }

    @Override
    public Statement createStatement() throws SQLException
    {
        return this.connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String s) throws SQLException
    {
        return this.connection.prepareStatement(s);
    }

    @Override
    public CallableStatement prepareCall(String s) throws SQLException
    {
        return this.connection.prepareCall(s);
    }

    @Override
    public String nativeSQL(String s) throws SQLException
    {
        return this.connection.nativeSQL(s);
    }

    @Override
    public void setAutoCommit(boolean b) throws SQLException
    {
        this.connection.setAutoCommit(b);
    }

    @Override
    public boolean getAutoCommit() throws SQLException
    {
        return this.connection.getAutoCommit();
    }

    @Override
    public void commit() throws SQLException
    {
        this.connection.commit();
    }

    @Override
    public void rollback() throws SQLException
    {
        this.connection.rollback();
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        return this.connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException
    {
        return this.connection.getMetaData();
    }

    @Override
    public void setReadOnly(boolean b) throws SQLException
    {
        this.connection.setReadOnly(b);
    }

    @Override
    public boolean isReadOnly() throws SQLException
    {
        return this.connection.isReadOnly();
    }

    @Override
    public void setCatalog(String s) throws SQLException
    {
        this.connection.setCatalog(s);
    }

    @Override
    public String getCatalog() throws SQLException
    {
        return this.connection.getCatalog();
    }

    @Override
    public void setTransactionIsolation(int i) throws SQLException
    {
        this.connection.setTransactionIsolation(i);
    }

    @Override
    public int getTransactionIsolation() throws SQLException
    {
        return this.connection.getTransactionIsolation();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        return this.connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        this.connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int i, int i1) throws SQLException
    {
        return this.connection.createStatement(i, i1);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1) throws SQLException
    {
        return this.connection.prepareStatement(s, i, i1);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1) throws SQLException
    {
        return this.connection.prepareCall(s, i, i1);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        return this.connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException
    {
        this.connection.setTypeMap(map);
    }

    @Override
    public void setHoldability(int i) throws SQLException
    {
        this.connection.setHoldability(i);
    }

    @Override
    public int getHoldability() throws SQLException
    {
        return this.connection.getHoldability();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException
    {
        return this.connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String s) throws SQLException
    {
        return this.connection.setSavepoint(s);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException
    {
        this.connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
        this.connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int i, int i1, int i2) throws SQLException
    {
        return this.connection.createStatement(i, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i, int i1, int i2) throws SQLException
    {
        return this.connection.prepareStatement(s, i, i1, i2);
    }

    @Override
    public CallableStatement prepareCall(String s, int i, int i1, int i2) throws SQLException
    {
        return this.connection.prepareCall(s, i, i1, i2);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int i) throws SQLException
    {
        return this.connection.prepareStatement(s, i);
    }

    @Override
    public PreparedStatement prepareStatement(String s, int[] ints) throws SQLException
    {
        return this.connection.prepareStatement(s, ints);
    }

    @Override
    public PreparedStatement prepareStatement(String s, String[] strings) throws SQLException
    {
        return this.connection.prepareStatement(s, strings);
    }

    @Override
    public Clob createClob() throws SQLException
    {
        return this.connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException
    {
        return this.connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException
    {
        return this.connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException
    {
        return this.connection.createSQLXML();
    }

    @Override
    public boolean isValid(int i) throws SQLException
    {
        return this.connection.isValid(i);
    }

    @Override
    public void setClientInfo(String s, String s1) throws SQLClientInfoException
    {
        this.connection.setClientInfo(s, s1);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException
    {
        this.connection.setClientInfo(properties);
    }

    @Override
    public String getClientInfo(String s) throws SQLException
    {
        return this.connection.getClientInfo(s);
    }

    @Override
    public Properties getClientInfo() throws SQLException
    {
        return this.connection.getClientInfo();
    }

    @Override
    public Array createArrayOf(String s, Object[] objects) throws SQLException
    {
        return this.connection.createArrayOf(s, objects);
    }

    @Override
    public Struct createStruct(String s, Object[] objects) throws SQLException
    {
        return this.connection.createStruct(s, objects);
    }

    @Override
    public void setSchema(String s) throws SQLException
    {
        this.connection.setSchema(s);
    }

    @Override
    public String getSchema() throws SQLException
    {
        return this.connection.getSchema();
    }

    @Override
    public void abort(Executor executor) throws SQLException
    {
        this.connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int i) throws SQLException
    {
        this.connection.setNetworkTimeout(executor, i);
    }

    @Override
    public int getNetworkTimeout() throws SQLException
    {
        return this.connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException
    {
        return this.connection.unwrap(aClass);
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException
    {
        return this.connection.isWrapperFor(aClass);
    }
}
