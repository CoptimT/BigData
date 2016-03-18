package cn.zxw.hbase.admin;

import java.io.IOException;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;

import cn.zxw.hbase.util.HBaseConnectUtil;

public class AdminOpera {
	
	public void listTables() throws IOException{
		Admin admin=HBaseConnectUtil.getAdmin();
		TableName[] tbns=admin.listTableNames();
		for(TableName tbn:tbns){
			System.out.println(tbn.getNameAsString());
		}
	}
	
}
