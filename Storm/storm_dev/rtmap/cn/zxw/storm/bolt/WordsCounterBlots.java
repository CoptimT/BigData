package cn.zxw.storm.bolt;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

@SuppressWarnings("rawtypes")
public class WordsCounterBlots implements IRichBolt{
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private Map<String, Integer> counter;
    
    
	public void prepare(Map stormConf, TopologyContext context,OutputCollector collector) {
        this.collector = collector;
        this.counter =new HashMap<String, Integer>();
    }
    
    public void execute(Tuple input) {
        String mac = input.getString(0);
        Integer integer = this.counter.get(mac);
        if(integer !=null){
        	integer += 1;
         }else{
        	 integer = 1;
         }
        this.counter.put(mac, integer);
        this.collector.ack(input);
        System.out.println("result ==> " + mac + " -- " + integer);
    }
 
    public void cleanup() {
    	this.counter.clear();
    	System.out.println("FileBlots is going to be shutdown!");
    }
 
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
          
    }
 
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
 
}