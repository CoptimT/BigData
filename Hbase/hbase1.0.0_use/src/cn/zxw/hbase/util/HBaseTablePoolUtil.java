package cn.zxw.hbase.util;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTablePool;
import org.apache.hadoop.hbase.client.Table;

/**
 * TablePool方式连接HBase
 * @author zhangxw
 *
 */
@SuppressWarnings("deprecation")
public class HBaseTablePoolUtil {
	public static Configuration conf=HBaseConfiguration.create();
	private static Admin admin=null;
	private static HTablePool tablePool=null;
	
	static{
		tablePool=new HTablePool(conf, 5);
	}
	
	public static Admin getAdmin() throws IOException {
		if(admin==null){
			admin=new HBaseAdmin(conf);
		}
		return admin;
	}
	public static HTablePool getHTablePool(){
		return tablePool;
	}
	public static Table getTable(String tableName) throws IOException {
		if(tableName==null){
			return null;
		}
		return tablePool.getTable(tableName);
	}

	@Override
	protected void finalize() throws Throwable {
		if(admin!=null){
			admin.close();
		}
		if(tablePool!=null){
			tablePool.close();
		}
		super.finalize();
	}
}
