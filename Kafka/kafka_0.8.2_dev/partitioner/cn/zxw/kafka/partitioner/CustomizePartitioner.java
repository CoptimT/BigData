package cn.zxw.kafka.partitioner;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

public class CustomizePartitioner implements Partitioner {  
	
    public CustomizePartitioner(VerifiableProperties props) {  
   
    }  
    
    /** 
     * 返回分区索引编号 
     * @param key sendMessage时,输出的partKey
     * @param numPartitions topic中的分区总数 
     * @return 
     */  
    @Override  
    public int partition(Object key, int numPartitions) {  
        System.out.println("key:" + key + "  numPartitions:" + numPartitions);  
        String partKey = (String)key;  
        if ("part2".equals(partKey)){
        	return 2; 
        }
        return 0;  
    }
    
}  