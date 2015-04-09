package org.elasticsearch.discovery;

import org.elasticsearch.discovery.Discovery;
import org.elasticsearch.discovery.zen.ZenDiscoveryModule;

public class RedisDiscoveryModule extends ZenDiscoveryModule {

	@Override
	protected void bindDiscovery() {
		bind(Discovery.class).to(RedisDiscovery.class).asEagerSingleton();
	}

	
	
}
