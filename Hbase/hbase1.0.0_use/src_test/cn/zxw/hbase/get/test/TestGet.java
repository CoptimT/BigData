package cn.zxw.hbase.get.test;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.zxw.hbase.constant.Constants;
import cn.zxw.hbase.get.GetOpera;

public class TestGet{
	private GetOpera opera=null;
	
	@Before
	public void before() throws Exception {
		opera=new GetOpera();
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
	public void getFamilyVersions() throws IOException {
		opera.getFamilyVersions(Constants.TABLE_NAME, Constants.ROW_KEY_PRE+1, Constants.CF_DEFAULT, Constants.COL_USER, Constants.COL_PASS);
	}
	
}
