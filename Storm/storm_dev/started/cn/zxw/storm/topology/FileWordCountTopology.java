package cn.zxw.storm.topology;

import cn.zxw.storm.bolt.WordCounter;
import cn.zxw.storm.bolt.WordNormalizer;
import cn.zxw.storm.spout.WordReader;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class FileWordCountTopology {
	
	public static void main(String[] args) throws InterruptedException {
		// 定义拓扑
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word-reader", new WordReader());
		builder.setBolt("word-normalizer", new WordNormalizer())
				.shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCounter(), 2)
		        .fieldsGrouping("word-normalizer", new Fields("word"));
		
		// 配置
		Config conf = new Config();
		//conf.put("wordsFile", args[0]);
		conf.put("wordsFile", "C:\\Users\\zhangxw\\Desktop\\log\\temp\\words.txt");
		conf.setDebug(false);
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		
		// 运行拓扑
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Topology", conf, builder.createTopology());
		Thread.sleep(1000);
		cluster.shutdown();
	}
	
}
