package com.rtmap.hive.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 创建对Hive的Jdbc连接，Prototype类型
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
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        return DriverManager.getConnection(
        		//"jdbc:hive2://r1s1:2181,r1s2:2181,r1s3:2181/;serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2", 
        		"jdbc:hive2://r1s1:10000/rtmap", 
        		"hive",
        		"");
    }
}
