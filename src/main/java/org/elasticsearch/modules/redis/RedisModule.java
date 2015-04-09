package org.elasticsearch.modules.redis;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.plugin.discovery.redis.RedisInstanceService;
import org.elasticsearch.plugin.discovery.redis.RedisInstanceServiceImpl;

public class RedisModule extends AbstractModule {

	
	@Override
	protected void configure() {
		bind(RedisInstanceService.class).to(RedisInstanceServiceImpl.class).asEagerSingleton();
	}

}
