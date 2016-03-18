package cn.zxw.storm.spout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

@SuppressWarnings("rawtypes")
public class WordReader implements IRichSpout{
	
	private static final long serialVersionUID = 1L;
	
	//private TopologyContext context;
	private SpoutOutputCollector collector;
    private FileReader fileReader;
    private boolean completed = false;
    
	@Override
	public void open(Map conf, TopologyContext context,	SpoutOutputCollector collector) {
		try {
            //this.context = context;
            this.fileReader = new FileReader(conf.get("wordsFile").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file ["+conf.get("wordFile")+"]");
        }
        this.collector = collector;
	}

	@Override
	public void nextTuple() {
		if (completed) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			return;
		}
		String str = null;
		BufferedReader reader = new BufferedReader(fileReader);
		try {
			while ((str = reader.readLine()) != null) {
				this.collector.emit(new Values(str), str);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error reading tuple", e);
		} finally {
			completed = true;
		}
	}

	@Override
	public void ack(Object msgId) {
		System.out.println("Spout OK: " + msgId);
	}

	@Override
	public void fail(Object msgId) {
		System.out.println("Spout FAIL: " + msgId);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("line"));
	}

	@Override
	public void close() {
		// TODO
	}

	@Override
	public void activate() {
		// TODO
	}

	@Override
	public void deactivate() {
		// TODO
	}
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO
		return null;
	}

}
