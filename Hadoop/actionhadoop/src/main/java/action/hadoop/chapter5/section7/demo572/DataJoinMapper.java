package action.hadoop.chapter5.section7.demo572;

import org.apache.hadoop.contrib.utils.join.DataJoinMapperBase;
import org.apache.hadoop.contrib.utils.join.TaggedMapOutput;
import org.apache.hadoop.io.Text;

public class DataJoinMapper extends DataJoinMapperBase{

	@Override
	protected Text generateGroupKey(TaggedMapOutput aRecord) {
		Text groupKey=(Text) aRecord.getData();
		return groupKey;
	}

	@Override
	protected Text generateInputTag(String inputFile) {
		return new Text(inputFile);
	}

	@Override
	protected TaggedMapOutput generateTaggedMapOutput(Object value) {
		TaggedRecordWritable customer=new TaggedRecordWritable((Text)value);
		//.........
		customer.setTag(inputTag);
		return customer;
	}

}
