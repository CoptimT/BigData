package com.zxw.auto.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.zxw.auto.elasticsearch.bean.Car;
import com.zxw.auto.elasticsearch.utl.ESUtil;

/**
 * ES单个文档增删改查
 */
public class ESClientCurd {
	
	public static void main(String[] args) {
		TransportClient client = null;
		try {
			client = ESUtil.getTransportClient();
			//1.add
			//addDocument(client);
			//2.delete
			delDocument(client);
			//3.update
			//updateDocument(client);
			upsertDocument(client);
			//4.get
			getDocument(client);
			//searcher(client);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(client != null) {
				client.close();
			}
		}
	}
	
	public static void addDocument(TransportClient client) {
		//单条添加
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("id","1");
		json.put("factory","雪铁龙(进口)");
		json.put("brand","雪铁龙");
		json.put("series","雪铁龙C4");
		IndexResponse response = client.prepareIndex(ESUtil.indexName, ESUtil.typeName)
		.setSource(json)
		.setId(json.get("id").toString())//自己设置id，也可以使用ES自带的，但是看文档说，ES的会因为删除id发生变动。
		.execute()
		.actionGet();
		System.out.println(response.getResult());
	}
	
	public static void delDocument(TransportClient client) {
		DeleteResponse response=client.prepareDelete(ESUtil.indexName, ESUtil.typeName, "1").execute().actionGet();
		System.out.println(response.getResult());
	}
	/**
	 * 1.更新信息
	 * 2.为已有的文档添加新的字段  
	 */
	public static void updateDocument(TransportClient client) {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("id","1");
		json.put("factory","雪铁龙(进口)11");
		json.put("brand","雪铁龙");
		json.put("series","雪铁龙C4");
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index(ESUtil.indexName).type(ESUtil.typeName).id("1").doc(json);
		UpdateResponse response=client.update(updateRequest).actionGet();
		//UpdateResponse response=client.prepareUpdate(ESUtil.indexName, ESUtil.typeName, "1").setDoc(json).get();
		System.out.println(response.getResult());
	}
	
	/**
	 * upsert：在使用update方法时，
	 *   a:针对文档不存在的情况时,做出index数据的操作,update无效;
	 *   b:如果文档存在,那么index数据操作无效,update有效;
	 */
	public static void upsertDocument(TransportClient client) throws IOException {
		Map<String, Object> json = new HashMap<String, Object>();
		json.put("factory","雪铁龙(进口)11");
		json.put("brand","雪铁龙11");
		json.put("series","雪铁龙C4");
		//先构建一个IndexRequest  
        IndexRequest indexRequest = new IndexRequest(ESUtil.indexName,ESUtil.typeName,"1");  
        indexRequest.source(json);  
        //再构建一个UpdateRequest，并用IndexRequest关联  
        UpdateRequest updateRequest = new UpdateRequest(ESUtil.indexName,ESUtil.typeName,"1");  
        updateRequest.doc(XContentFactory.jsonBuilder()
                .startObject()  
                .field("series","love")  
                .endObject()  
                ).upsert(indexRequest);  
        UpdateResponse response=client.update(updateRequest).actionGet();
        System.out.println(response.getResult());
	}
	
	public static void getDocument(TransportClient client) {
		GetResponse getResponse = client.prepareGet(ESUtil.indexName,ESUtil.typeName,"1").get();  
        System.out.println(getResponse.getSourceAsString());  
	}
	
	/**
     * 执行搜索
     */
    public static List<Car> searcher(TransportClient client){
    	QueryBuilder queryBuilder = QueryBuilders.matchQuery("factory", "4");
    	//QueryBuilder queryBuilder = QueryBuilders.matchQuery("brand", "4");
    	//QueryBuilder queryBuilder = QueryBuilders.matchQuery("series", "41");
        List<Car> list = new ArrayList<Car>();
        SearchResponse searchResponse = client.prepareSearch(ESUtil.indexName).setTypes(ESUtil.typeName)
        		.setQuery(queryBuilder)
		        .execute().actionGet();
        SearchHits hits = searchResponse.getHits();
        System.out.println("查询到记录数=" + hits.getTotalHits());
        SearchHit[] searchHists = hits.getHits();
        if(searchHists.length>0){
            for(SearchHit hit:searchHists){
            	String factory = (String)hit.getSource().get("factory");
                String brand =  (String) hit.getSource().get("brand");
                String series =  (String) hit.getSource().get("series");
                Car car=new Car(factory, brand, series);
                System.out.println(obj2Json(car));
                list.add(car);
            }
        }
        return list;
    }
    
    public static String obj2Json(Car car){
        String jsonData = null;
        try {
            //使用XContentBuilder创建json数据
            XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
            jsonBuild.startObject()
                    .field("factory", car.getFactory())
                    .field("brand", car.getBrand())
                    .field("series", car.getSeries())
                    .endObject();
            jsonData = jsonBuild.string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }
}
