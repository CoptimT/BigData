package cn.zxw.hive.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 创建对Hive的Jdbc连接，Singleton类型
 * Created by doge on 15-4-28.
 */
public class HiveConnectorSingleton {
    private static Connection connection = null;

    private HiveConnectorSingleton() {
    }

    /**
     * 创建单例的Connector
     *
     * @return Connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static synchronized Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            Class.forName(HiveConnectorMetadata.DRIVER_NAME);
            connection = DriverManager.getConnection(HiveConnectorMetadata.CONNECT_URL, HiveConnectorMetadata.CONNECT_USER, HiveConnectorMetadata.CONNECT_PWD);
        }
        return connection;
    }
    
    
}
