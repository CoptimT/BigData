package cn.zxw.hive.common;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 创建HiveConnector的工厂类
 * Created by doge on 15-4-28.
 */
public class HiveConnectorFactory {
    /**
     * Hive Connector Type
     */
    public enum HiveConnectorType {
        PROTOTYPE, SINGLETON
    }

    /**
     * 根据参数类型创建Connector
     *
     * @param hiveConnectorType
     * @return Connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getHiveConnector(HiveConnectorType hiveConnectorType) throws SQLException, ClassNotFoundException {
        if (hiveConnectorType == HiveConnectorType.PROTOTYPE) {
            return HiveConnectorFactory.getHiveConnectorPrototype();
        }
        if (hiveConnectorType == HiveConnectorType.SINGLETON) {
            return HiveConnectorFactory.getHiveConnectorSingleton();
        }
        return null;
    }

    /**
     * get hive connector prototype
     *
     * @return Connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getHiveConnectorPrototype() throws SQLException, ClassNotFoundException {
        return HiveConnectorPrototype.getConnection();
    }

    /**
     * get hive connector singleton
     *
     * @return Connection
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getHiveConnectorSingleton() throws SQLException, ClassNotFoundException {
        return HiveConnectorSingleton.getConnection();
    }
}
