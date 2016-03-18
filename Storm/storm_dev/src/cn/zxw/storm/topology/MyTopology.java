package cn.zxw.storm.topology;

import cn.zxw.storm.bolt.SplitSentence;
import cn.zxw.storm.bolt.WordCount;
import cn.zxw.storm.spout.KestrelSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

public class MyTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("1", new KestrelSpout()); 
		builder.setBolt("2", new SplitSentence(),2).setNumTasks(2).shuffleGrouping("1"); 
		builder.setBolt("3", new WordCount(), 20).setNumTasks(20).fieldsGrouping("2", new Fields("word")); 
		
		Config conf = new Config(); 
		conf.setDebug(true); 
		conf.setNumWorkers(2); 
		LocalCluster cluster = new LocalCluster(); 
		cluster.submitTopology("test", conf, builder.createTopology()); 
		Utils.sleep(5000); 
		cluster.killTopology("test");
		cluster.shutdown();
	}

}
