package com.rtmap.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Get MySql Connection
 */
public class MySqlConnector {
    /**
     * Get MySql Connection
     */
    public static Connection getMySqlConnector() throws ClassNotFoundException, SQLException {
        return getMySqlConnector(Constants.MYSQL_JDBC_URL, Constants.MYSQL_JDBC_USERNAME, Constants.MYSQL_JDBC_PASSWORD);
    }

    public static Connection getMySqlConnector(String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(Constants.MYSQL_JDBC_DRIVER);
        return DriverManager.getConnection(url, username, password);
    }
}
