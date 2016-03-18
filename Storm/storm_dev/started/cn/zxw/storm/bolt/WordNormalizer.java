package cn.zxw.storm.bolt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

@SuppressWarnings("rawtypes")
public class WordNormalizer implements IRichBolt{

	private static final long serialVersionUID = 3942452226877823574L;
	
	private OutputCollector collector;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,	OutputCollector collector) {
		this.collector=collector;
	}

	@Override
	public void execute(Tuple input) {
		String sentence = input.getString(0);
		String[] words = sentence.split(" ");
		for (String word : words) {
			word = word.trim();
			if (!word.isEmpty()) {
				word = word.toLowerCase();
				List<Tuple> anchors = new ArrayList<Tuple>();
				anchors.add(input);
				collector.emit(anchors, new Values(word));
			}
		}
		collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
	
	@Override
	public void cleanup() {
		// TODO 
	}
	
	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO 
		return null;
	}

}
