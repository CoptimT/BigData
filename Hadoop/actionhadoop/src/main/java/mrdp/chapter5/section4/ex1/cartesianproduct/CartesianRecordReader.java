package mrdp.chapter5.section4.ex1.cartesianproduct;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.join.CompositeInputSplit;
import org.apache.hadoop.util.ReflectionUtils;

public class CartesianRecordReader<K1, V1, K2, V2> implements RecordReader<Text, Text> {
	// Record readers to get key value pairs
	private RecordReader leftRR = null, rightRR = null;
	// Store configuration to re-create the right record reader
	private FileInputFormat rightFIF;
	private JobConf rightConf;
	private InputSplit rightIS;
	private Reporter rightReporter;
	// Helper variables
	private K1 lkey;
	private V1 lvalue;
	private K2 rkey;
	private V2 rvalue;
	private boolean goToNextLeft = true, alldone = false;
	
	public CartesianRecordReader(CompositeInputSplit split, JobConf conf,
			Reporter reporter) throws Exception {
		this.rightConf = conf;
		this.rightIS = split.get(1);
		this.rightReporter = reporter;
		// Create left record reader
		FileInputFormat leftFIF = (FileInputFormat) ReflectionUtils
			.newInstance(Class.forName(conf.get(CartesianInputFormat.LEFT_INPUT_FORMAT)), conf);
		leftRR = leftFIF.getRecordReader(split.get(0), conf, reporter);
		// Create right record reader
		rightFIF = (FileInputFormat) ReflectionUtils.newInstance(Class
			.forName(conf.get(CartesianInputFormat.RIGHT_INPUT_FORMAT)), conf);
		rightRR = rightFIF.getRecordReader(rightIS, rightConf, rightReporter);
		// Create key value pairs for parsing
		lkey = (K1) this.leftRR.createKey();
		lvalue = (V1) this.leftRR.createValue();
		rkey = (K2) this.rightRR.createKey();
		rvalue = (V2) this.rightRR.createValue();
	}
	
	public boolean next(Text key, Text value) throws IOException {
		do {
			// If we are to go to the next left key/value pair
			if (goToNextLeft) {
				// Read the next key value pair, false means no more pairs
				if (!leftRR.next(lkey, lvalue)) {
					// If no more, then this task is nearly finished
					alldone = true;
					break;
				} else {
					// If we aren't done, set the value to the key and set
					// our flags
					key.set(lvalue.toString());
					goToNextLeft = alldone = false;
					// Reset the right record reader
					this.rightRR = this.rightFIF.getRecordReader(
							this.rightIS, this.rightConf,this.rightReporter);
				}
			}
			// Read the next key value pair from the right data set
			if (rightRR.next(rkey, rvalue)) {
				// If success, set the value
				value.set(rvalue.toString());
			} else {
				// Otherwise, this right data set is complete
				// and we should go to the next left pair
				goToNextLeft = true;
			}
			// This loop will continue if we finished reading key/value
			// pairs from the right data set
		} while (goToNextLeft);
		// Return true if a key/value pair was read, false otherwise
		return !alldone;
	}
			
}
