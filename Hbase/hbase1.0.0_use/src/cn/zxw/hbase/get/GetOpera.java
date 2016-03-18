package cn.zxw.hbase.get;

import java.io.IOException;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import cn.zxw.hbase.util.HBaseConnectUtil;

public class GetOpera {
	
	public Result getRow(String tableName,String rowKey) throws IOException {
		Table table=HBaseConnectUtil.getTable(tableName);
		Get get = new Get(Bytes.toBytes(rowKey));
		Result res = table.get(get);
		return res;
	}
	public String getValue(String tableName,String rowKey,String family,String qualifier) throws IOException {
		Table table=HBaseConnectUtil.getTable(tableName);
		Get get = new Get(Bytes.toBytes(rowKey));
		Result result = table.get(get);
		byte[] b = result.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier));//returns current version of value
		String res=new String(b);
		System.out.println(res);
		return res;
	}
	public void getFamilyVersions(String tbName,String rowKey,String family,String...columns) throws IOException{
		Get get=new Get(Bytes.toBytes(rowKey));
		get.addFamily(Bytes.toBytes(family));
		get.setMaxVersions();//所有版本
		Result rs = HBaseConnectUtil.getTable(tbName).get(get);
		if(rs!=null){
			NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map=rs.getMap();//family,column,datas
			if(map!=null){
				NavigableMap<byte[], NavigableMap<Long, byte[]>> cfAllVersions=map.get(Bytes.toBytes(family));//column,datas
				if(cfAllVersions!=null){
					for(String column:columns){
						NavigableMap<Long, byte[]> versions=cfAllVersions.get(Bytes.toBytes(column));//timestamp,data
						for(Long version:versions.keySet()){
							System.out.println(version+","+new String(versions.get(version)));//print
						}
					}
				}
			}
		}
	}
	
	
}
