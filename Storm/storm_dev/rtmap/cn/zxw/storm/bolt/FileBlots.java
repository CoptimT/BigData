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
public class FileBlots implements IRichBolt{
	private static final long serialVersionUID = -9009257900893620153L;
	private OutputCollector collector;
	
	public void prepare(Map stormConf, TopologyContext context,OutputCollector collector) {
    	this.collector = collector;
    }
	
    public void execute(Tuple input) {
        String line = input.getString(0);
        List<Tuple> anchors = new ArrayList<Tuple>();
        anchors.add(input);
        String[] arr = line.split("\t");
        if(arr.length >= 7){
        	this.collector.emit(anchors,new Values(arr[1]));
            this.collector.ack(input);
        }
    }
 
    public void cleanup() {
    	System.out.println("FileBlots is going to be shutdown!");
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("record")); 
    }
    
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
 
}
