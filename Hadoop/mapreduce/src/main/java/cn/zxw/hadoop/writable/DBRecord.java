package cn.zxw.hadoop.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

/**
 * MySQL数据对应的实体
 * @author hadoop
 */
public class DBRecord implements Writable,DBWritable{
	//一级分类
	private String first;
	//二级分类
	private String second;
	//与媒体分类对应的ID
	private int mappingId;

	@Override
	public void readFields(ResultSet rs) throws SQLException {
		this.first = rs.getString(1);
		this.second = rs.getString(2);
		this.mappingId = rs.getInt(3);
	}

	@Override
	public void write(PreparedStatement ps) throws SQLException {
		ps.setString(1, first);
		ps.setString(2, second);
		ps.setInt(3,mappingId);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.first = Text.readString(in);
		this.second = Text.readString(in);
		this.mappingId = in.readInt();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		Text.writeString(out,this.first);
		Text.writeString(out,this.second);
		out.write(this.mappingId);
	}

	@Override
	public String toString() {
		return first.trim() + "_" + second.trim() + "\t" + mappingId;
	}
}
