package action.hadoop.chapter5.section9;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class PersonWritable implements DBWritable,Writable{
	private String name;
	private String phone;
	
	@Override
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(phone);
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		this.name=in.readUTF();
		this.phone=in.readUTF();
	}
	
	@Override
	public void write(PreparedStatement statement) throws SQLException {
		statement.setString(1, this.name);
		statement.setString(2, this.phone);
	}

	@Override
	public void readFields(ResultSet resultSet) throws SQLException {
		this.name=resultSet.getString(1);
		this.phone=resultSet.getString(2);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}
