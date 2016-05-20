package action.hadoop.chapter6.section5.udaf;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;

public class AverageNum extends UDAF{
	public static class MidResult{
		public int numCount;
		public double totalNum;
	}
	public static class AverageEvaluator implements UDAFEvaluator{
		public MidResult midResult;
		
		public AverageEvaluator() {
			super();
			midResult=new MidResult();
			init();
		}

		@Override
		public void init() {
			midResult.numCount=0;
			midResult.totalNum=0;
		}
		
		public boolean iterate(IntWritable val){
			if (val!= null) {
				midResult.numCount ++;
				midResult.totalNum += val.get();
            }
            return true;
		}
		
		public MidResult terminatePartial(){
			return midResult.numCount==0?null:midResult;
		}
		
		public boolean merge(MidResult result){
			if (result!= null) {
				midResult.numCount += result.numCount;
				midResult.totalNum += result.totalNum;
            }
            return true;
		}
		
		public DoubleWritable terminate(){
			DoubleWritable writable=new DoubleWritable(midResult.totalNum/midResult.numCount);
			return midResult.numCount==0?null:writable;
		}
	}
}
