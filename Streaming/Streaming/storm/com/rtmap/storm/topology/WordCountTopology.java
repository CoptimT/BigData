package com.rtmap.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import com.rtmap.storm.bolt.WordCounter;
import com.rtmap.storm.bolt.WordNormalizer;
import com.rtmap.storm.spout.KafkaSpout;

public class WordCountTopology {
	
	public static void main(String[] args) throws InterruptedException {
		try {
			// 定义拓扑
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("word-reader", new KafkaSpout("TestTopic"));
			builder.setBolt("word-normalizer", new WordNormalizer())
					.shuffleGrouping("word-reader");
			builder.setBolt("word-counter", new WordCounter(), 1)
			        .fieldsGrouping("word-normalizer", new Fields("word"));
			
			// 配置
			Config conf = new Config();
			conf.setDebug(false);
			conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
			
			// 运行拓扑
			if (args != null && args.length > 0) {
			    StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			}else{
				LocalCluster cluster = new LocalCluster();
				cluster.submitTopology("Counter-Topology", conf, builder.createTopology());
				Utils.sleep(1*60*1000);//00:01:00
				cluster.killTopology("Counter-Topology");
				cluster.shutdown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
