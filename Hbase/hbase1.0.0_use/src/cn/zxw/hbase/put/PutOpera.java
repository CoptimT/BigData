package cn.zxw.hbase.put;

import java.io.IOException;
import java.util.Map;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import cn.zxw.hbase.util.HBaseConnectUtil;

public class PutOpera {
	
	/**
	 * 插入数据行
	 * @param tableName
	 * @param rowKey
	 * @param cf
	 * @param values
	 * @throws IOException
	 */
	public void writeRow(String tableName,String rowKey,String cf,Map<String,String> values) throws IOException {
		Put put=new Put(Bytes.toBytes(rowKey));
		for(String key:values.keySet()){
			put.addColumn(Bytes.toBytes(cf), Bytes.toBytes(key), Bytes.toBytes(values.get(key)));
		}
		
		Table table=HBaseConnectUtil.getTable(tableName);
		table.put(put);
		System.out.println("插入数据行成功！");
	}
	
	
}
