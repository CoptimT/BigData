package cn.zxw.kylin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class KylinJDBC {
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		Statement state = null;
		ResultSet rs = null;
		try {
			Class.forName("org.apache.kylin.jdbc.Driver");
			conn = DriverManager.getConnection(
					"jdbc:kylin://10.10.25.13:7070/learn_kylin", "ADMIN","KYLIN");
			state = conn.createStatement();
			rs = state.executeQuery("select dt,count(distinct id) from dmp_wanka_hive group by dt");
			System.out.println("PART_DT\tTOTAL_USER");
			while (rs.next()) {
				String dt = rs.getString(1);
				int users = rs.getInt(2);
				System.out.println(dt + "\t" + users);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			state.close();
			conn.close();
		}
	}
}