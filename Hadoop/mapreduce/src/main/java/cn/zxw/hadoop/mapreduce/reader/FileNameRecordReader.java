package cn.zxw.hadoop.mapreduce.reader;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

public class FileNameRecordReader extends RecordReader<Text, Text> {
        String FileName;
        LineRecordReader lrr = new LineRecordReader();

        public Text getCurrentKey() throws IOException {
            return new Text("(" + FileName + "@" + lrr.getCurrentKey() + ")");
        }

        public Text getCurrentValue() throws IOException, InterruptedException {
        	return lrr.getCurrentValue();
        }

        public void initialize(InputSplit arg0, TaskAttemptContext arg1)
                throws IOException {
            lrr.initialize(arg0, arg1);
            FileName = ((FileSplit) arg0).getPath().getName();
        }

		@Override
		public boolean nextKeyValue() throws IOException, InterruptedException {
			return lrr.nextKeyValue();
		}

		@Override
		public float getProgress() throws IOException, InterruptedException {
			return lrr.getProgress();
		}

		@Override
		public void close() throws IOException {
			lrr.close();
		}
}