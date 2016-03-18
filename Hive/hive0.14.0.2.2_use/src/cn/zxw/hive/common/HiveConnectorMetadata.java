package cn.zxw.hive.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 连接Hive的元数据
 * Created by doge on 15-4-28.
 */
public class HiveConnectorMetadata {
    private static final Logger logger = Logger.getLogger(HiveConnectorMetadata.class);
    private static final String CONFIG_PATH = "/config11.properties";//change for not found

    private static final String HIVE_JDBC_DRIVER = "hive.jdbc.driver";
    private static final String HIVE_JDBC_URL = "hive.jdbc.url";
    private static final String HIVE_JDBC_USERNAME = "hive.jdbc.username";
    private static final String HIVE_JDBC_PASSWORD = "hive.jdbc.password";

    public static String DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
    public static String CONNECT_URL = "jdbc:hive2://namenode:10000/default";
    public static String CONNECT_USER = "hive";
    public static String CONNECT_PWD = "";

    static {
        try {
            InputStream inputStream = HiveConnectorMetadata.class.getResourceAsStream(CONFIG_PATH);
            if (inputStream == null) {
                logger.warn("hive connector configuration file not found, using default metadata.");
            } else {
                Properties properties = new Properties();
                properties.load(inputStream);

                DRIVER_NAME = properties.getProperty(HIVE_JDBC_DRIVER);
                CONNECT_URL = properties.getProperty(HIVE_JDBC_URL);
                CONNECT_USER = properties.getProperty(HIVE_JDBC_USERNAME);
                CONNECT_PWD = properties.getProperty(HIVE_JDBC_PASSWORD);
            }
        } catch (IOException e) {
            logger.error("load hive connector configuration error: " + e.getMessage(), e);
        }
    }
}
