package cn.zxw.hbase.table;

import java.io.IOException;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;

import cn.zxw.hbase.util.HBaseConnectUtil;
/**
 * 表操作
 * @author zhangxw
 *
 */
public class TableOpera {
	
	public void createTable(String tableName, String... cfs) throws IOException {
		if(HBaseConnectUtil.getAdmin().tableExists(TableName.valueOf(tableName))){
			System.out.println("表已存在");
		}else{
			HTableDescriptor table = new HTableDescriptor(TableName.valueOf(tableName));
			for(String cf:cfs){
				HColumnDescriptor family=new HColumnDescriptor(cf)
							.setCompressionType(Algorithm.SNAPPY)//设置列簇压缩
							.setMaxVersions(HConstants.ALL_VERSIONS);//版本数
				table.addFamily(family);
			}
			HBaseConnectUtil.getAdmin().createTable(table);
			System.out.println("表创建成功");
		}
	}
	
	public void deleteTable(String tableName) throws IOException {
		HBaseConnectUtil.getAdmin().disableTable(TableName.valueOf(tableName));
		HBaseConnectUtil.getAdmin().deleteTable(TableName.valueOf(tableName));
		System.out.println("删除表成功！");
	}
	
	public void alterTable(String tableName) throws IOException {
		
		/*HBaseConnectUtil.getAdmin().modifyColumn(tableName, descriptor);
		Table table=HBaseConnectUtil.getTable(tableName);
		
		System.out.println("删除表成功！");*/
	}
}
