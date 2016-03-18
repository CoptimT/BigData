package cn.zxw.hbase.get;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import cn.zxw.hbase.util.HBaseConnectUtil;

public class ScanOpera {
	
	public void scan(String tableName, String startRow, String stopRow, String family, String qualifier) throws IOException {
		Table table = HBaseConnectUtil.getTable(tableName);
		Scan scan = new Scan();
		scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
		//scan.setRowPrefixFilter(Bytes.toBytes(ROW_KEY));
		scan.setStartRow(Bytes.toBytes(startRow)); // start key is inclusive
		scan.setStopRow(Bytes.toBytes(stopRow));   // stop key is exclusive
		ResultScanner rs = table.getScanner(scan);
		try {
		  for (Result r = rs.next(); r != null; r = rs.next()) {
			  System.out.println(r.getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier)));
		  }
		} finally {
		  rs.close();//always close the ResultScanner!
		}
	}

}
