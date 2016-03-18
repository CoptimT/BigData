package cn.zxw.hbase.del;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import cn.zxw.hbase.util.HBaseConnectUtil;

public class DeleteOpera {
	
	public void deleteRow(String tablename, String rowKey) throws IOException {
		Delete delete=new Delete(Bytes.toBytes(rowKey));
		Table table=HBaseConnectUtil.getTable(tablename);
		table.delete(delete);
		//table.delete(Arrays.asList(delete));
		System.out.println("删除数据行成功！");
	}
	public void deleteCFs(String tableName, String rowKey,String... cfs) throws IOException {
		Delete delete=new Delete(Bytes.toBytes(rowKey));
		for(String cf:cfs){
			delete.addFamily(Bytes.toBytes(cf));
		}
		Table table=HBaseConnectUtil.getTable(tableName);
		table.delete(delete);
		//table.delete(Arrays.asList(delete));
		System.out.println("删除行列簇数据成功！");
	}
	public void deleteCols(String tableName, String rowKey, String cf, String col) throws IOException {
		Delete delete=new Delete(Bytes.toBytes(rowKey));
		delete.addColumn(Bytes.toBytes(cf), Bytes.toBytes(col));
		
		Table table=HBaseConnectUtil.getTable(tableName);
		table.delete(delete);
		//table.delete(Arrays.asList(delete));
		System.out.println("删除数据列成功！");
	}
}
