package org.projectx.elasticsearch;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * A {@link FactoryBean} implementation used to create a {@link Client}
 * element which connects remotely to a cluster.
 * <p>
 * The lifecycle of the underlying {@link Client} instance is tied to
 * the lifecycle of the bean via the {@link #destroy()} method which calls
 * {@link Client#close()}
 * 
 * @author Erez Mazor (erezmazor@gmail.com)
 */
public class ElasticsearchTransportClientFactoryBean implements FactoryBean<Client>, InitializingBean, DisposableBean {

	private static Logger logger = LoggerFactory.getLogger(ElasticsearchTransportClientFactoryBean.class);

	private Client client;

	private List<Resource> configLocations;

	private Resource configLocation;

	private Map<String, String> settings;

	private Map<String, Integer> transportAddresses;

	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	public void setConfigLocations(List<Resource> configLocations) {
		this.configLocations = configLocations;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public void setTransportAddresses(final Map<String, Integer> transportAddresses) {
		this.transportAddresses = transportAddresses;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		internalCreateTransportClient();
	}

	private void internalCreateTransportClient() {
		ImmutableSettings.Builder builder = ImmutableSettings.settingsBuilder();
		if (configLocation != null) {
			internalLoadSettings(builder, configLocation);
		}

		if (configLocations != null) {
			for (Resource location : configLocations) {
				internalLoadSettings(builder, location);
			}
		}

		if (this.settings != null) {
			builder.put(this.settings);
		}

		client = new TransportClient(builder.build());

		if (transportAddresses != null) {
			for (final Entry<String, Integer> address : transportAddresses.entrySet()) {
				if (address.getKey() != null && !address.getKey().equals("")) {
					logger.info("Adding transport address: " + address.getKey() + " port: " + address.getValue());
					((TransportClient) client).addTransportAddress(new InetSocketTransportAddress(address.getKey(), address.getValue()));
				}
			}
		}
	}
	
	private void internalLoadSettings(ImmutableSettings.Builder builder, Resource configLocation) {
		try {
			String filename = configLocation.getFilename();
			logger.info("Loading configuration file from: " + filename);

			builder.loadFromStream(filename, configLocation.getInputStream());
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not load settings from configLocation: " + configLocation.getDescription(), e);
		}
	}

	@Override
	public void destroy() throws Exception {
		client.close();
	}

	@Override
	public Client getObject() throws Exception {
		return client;
	}

	@Override
	public Class<TransportClient> getObjectType() {
		return TransportClient.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
