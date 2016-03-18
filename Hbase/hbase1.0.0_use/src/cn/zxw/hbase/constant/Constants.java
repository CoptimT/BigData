package cn.zxw.hbase.constant;

import org.apache.hadoop.hbase.TableName;

public class Constants {
	public final static String TABLE_NAME="tb_hbase_user_test";
	public final static String CF_DEFAULT="cf_default";
	public final static String COL_USER="col_username";
	public final static String COL_PASS="col_password";
	public final static String ROW_KEY_PRE="row_key_";
	
	public final static TableName TBN_OBJ=TableName.valueOf(TABLE_NAME);
	
	public static final byte[] CF_DEFAULT_BYTES = CF_DEFAULT.getBytes();
	public static final byte[] COL_USER_BYTES = COL_USER.getBytes();
}
