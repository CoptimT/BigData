package cn.zxw.hbase.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

/**
 * Connection方式连接HBase
 * @author zhangxw
 *
 */
public class HBaseConnectUtil {
	public static Configuration conf=HBaseConfiguration.create();
	public static Connection conn=null;
	private static Admin admin=null;
	private static Table table=null;
	
	static{
		try {
			conn=ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HBaseConnectUtil() {
		super();
	}


	public static Admin getAdmin() throws IOException {
		if(admin==null){
			admin=conn.getAdmin();
		}
		return admin;
	}

	public static Table getTable(String tableName) throws IOException {
		if(tableName==null){
			return null;
		}
		if(table==null){
			table = conn.getTable(TableName.valueOf(tableName));
		}else if(!table.getName().getNameAsString().equals(tableName)){
			table.close();
			table = conn.getTable(TableName.valueOf(tableName));
		}
		return table;
	}


	@Override
	protected void finalize() throws Throwable {
		if(table!=null){
			table.close();
		}
		if(admin!=null){
			admin.close();
		}
		if(conn!=null){
			conn.close();
		}
		super.finalize();
	}
	
	
}
