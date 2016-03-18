package com.rtmap.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlOperator{

    private Connection connection = null;
    private Statement statement = null;

    /**
     * 执行MySql的查询操作，返回结果集
     *
     * @param sql
     * @return
     */
    public ResultSet executeQuery(String sql) throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            connection = MySqlConnector.getMySqlConnector();
        }
        if (statement == null || statement.isClosed()) {
            statement = connection.createStatement();
        }
        return statement.executeQuery(sql);
    }

    /**
     * 执行MySql的更新语句，响应更新状态
     *
     * @param sql
     */
    public int executeUpdate(String sql) throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            connection = MySqlConnector.getMySqlConnector();
        }
        if (statement == null || statement.isClosed()) {
            statement = connection.createStatement();
        }
        return statement.executeUpdate(sql);
    }

    /**
     * 关闭MySql的连接等资源
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
            System.out.println("close mysql connection error: " + e.getMessage());
        }
    }

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
    
}
