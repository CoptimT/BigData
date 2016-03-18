package cn.zxw.storm.spout;

import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

/**
 * Spout起到和外界沟通的作用，他可以从一个数据库中按照某种规则取数据，也可以从分布式队列中取任务
 * 
 * @author Administrator
 *
 */
@SuppressWarnings("all")
public class SimpleSpout extends BaseRichSpout{
    //用来发射数据的工具类
    private SpoutOutputCollector collector;
    private static String[] info = new String[]{
        "comaple\t,12424,44w46,654,12424,44w46,654,",
        "lisi\t,435435,6537,12424,44w46,654,",
        "lipeng\t,45735,6757,12424,44w46,654,",
        "hujintao\t,45735,6757,12424,44w46,654,",
        "jiangmin\t,23545,6457,2455,7576,qr44453",
        "beijing\t,435435,6537,12424,44w46,654,",
        "xiaoming\t,46654,8579,w3675,85877,077998,",
        "xiaozhang\t,9789,788,97978,656,345235,09889,",
        "ceo\t,46654,8579,w3675,85877,077998,",
        "cto\t,46654,8579,w3675,85877,077998,",
        "zhansan\t,46654,8579,w3675,85877,077998,"};
    
    Random random=new Random();
    
    /**
     * 初始化collector
     */
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }
    
    /**
     * 在SpoutTracker类中被调用，每调用一次就可以向storm集群中发射一条数据（一个tuple元组），该方法会被不停的调用
     */
    @Override
    public void nextTuple() {
        try {
            String msg = info[random.nextInt(11)];
            // 调用发射方法
            collector.emit(new Values(msg));
            // 模拟等待500ms
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义字段id，该id在简单模式下没有用处，但在按照字段分组的模式下有很大的用处。
     * 该declarer变量有很大作用，我们还可以调用declarer.declareStream();来定义stramId，该id可以用来定义更加复杂的流拓扑结构
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("source")); //collector.emit(new Values(msg));参数要对应
    }

}