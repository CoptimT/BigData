package com.rtmap.hive;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rtmap.hive.util.DateUtils;
import com.rtmap.hive.util.HDFSUtils;
import com.rtmap.hive.util.HiveJdbcOperator;

/**
 * AddPartition!
 */
public class AddPartition 
{
    public static void main( String[] args )
    {
    	String table = null;
    	String stat_date = null;
    	if(args == null || args.length == 0 || args.length > 2){
    		System.out.println("Paramter Error!");
    		System.out.println("Usage: TableName [StatDate]");
    		return;
    	}else if(args.length == 1){
    		table = args[0];
    	}else if(args.length == 2){
    		table = args[0];
    		stat_date = args[1];
    	}
    	try {
    		if(stat_date == null){
        		stat_date = DateUtils.formatDate(DateUtils.getPreDay(new Date()), "yyyy-MM-dd");
        	}
    		Map<String, String> tbDataPath = new HashMap<String, String>();
    		tbDataPath.put("log_sec_lkxxb", "/bcia-queue/calc/anjian/lkxxb/");
    		tbDataPath.put("log_barcode_record", "/bcia-queue/calc/anjian/barcode/");
    		tbDataPath.put("log_sec_ajxxb", "/bcia-queue/calc/anjian/ajxxb/");
    		tbDataPath.put("ods_position", "/bcia-queue/calc/wifi/lbs/");
    		if(!tbDataPath.containsKey(table)){
    			System.out.println("Paramter Error: Table [" + table + "] not exist!");
    			return;
    		}
    		HiveJdbcOperator operator = new HiveJdbcOperator();
    		try {//  test 1
				operator.getConnection();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    		HDFSUtils utils = new HDFSUtils();
			List<String> names = utils.listHdfsFilenames(tbDataPath.get(table));
			String partition = null;
			String longString = null;
			String sql = null;
			for(String name:names){
				try {
					longString = name;
					if(name.startsWith("-")){
						longString = name.substring(1);
					}
					long timestamp = Long.parseLong(longString);
					partition = DateUtils.formatDate(new Date(timestamp), "yyyy-MM-dd");//HH:mm:ss
					if(stat_date.equals(partition)){
						sql = "alter table " + table + " add IF NOT EXISTS partition (stat_date='" + partition + "',stat_time='" + longString + "') location '" + tbDataPath.get(table) + name + "/'";
						System.out.println(sql);
						//operator.executeUpdate(sql);
						//break;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
