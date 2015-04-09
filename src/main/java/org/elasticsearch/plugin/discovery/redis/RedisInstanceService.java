package org.elasticsearch.plugin.discovery.redis;

import java.util.Set;

public interface RedisInstanceService {

	public Set<Node> getClusterTopology();
	public void addNode(Node node);
	
}
