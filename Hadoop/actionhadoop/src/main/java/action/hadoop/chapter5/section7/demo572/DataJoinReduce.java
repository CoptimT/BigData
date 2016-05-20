package action.hadoop.chapter5.section7.demo572;

import org.apache.hadoop.contrib.utils.join.DataJoinReducerBase;
import org.apache.hadoop.contrib.utils.join.TaggedMapOutput;
import org.apache.hadoop.io.Text;

public class DataJoinReduce extends DataJoinReducerBase{

	@Override
	protected TaggedMapOutput combine(Object[] tags, Object[] values) {
		if(tags.length<2){return null;}
		String joinedData="";
		for(int i=0;i<values.length;i++){
			if(i==0)joinedData+=",";
			TaggedRecordWritable record=(TaggedRecordWritable) values[i];
			String recordLine=((Text)record.getData()).toString();
			String[] tokens=recordLine.split(",", 2);
			if(i==0)joinedData+=tokens[0];
			joinedData+=tokens[1];
		}
		TaggedRecordWritable combineResult=new TaggedRecordWritable(new Text(joinedData));
		combineResult.setTag((Text) tags[0]);
		return combineResult;
	}

}
