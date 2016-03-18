package cn.zxw.hive.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 创建对Hive的Jdbc连接，Prototype类型
 * Created by doge on 15-4-28.
 */
public class HiveConnectorPrototype {
    /**
     * 创建多例的Connector
     *
     * @return Connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(HiveConnectorMetadata.DRIVER_NAME);
        return DriverManager.getConnection(HiveConnectorMetadata.CONNECT_URL, HiveConnectorMetadata.CONNECT_USER, HiveConnectorMetadata.CONNECT_PWD);
    }
}
