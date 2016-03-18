package cn.zxw.hbase.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cn.zxw.hbase.constant.Constants;
import cn.zxw.hbase.get.GetOpera;
import cn.zxw.hbase.put.PutOpera;
import cn.zxw.hbase.table.TableOpera;

public class TestFunction {
	private TableOpera opera=null;
	private PutOpera putOpera=null;
	private GetOpera getOpera=null;
	
	@Before
	public void before() throws Exception {
		opera=new TableOpera();
		putOpera=new PutOpera();
		getOpera=new GetOpera();
		System.out.println("----- before finish -----");
	}
	@Test
	public void test() throws Exception {
		opera.deleteTable(Constants.TABLE_NAME);
		opera.createTable(Constants.TABLE_NAME, Constants.CF_DEFAULT);
		for(int i=1;i<20;i++){
			Map<String, String> data=new HashMap<String, String>();
			data.put(Constants.COL_USER, "user-"+i);
			data.put(Constants.COL_PASS, "pass-"+i);
			putOpera.writeRow(Constants.TABLE_NAME, Constants.ROW_KEY_PRE+1, Constants.CF_DEFAULT, data);
		}
		getOpera.getFamilyVersions(Constants.TABLE_NAME, Constants.ROW_KEY_PRE+1, Constants.CF_DEFAULT, Constants.COL_USER, Constants.COL_PASS);
		
	}
	
}
