package cn.zxw.hive.common;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 提供对Hive的操作
 * Created by doge on 15-4-30.
 */
public class HiveJdbcOperator {
    private static final Logger LOGGER = Logger.getLogger(HiveJdbcOperator.class);

    private static Connection connection = null;
    private static Statement statement = null;

    /**
     * 对Hive库的查询动作
     *
     * @param sql
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static ResultSet executeQuery(String sql) throws SQLException, ClassNotFoundException {
        connection = HiveConnectorFactory.getHiveConnector(HiveConnectorFactory.HiveConnectorType.PROTOTYPE);
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        return resultSet;
    }

    public static void executeAlter(String sql) throws SQLException, ClassNotFoundException {
        if (connection == null) {
            connection = HiveConnectorFactory.getHiveConnector(HiveConnectorFactory.HiveConnectorType.PROTOTYPE);
        }
        if (statement == null) {
            statement = connection.createStatement();
        }
        statement.executeUpdate(sql);
    }

    /**
     * 关闭Hive的连接
     *
     * @throws SQLException
     */
    public static void close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            LOGGER.error("close hive connection error: " + e.getMessage(), e);
        }
    }
}
