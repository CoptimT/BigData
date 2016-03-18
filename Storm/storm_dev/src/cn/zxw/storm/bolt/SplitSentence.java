package cn.zxw.storm.bolt;

import java.util.Map;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SplitSentence implements IBasicBolt{

	private static final long serialVersionUID = 4957226595861585696L;
	
	@SuppressWarnings("rawtypes")
	public void prepare(Map conf, TopologyContext context) {
		
	}        
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String sentence = tuple.getString(0);          
		for(String word: sentence.split(" ")) {
			collector.emit(new Values(word));           
		}           
	}
	public void cleanup() {
		
	}
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
	public Map<String, Object> getComponentConfiguration() {
		
		return null;
	}
	
} 
