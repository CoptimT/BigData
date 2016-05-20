package action.hadoop.chapter5.section7.demo572;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.contrib.utils.join.TaggedMapOutput;
import org.apache.hadoop.io.Writable;

public class TaggedRecordWritable extends TaggedMapOutput{

	private Writable writableData;
	
	public TaggedRecordWritable(Writable writableData) {
		super();
		this.writableData = writableData;
	}
	
	public TaggedRecordWritable() {
		super();
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		this.tag.write(out);
		this.writableData.write(out);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.tag.readFields(in);
		this.writableData.readFields(in);
	}

	@Override
	public Writable getData() {
		return this.writableData;
	}

	public Writable getWritableData() {
		return writableData;
	}

	public void setWritableData(Writable writableData) {
		this.writableData = writableData;
	}

}
