package cn.zxw.hadoop.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * app_name 表中获取应用名称，应用包名
 * app_tags 表中获取标签的值
 *
 * 两者要靠包名进行关联同步
 */
public class TypeDataFlag implements WritableComparable<TypeDataFlag> {
	private String value;
	// 标记 0:表示应用名称app_name表   
	// 标记1：表示标签app_tags表
	private int flag; 
	
	public TypeDataFlag() {
		super();
	}

	public TypeDataFlag(String value, int flag) {
		super();
		this.value = value;
		this.flag = flag;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void readFields(DataInput in) throws IOException {
		this.flag = in.readInt();
		this.value = in.readUTF();
		
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(flag);
		out.writeUTF(value);
		
	}

	public int compareTo(TypeDataFlag o) {
		if (this.flag >= o.getFlag()) {
			if (this.flag > o.getFlag()) {
				return 1;
			}
		} else {
			return -1;
		}
		return this.value.compareTo(o.getValue());
	}
}
