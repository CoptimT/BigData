package cn.zxw.hbase.put.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.zxw.hbase.constant.Constants;
import cn.zxw.hbase.put.PutOpera;

public class TestPut{
	private PutOpera opera=null;
	
	@Before
	public void before() throws Exception {
		opera=new PutOpera();
		System.out.println("before");
	}
	@BeforeClass
	public static void beforeClass() throws Exception {
		System.out.println("beforeClass");
	}
	@After
	public void after() throws Exception {
		System.out.println("after");
	}
	@AfterClass
	public static void afterClass() throws Exception {
		System.out.println("afterClass");
	}
	
	@Test
	public void writeRow() throws IOException {
		for(int i=1;i<20;i++){
			Map<String, String> data=new HashMap<String, String>();
			data.put(Constants.COL_USER, "user-"+i);
			data.put(Constants.COL_PASS, "pass-"+i);
			opera.writeRow(Constants.TABLE_NAME, Constants.ROW_KEY_PRE+1, Constants.CF_DEFAULT, data);
		}
	}
	
}
