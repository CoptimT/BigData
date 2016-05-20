package action.hadoop.chapter6.section6;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil {
	private static final String hiveDriverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
	private static final String hiveURL = "jdbc:hive://192.168.56.128:50000/default";
	private static Connection hiveConn=null;
	private static final String mysqlDriverName = "com.mysql.jdbc.Driver";
	private static final String mysqlURL = "jdbc:mysql://10.3.4.3:3306/test?characterEncoding=UTF-8";
	private static Connection mysqlConn=null;
	
	private ConnectionUtil() {
		super();
	}

	public static Connection getHiveConnection() {
		if(hiveConn==null){
			try {
				Class.forName(hiveDriverName);
				hiveConn=DriverManager.getConnection(hiveURL, "root", "zxw");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			} 
		}
		return hiveConn;
	}
	
	public static Connection getMysqlConnection() {
		if(mysqlConn==null){
			try {
				Class.forName(mysqlDriverName);
				mysqlConn=DriverManager.getConnection(mysqlURL, "root", "zxw");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			} 
		}
		return mysqlConn;
	}
	
	public static Statement createStatement(Connection conn) throws SQLException{
		Statement statement=null;
		if(conn!=null){
			statement=conn.createStatement();
		}
		return statement;
	}
	
	public static void closeHiveConn() throws SQLException{
		if(hiveConn!=null){
			hiveConn.close();
		}
	}
	
	public static void closeMysqlConn() throws SQLException{
		if(mysqlConn!=null){
			mysqlConn.close();
		}
	}
	
	public static void closeStatement(Statement statement) throws SQLException{
		if(statement!=null){
			statement.close();
		}
	}
	
	public static ResultSet execQuery(Statement statement,String sql) throws SQLException{
		ResultSet rs=null;
		if(statement!=null){
			rs=statement.executeQuery(sql);
		}
		return rs;
	}
}
