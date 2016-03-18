package cn.zxw.storm.spout;

import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class KestrelSpout extends BaseRichSpout{
	private static final long serialVersionUID = -7856287136648322662L;
	private static int count = 0;
	
    private SpoutOutputCollector collector;
    
    private static String[] info = new String[]{
        "spouts can tag messages with ids so that they can be",
        "Emits a new tuple to the specified output stream with the given message ID",
        "When Storm detects that this tuple has been fully processed or has failed",
        "to be fully processed the spout will receive an ack or fail callback respectively",
        "with the messageId as long as the messageId was not null If the messageId was null",
        "Storm will not track the tuple and no callback will be received The emitted values must be ",
        "not track this message so ack and fail will never be called for this tuple"};
    
    Random random=new Random();
    
    @SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }
    
    /**
     * 在SpoutTracker类中被调用，每调用一次就可以向storm集群中发射一条数据（一个tuple元组），该方法会被不停的调用
     */
    @Override
    public void nextTuple() {
    	System.out.println("---------- start ----------");
    	if(count == 0){
    		String msg = info[random.nextInt(info.length)];
            collector.emit(new Values(msg));
    	}
    	Utils.sleep(6000);
    	count++;
    }
    
    /**
     * 定义字段id，该id在简单模式下没有用处，但在按照字段分组的模式下有很大的用处。
     * 该declarer变量有很大作用，我们还可以调用declarer.declareStream();来定义stramId，该id可以用来定义更加复杂的流拓扑结构
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("source"));
    }
    
}
