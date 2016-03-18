package cn.zxw.hive.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.zxw.hive.common.HiveJdbcOperator;

public class QueryDao {

	public static void main(String[] args){
		String sql="show tables";
		try {
			ResultSet rs=HiveJdbcOperator.executeQuery(sql);
			while(rs.next()){
				System.out.println(rs.getString(1));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			HiveJdbcOperator.close();
		}
	}

}
