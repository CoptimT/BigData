package com.rtmap.storm.bolt;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("rawtypes")
public class WordCounter implements IRichBolt{

	private static final long serialVersionUID = -5738115575547592889L;
	private static final Logger LOG = LoggerFactory.getLogger(WordCounter.class);
	private OutputCollector collector;
	private Integer id = 0;
	private String name = null;
	private Map<String,Integer> counters;
    private static Integer count = 0;
	
	@Override
	public void prepare(Map stormConf, TopologyContext context,	OutputCollector collector) {
		this.collector = collector;
		this.counters = new HashMap<String, Integer>();
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
	}

	@Override
	public void execute(Tuple input) {
		String str=input.getString(0);
        if(!counters.containsKey(str)){
            this.counters.put(str,1);
        }else{
            Integer c = counters.get(str) + 1;
            counters.put(str,c);
        }
        LOG.info("Recieve word: " + str);
        System.out.println(str);
        print();
        collector.ack(input);
	}

	@Override
	public void cleanup() {
        System.out.println("-- 单词数 ["+name+":"+id+"] --");
        for(Map.Entry<String,Integer> entry : counters.entrySet()){
            System.out.println(entry.getKey()+": "+entry.getValue());
        }
	}
	
	private void print(){
		if(count++ > 10){
        	count=0;
			System.out.println("-- 单词数 ["+name+":"+id+"] --");
	        for(Map.Entry<String,Integer> entry : counters.entrySet()){
	            System.out.println(entry.getKey()+": "+entry.getValue());
	        }
		}
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO 
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO 
		return null;
	}

}
