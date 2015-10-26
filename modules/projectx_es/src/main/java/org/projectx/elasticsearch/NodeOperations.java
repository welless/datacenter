package org.projectx.elasticsearch;

import org.elasticsearch.action.ActionResponse;

/**
 * An interface describing common Elasticsearch <code>Node</code> based
 * operations for a given {@link #getIndexName() Index}.
 * 
 * @author Erez Mazor (erezmazor@gmail.com)
 */
public interface NodeOperations {

	/**
	 * Check if the underlying index exists
	 * 
	 * @return true if the index exists
	 */
	boolean indexExists();

	boolean indexExists(String indexName);

	/**
	 * Delete the underlying index (tread lightly, actually deletes the index)
	 */
	void deleteIndex();

	void deleteIndex(final String indexName);

	/**
	 * Refresh the underlying index
	 */
	void refreshIndex();

	void refreshIndex(final String indexName);

	/**
	 * Close the underlying index
	 */
	void closeIndex();

	void closeIndex(final String indexName);

	/**
	 * Flush the underlying index
	 */
	void flushIndex();

	void flushIndex(final String indexName);

	/**
	 * Create a gateway snapshot of the underlying index
	 */
	@Deprecated
	void snapshotIndex();

	@Deprecated
	void snapshotIndex(final String indexName);

	/**
	 * Execute a get using the {@link NodeCallback} on the node
	 * 
	 * @param <T>
	 *            the actual {@link ActionResponse} sub-class
	 * @param callback
	 *            the execution specification
	 * @return the response as returned by the node operation
	 */
	<T extends ActionResponse> T executeGet(final NodeCallback<T> callback);

	/**
	 * Execute a get using the {@link ClusterCallback} on the cluster
	 * 
	 * @param <T>
	 *            the actual {@link ActionResponse} sub-class
	 * @param callback
	 *            the execution specification
	 * @return the response as returned by the cluster operation
	 */
	<T extends ActionResponse> T executeGet(final ClusterCallback<T> callback);

	/**
	 * Execute a get using the {@link ClientCallback} on the client
	 * 
	 * @param <T>
	 *            the actual {@link ActionResponse} sub-class
	 * @param callback
	 *            the execution specification
	 * @return the response as returned by the cllient operation
	 */
	<T extends ActionResponse> T executeGet(final ClientCallback<T> callback);

	/**
	 * Get the name of the underlying index
	 * 
	 * @return the name of the index
	 */
	String getIndexName();

}