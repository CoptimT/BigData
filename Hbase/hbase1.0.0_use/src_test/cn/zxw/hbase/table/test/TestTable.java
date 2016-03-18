package cn.zxw.hbase.table.test;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import cn.zxw.hbase.constant.Constants;
import cn.zxw.hbase.table.TableOpera;

public class TestTable{
	private TableOpera opera=null;
	
	@Before
	public void before() throws Exception {
		opera=new TableOpera();
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
	public void createTable() throws IOException {
		opera.createTable(Constants.TABLE_NAME, Constants.CF_DEFAULT);
	}
	@Test
	public void deleteTable() throws IOException {
		opera.deleteTable(Constants.TABLE_NAME);
	}
}
