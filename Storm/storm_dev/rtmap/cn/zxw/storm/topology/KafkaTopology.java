package cn.zxw.storm.topology;

import cn.zxw.storm.bolt.FileBlots;
import cn.zxw.storm.bolt.WordsCounterBlots;
import cn.zxw.storm.spout.KafkaSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

public class KafkaTopology {

	public static void main(String[] args) {
		try {
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("KafkaSpout", new KafkaSpout("positionDataTopic")).setNumTasks(1);
			builder.setBolt("MACBolt", new FileBlots()).shuffleGrouping("Kafka	Spout");
			builder.setBolt("words-counter", new WordsCounterBlots(), 2)//executor数目
					.setNumTasks(2)//task数目
					.fieldsGrouping("MACBolt", new Fields("record"));
			
			Config config = new Config();
			config.setNumWorkers(1);//worker数目
			config.setDebug(false);
			
			if (args != null && args.length > 0) {
	            config.setNumWorkers(1);
	            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
	        }else{
	        	LocalCluster cluster = new LocalCluster();
	    		cluster.submitTopology("counter", config, builder.createTopology());
	    		Utils.sleep(1*60*1000);//00:01:00
	    		cluster.killTopology("counter");
	    		cluster.shutdown();
	        }
		} catch (Exception e) {
		  	e.printStackTrace(); 
		}
	}
}