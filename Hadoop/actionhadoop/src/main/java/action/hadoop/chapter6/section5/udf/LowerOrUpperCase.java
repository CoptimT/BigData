package action.hadoop.chapter6.section5.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

public class LowerOrUpperCase extends UDF{
	private final static String LOWERTOUPPER="a2A";
	private final static String UPPERTOLOWER="A2a";
	
	public Text evaluate(Text text,String lowerOrUpper) {
		if(text==null){
			return null;
		}
		if(LOWERTOUPPER.equals(lowerOrUpper)){
			return new Text(text.toString().toUpperCase());
		}else if(UPPERTOLOWER.equals(lowerOrUpper)){
			return new Text(text.toString().toLowerCase());
		}
		return null;
	}
}
