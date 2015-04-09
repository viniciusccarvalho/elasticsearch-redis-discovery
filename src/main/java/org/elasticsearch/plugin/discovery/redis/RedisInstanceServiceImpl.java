package org.elasticsearch.plugin.discovery.redis;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.node.service.NodeService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisInstanceServiceImpl implements RedisInstanceService {
	
	private JedisPool pool;
	private ESLogger logger = Loggers.getLogger(RedisInstanceServiceImpl.class);
	private NodeService nodeService;
	private Settings settings;
	@Inject
	public RedisInstanceServiceImpl(Settings settings, NodeService nodeService){
		logger.debug("Setting up jedis connection pool on {}", settings.get("redis.host"));
		this.pool = new JedisPool(settings.get("redis.host"));
		this.nodeService = nodeService;
		this.settings = settings;
	}
	
	@Override
	public Set<Node> getClusterTopology() {
		Set<Node> nodes = new HashSet<>();
		Jedis jedis = null;
		try {
			
			jedis = pool.getResource();
			Set<String> members = jedis.smembers(ClusterName.clusterNameFromSettings(settings).value());
			for(String member : members){
				Node n = parse(member);
				if(n != null){
					nodes.add(n);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			jedis.close();
		}
		return nodes;
	}

	@Override
	public void addNode(Node node) {
		Jedis jedis = null;
		try {
			jedis = pool.getResource();
			XContentBuilder builder = XContentFactory.jsonBuilder();
			builder.startObject()
				.field("name",node.getName())
				.field("ip",node.getIp())
				.field("port",node.getPort())
				.endObject();
			
			jedis.sadd(settings.get("cluster.name"), builder.string());
		} catch (Exception e) {
			
		}finally {
			jedis.close();
		}
	}

	
	private Node parse(String json){
		Node node = null;
		try {
			Map<String, Object> map = XContentHelper.createParser(new BytesArray(json)).map();
			String ip = String.valueOf(map.get("ip"));
			Integer port = Integer.valueOf(String.valueOf(map.get("port")));
			String name = String.valueOf(map.get("name"));
			node = new Node(ip, port, name);
		} catch (Exception e) {
			
		}
		return node;
	}
	
}
