package org.projectx.elasticsearch;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.search.SearchHit;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchTestBase {

	private static Logger logger = LoggerFactory.getLogger(ElasticsearchTestBase.class);
	
	@Resource
	ClientTemplate clientTemplate;

	@Before
	public void before() {
		try {
			if (!clientTemplate.indexExists()) {
				logger.warn("index is not exists!");
				
				clientTemplate.executeGet(new NodeCallback<CreateIndexResponse>() {
					@Override
					public ActionFuture<CreateIndexResponse> execute(final IndicesAdminClient admin) {
						return admin.create(Requests.createIndexRequest(clientTemplate.getIndexName()));
					}
				});
			}
			logger.info("index exists!");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	@After
	public void after() {
//		clientTemplate.deleteIndex();
//		logger.info("index delete sucessful!");
	}

	protected void refreshIndex() {
		clientTemplate.refreshIndex();
		logger.info("index refresh sucessful!");
	}

	protected void flushIndex() {
		clientTemplate.flushIndex();
		logger.info("index flush sucessful!");
	}
	
	protected void addAliases(final String indexName, final String indexAlias) {
		clientTemplate.executeGet(new NodeCallback<IndicesAliasesResponse>() {
			@Override
			public ActionFuture<IndicesAliasesResponse> execute(final IndicesAdminClient admin) {
				IndicesAliasesRequest request = Requests.indexAliasesRequest();
//				request.addAlias(indexName, indexAlias);
				request.addAlias(indexAlias, indexName);
				return admin.aliases(request);
			}
		});
		logger.info("add aliases successful! indexName: " + indexName + ", indexAlias: " + indexAlias);
	}
	
	protected IndexResponse index(final XContentBuilder content) {
		final IndexResponse response = clientTemplate.executeGet(new ClientCallback<IndexResponse>() {

			@Override
			public ActionFuture<IndexResponse> execute(final Client client) {
				final IndexRequest request = Requests.indexRequest(clientTemplate.getIndexName()).source(content).type("log");
				return client.index(request);
			}
		});
		assertNotNull("response is null", response);
		return response;
	}

	protected <Q> List<SearchHit> search(final String field, final Q value, final int maxResults) {
		return clientTemplate.search(field, value, maxResults);
	}

}
