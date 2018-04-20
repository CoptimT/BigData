package com.zxw.auto.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.zxw.auto.elasticsearch.utl.ESUtil;

/**
 * ES批量增删改查
 */
public class ESClientBulkCurd {
	
	public static void main(String[] args) {
		TransportClient client = null;
		try {
			client = ESUtil.getTransportClient();
			//1.add
			//bulkDocument(client);
			//2.delete
			//bulkDel(client);
			//3.update
			//bulkUpdate(client);
			//4.get
			//multiGet(client);
			//5.bulkProcessor
			bulkProcessor(client);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(client != null) {
				client.close();
			}
		}
	}
	
	/**
	 * 批量添加
	 */
	public static void bulkDocument(TransportClient client) {
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
		for (int i = 1; i<=10; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id",i);
			map.put("factory","雪铁龙(进口)"+i);
			map.put("brand","雪铁龙"+i);
			map.put("series","雪铁龙C4"+i);
			bulkRequestBuilder.add(client.prepareIndex(ESUtil.indexName, ESUtil.typeName)
					.setId(map.get("id").toString()).setSource(map));
		}
		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
		//bulkRequestBuilder.request().requests().clear();
		//if (bulkResponse.hasFailures()){
			Iterator<BulkItemResponse> it=bulkResponse.iterator();
			while(it.hasNext()) {
				BulkItemResponse bulkItemResponse=it.next();
				System.out.println(bulkItemResponse.getItemId()+" - "+bulkItemResponse.getResponse().getResult());
			}
		//}
	}
	
	public static void bulkDel(TransportClient client) {
		BulkRequestBuilder bulkRequest = client.prepareBulk();  
		bulkRequest.add(client.prepareDelete(ESUtil.indexName, ESUtil.typeName, "9"));  
		bulkRequest.add(client.prepareDelete(ESUtil.indexName, ESUtil.typeName,"10"));  
		BulkResponse bulkResponse = bulkRequest.get();  
		if(bulkResponse.hasFailures()){
		    System.out.println("bulk error:"+bulkResponse.buildFailureMessage());  
		}
	}
	/**
	 * 批量更新
	 */
	public static void bulkUpdate(TransportClient client) throws IOException {
		BulkRequestBuilder bulkRequest = client.prepareBulk();  
		bulkRequest.add(client.prepareUpdate(ESUtil.indexName, ESUtil.typeName, "1").setDoc(
				XContentFactory.jsonBuilder().startObject()  
		        .field("series","China")//新添加字段  
		        .endObject()));  
		bulkRequest.add(client.prepareUpdate(ESUtil.indexName, ESUtil.typeName, "2").setDoc(
				XContentFactory.jsonBuilder().startObject()  
		        .field("series","hello")//更新字段  
		        .endObject()));
		BulkResponse bulkResponse = bulkRequest.get();  
		if(bulkResponse.hasFailures()){  
		    System.out.println("bulk error:"+bulkResponse.buildFailureMessage());  
		}
	}
	
	public static void multiGet(TransportClient client) {
		MultiGetResponse multiGetResponse = client.prepareMultiGet()
		        .add(ESUtil.indexName,ESUtil.typeName,"1")//指定单个id
		        .add(ESUtil.indexName,ESUtil.typeName,"2","3","4")//指定一个id-list  
		        .add(ESUtil.indexName,ESUtil.typeName,Arrays.asList("5")).get();//指定别的index/type  
		for(MultiGetItemResponse item:multiGetResponse){  
		    GetResponse response = item.getResponse();
		    System.out.println(response.getSourceAsString());  
		}
	}
	
	/**
     * BulkProcessor批量
     */
    public static void bulkProcessor(TransportClient client) throws Exception{
    	BulkProcessor bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener(){
			public void beforeBulk(long executionId, BulkRequest request) {
				//批量执行前做的事情  
    	        System.out.println("bulk api action starting...");
			}
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				 //正常执行完毕后...  
    	        System.out.println("normal:bukl api action ending...");  
			}
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				System.out.println("exception:bukl api action ending...:"+failure.getMessage());  
			}
    	})
    	//设置多种条件，对批量操作进行限制，达到限制中的任何一种触发请求的批量提交  
    	.setBulkActions(10)//设置批量操作一次性执行的action个数，根据请求个数批量提交  
    	//.setBulkSize(new ByteSizeValue(1,ByteSizeUnit.KB))//设置批量提交请求的大小允许的最大值  
    	//.setFlushInterval(TimeValue.timeValueMillis(100))//根据时间周期批量提交请求  
    	//.setConcurrentRequests(1)//设置允许并发请求的数量  
    	//设置请求失败时的补偿措施，重复请求3次  
    	//.setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))  
    	.build();  
    	for(int i=0;i<100;i++){  
    	    bulkProcessor.add(new IndexRequest(ESUtil.indexName,ESUtil.typeName,""+i).source(  
    	            XContentFactory.jsonBuilder().startObject()
    	            .field("name","yuchen"+i)  
    	            .field("interest","love"+i)  
    	            .endObject()));  
    	}
    	bulkProcessor.awaitClose(2, TimeUnit.MINUTES);//释放bulkProcessor资源  
    	System.out.println("load succeed!");
    }
    
}
