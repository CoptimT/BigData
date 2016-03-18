package com.rtmap.hive.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 提供对Hive的操作
 */
public class HiveJdbcOperator {
    private Connection connection = null;
    private Statement statement = null;

    public Connection getConnection() throws ClassNotFoundException, SQLException{
    	if (connection == null || connection.isClosed()) {
            connection = HiveConnectorPrototype.getConnection();
        }
    	return connection;
    }
    
    /**
     * 对Hive库的查询动作
     *
     * @param sql
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public ResultSet executeQuery(String sql) throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            connection = HiveConnectorPrototype.getConnection();
        }
        if (statement == null || statement.isClosed()) {
            statement = connection.createStatement();
        }
        return statement.executeQuery(sql);
    }

    /**
     * executing update or alter sql
     *
     * @param sql
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public int executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            connection = HiveConnectorPrototype.getConnection();
        }
        if (statement == null || statement.isClosed()) {
            statement = connection.createStatement();
        }
        return statement.executeUpdate(sql);
    }

    /**
     * 关闭Hive的连接
     *
     * @throws SQLException
     */
    public void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("close hive connection error: " + e.getMessage());
        }
    }
    
}
