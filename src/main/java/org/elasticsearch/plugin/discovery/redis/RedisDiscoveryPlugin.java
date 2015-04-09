package org.elasticsearch.plugin.discovery.redis;

import java.util.Collection;

import org.elasticsearch.common.collect.Lists;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.modules.redis.RedisModule;
import org.elasticsearch.plugins.AbstractPlugin;

public class RedisDiscoveryPlugin extends AbstractPlugin {

	private final Settings settings;
	private final ESLogger logger = Loggers.getLogger(RedisDiscoveryPlugin.class);
	
	public RedisDiscoveryPlugin(Settings settings) {
		this.settings = settings;
	}

	@Override
	public String name() {
		return "redis-discovery";
	}

	@Override
	public String description() {
		return "Redis discovery plugin";
	}

	@Override
	public Collection<Class<? extends Module>> modules() {
		Collection<Class<? extends Module>> modules = Lists.newArrayList();
		if(settings.getAsBoolean("redis", true)){
			modules.add(RedisModule.class);
		}
		return modules;
	}

}
