package com.rtmap.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Constants {
    public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";
    public static final String MYSQL_JDBC_URL = getValue("mysql.jdbc.url");
    public static final String MYSQL_JDBC_USERNAME = getValue("mysql.jdbc.username");
    public static final String MYSQL_JDBC_PASSWORD = getValue("mysql.jdbc.password");
    
    public static String getValue(String key) {
        return Loader.getValue(key);
    }
    
    public static int getValueAsInt(String key) {
        return Integer.valueOf(getValue(key));
    }
    
    static class Loader {
        private static Properties properties = new Properties();
        private static final String CONFIG_PATH = "/config.properties";
        
        static {
            try {
                InputStream inputStream = Constants.class.getResourceAsStream(CONFIG_PATH);
                if (inputStream == null) {
                    System.exit(1);
                }
                properties.load(inputStream);
                inputStream.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public static String getValue(String key) {
            return properties.getProperty(key);
        }
    }
}
