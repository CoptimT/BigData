package action.hadoop.chapter6.section6;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainClass {
	public final static String sql="create table v3xlog(time string,thread string,grade string,position string,message string,exception string,heap string) partitions by(date string,company string) row format delimited fields terminated by '\001' collection items terminated by '\002' map keys terminated by '\003' lines terminated by '\n' stored as textfile;";
	
	public static void main(String[] args) throws Exception {
		Connection conn=ConnectionUtil.getHiveConnection();
		Statement statement=ConnectionUtil.createStatement(conn);
		//建表
		ConnectionUtil.execQuery(statement, sql);
		//加载数据
		ConnectionUtil.execQuery(statement, "load data local inpath '/hone/connor/userinfo' overwrite into table v3xlog;");
		//查询数据
		ResultSet rs=ConnectionUtil.execQuery(statement, sql);
		//查询数据存入mysql
		
		//关闭statement
		//关闭conn
	}
}
