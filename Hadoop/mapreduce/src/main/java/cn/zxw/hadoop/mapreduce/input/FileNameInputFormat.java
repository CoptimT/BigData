package cn.zxw.hadoop.mapreduce.input;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import cn.zxw.hadoop.mapreduce.reader.FileNameRecordReader;

public class FileNameInputFormat extends FileInputFormat<Text,Text>{
	
    public RecordReader<Text,Text> createRecordReader(InputSplit split,TaskAttemptContext context)
    		throws IOException{
        FileNameRecordReader fnrr = new FileNameRecordReader();
        fnrr.initialize(split,context);
        return fnrr;
    }
    
}

