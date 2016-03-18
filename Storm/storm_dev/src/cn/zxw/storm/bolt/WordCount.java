package cn.zxw.storm.bolt;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordCount implements IBasicBolt {
	
	private static final long serialVersionUID = -2238128827712093293L;
	
	private Map<String, Integer> _counts = new HashMap<String, Integer>();
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context) {
		
	}
	
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String word = tuple.getString(0);
		int count=0;
		if(_counts.containsKey(word)) {
			count = _counts.get(word); 
		}    
		count++; 
		_counts.put(word, count); 
		collector.emit(new Values(word, count));
	}
	
	public void cleanup() { 
		
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) { 
		declarer.declare(new Fields("word", "count")); 
	}
	
	public Map<String, Object> getComponentConfiguration() {
		
		return null;
	}
	
} 
