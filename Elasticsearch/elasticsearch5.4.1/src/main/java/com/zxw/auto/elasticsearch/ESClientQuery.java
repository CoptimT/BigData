package com.zxw.auto.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.max.InternalMax;
import org.elasticsearch.search.aggregations.metrics.min.InternalMin;

import com.zxw.auto.elasticsearch.utl.ESUtil;

public class ESClientQuery {

	public static void main(String[] args) {
		TransportClient client = null;
		try {
			client = ESUtil.getTransportClient();
			
			aggregation(client);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(client != null) {
				client.close();
			}
		}
	}
	public static void aggregation(TransportClient client) {
	    SearchResponse response = client.prepareSearch(ESUtil.indexName)
	                            .addAggregation(AggregationBuilders.min("min").field("age"))
	                            .addAggregation(AggregationBuilders.max("max").field("age"))
	                            .get();
	    InternalMin min = response.getAggregations().get("min");
	    InternalMax max=response.getAggregations().get("max");
	    System.out.println(min.getValue());
	    System.out.println(max.getValue());
	}
}
