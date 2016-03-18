package cn.zxw.hbase.util.test;

import java.io.IOException;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Table;
import org.junit.Test;

import cn.zxw.hbase.constant.Constants;
import cn.zxw.hbase.util.HBaseConnectUtil;
import cn.zxw.hbase.util.HBaseTablePoolUtil;

@SuppressWarnings("deprecation")
public class UtilTest {
	
	@Test
	public void testAdmin() throws IOException {
		Admin admin=HBaseConnectUtil.getAdmin();
		TableName[] tbns=admin.listTableNames();
		for(TableName tbn:tbns){
			System.out.println(tbn.getNameAsString());
		}
	}
	
	
	@Test
	public void testTablePool() throws IOException {
		//开始表POOL大小
		System.out.println(HBaseTablePoolUtil.getHTablePool().getCurrentPoolSize(Constants.TABLE_NAME));
		Table table=HBaseTablePoolUtil.getTable(Constants.TABLE_NAME);
		System.out.println(table==null?"表不存在":table.getName().getNameAsString());
		HBaseTablePoolUtil.getHTablePool().putTable((HTableInterface) table);
		//放入POOL后
		System.out.println(HBaseTablePoolUtil.getHTablePool().getCurrentPoolSize(Constants.TABLE_NAME));
		System.out.println(HBaseTablePoolUtil.getTable(Constants.TABLE_NAME).getName().getNameAsString());
	}
}
