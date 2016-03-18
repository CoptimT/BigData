package com.rtmap.hive.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class LowerOrUpperCase extends UDF {
	private final static String LOWERTOUPPER = "a2A";
	private final static String UPPERTOLOWER = "A2a";

	// 根据需要可自定义多个参数不同的evaluate方法
	public Text evaluate(Text text, String lowerOrUpper) {
		if (text == null) {
			return null;
		}
		if (LOWERTOUPPER.equals(lowerOrUpper)) {
			return new Text(text.toString().toUpperCase());
		} else if (UPPERTOLOWER.equals(lowerOrUpper)) {
			return new Text(text.toString().toLowerCase());
		}
		return null;
	}
	
	//String execAdd = "alter table " + table + " add partition (p_build_id='" + buildId + "', p_date='" + date + "') location '" + path + buildId + "/" + date + "/'";
    //hiveJdbcOperator.executeUpdate(execAdd);
}