package com.zxw.auto.elasticsearch;

import java.io.IOException;

import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;

import com.zxw.auto.elasticsearch.utl.ESUtil;

/**
 * ES管理操作
 */
public class ESAdminClient {
	
	public static void main(String[] args) {
		TransportClient client = null;
		try {
			client = ESUtil.getTransportClient();
			AdminClient adminClient=client.admin();
			//1.创建索引
			//createIndex(adminClient);
			//2.创建类型
			createType(adminClient);
			
			//delIndex(adminClient);
			
			delByQuery(client);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(client != null) {
				client.close();
			}
		}
	}

	/**
	 * 创建索引
	 * @param adminClient
	 */
	public static void createIndex(AdminClient adminClient) {
		Settings indexSettings = Settings.builder()
				.put("number_of_shards", 1)
		        .put("number_of_replicas", 1).build();
		//TODO
		//配置默认的分词器IK{"analysis":{"analyzer":{"ik":{"tokenizer":"ik_smart"}}}}
		//默认的分词器为standardAnalyzer
		CreateIndexResponse createIndexResponse=adminClient.indices().prepareCreate(ESUtil.indexName)
				.setSettings(indexSettings).get();
		System.out.println(createIndexResponse.isAcknowledged());
	}
	
	/**
	 * 创建类型，并增加mapping
	 */
	public static void createType(AdminClient adminClient) throws IOException {
		// 使用XContentBuilder创建Mapping
        XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                        .field("properties")
                            .startObject()
                                .field("name")
                                    .startObject()
                                        .field("index", "not_analyzed")
                                        .field("type", "text")
                                    .endObject()
                                .field("interests")
                                    .startObject()
                                        .field("index", "not_analyzed")
                                        .field("type", "string")
                                    .endObject()
                                .field("age")
                                    .startObject()
                                        .field("index", "not_analyzed")
                                        .field("type", "integer")
                                    .endObject()
                            .endObject()
                    .endObject();
        System.out.println(builder.string());
		PutMappingRequest mappingRequest = Requests.putMappingRequest(ESUtil.indexName).type(ESUtil.typeName)
				.source(builder);
		PutMappingResponse response=adminClient.indices().putMapping(mappingRequest).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	/**
	 * 删除索引
	 * @param adminClient
	 */
	public static void delIndex(AdminClient adminClient) {
		DeleteIndexResponse response=adminClient.indices()
				//.prepareDelete(ESUtil.indexName).get();
				.delete(new DeleteIndexRequest(ESUtil.indexName)).actionGet();
		System.out.println(response.isAcknowledged());
	}
	
	/**
	 * 删除类型
	 * 如何删除整个type呢？es是没有提供整个东西的。因为ES是基于Lucene的，Lucene的核心是文档，
	 * 一个索引就是一个文件夹，里面存储都是文档，所以没有type的物理概念。ES里面提供了这样一个概念。
	 * 是一组Field定义相同的文档的集合。那么我们要删除特定的集合的文档，比如一个type下的，怎么做的？
	 * Lucene提供了delete by query的能力
	 * @param client
	 */
	public static void delByQuery(TransportClient client) {
		QueryBuilder builder = QueryBuilders.typeQuery(ESUtil.typeName);//查询整个type
		BulkByScrollResponse response=DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
		                   .source(ESUtil.indexName).filter(builder).execute().actionGet();
		System.out.println(response.getStatus());
	}
}
