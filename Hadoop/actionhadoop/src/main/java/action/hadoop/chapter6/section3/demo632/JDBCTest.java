package action.hadoop.chapter6.section3.demo632;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.jdbc.HiveConnection;

public class JDBCTest extends TestCase{
	private static final String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
	private static final String URL = "jdbc:hive://192.168.56.128:50000/default";
	private static final String tableName = "testHiveJdbcDriver_Table";
	private static final String tableComment = "Simple table";
	private static final String viewName = "testHiveJdbcDriverView";
	private static final String viewComment = "Simple view";
	private static final String partitionedTableName = "testHiveJdbcDriverPartitionedTable";
	private static final String partitionedColumnName = "partcolabc";
	private static final String partitionedColumnValue = "20090619";
	private static final String partitionedTableComment = "Partitioned table";
	private static final String dataTypeTableName = "testDataTypeTable";
	private static final String dataTypeTableComment = "Table with many column data types";
	//private final HiveConf conf;
	//private final Path dataFilePath;
	//private final Path dataTypeDataFilePath;
	private Connection con;
	private boolean standAloneServer = false;
	
	
	public static void main(String[] args) throws Exception {
		Class.forName(driverName);
		Connection conn=DriverManager.getConnection(URL, "root", "zxw");
		Statement statement=conn.createStatement();
		// drop table. ignore error.
		try {
			statement.executeQuery("drop table " + tableName);
	    } catch (Exception ex) {
	      fail(ex.toString());
	    }
		// create table
	    ResultSet res = statement.executeQuery("create table " + tableName
	        + " (under_col int comment 'the under column', value string) comment '"
	        + tableComment + "'");
	    assertFalse(res.next());
	    
	    
	    
		
		
	}

}
