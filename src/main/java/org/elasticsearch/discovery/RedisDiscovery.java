package org.elasticsearch.discovery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.Version;
import org.elasticsearch.action.admin.cluster.node.stats.NodeStats;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNodeService;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.discovery.DiscoverySettings;
import org.elasticsearch.discovery.zen.ZenDiscovery;
import org.elasticsearch.discovery.zen.ping.ZenPing;
import org.elasticsearch.discovery.zen.ping.ZenPingService;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastZenPing;
import org.elasticsearch.node.settings.NodeSettingsService;
import org.elasticsearch.plugin.discovery.redis.Node;
import org.elasticsearch.plugin.discovery.redis.RedisInstanceService;
import org.elasticsearch.plugin.discovery.redis.RedisUnicastHostsProvider;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

public class RedisDiscovery extends ZenDiscovery {

	private RedisInstanceService redisService;
	private final static Pattern pattern = Pattern.compile("inet\\[(.*)\\]");

	@Inject
	public RedisDiscovery(Settings settings, ClusterName clusterName, ThreadPool threadPool, TransportService transportService, ClusterService clusterService, NodeSettingsService nodeSettingsService,
			ZenPingService pingService, DiscoveryNodeService discoveryNodeService, RedisInstanceService redisService, NetworkService networkService, DiscoverySettings discoverySettings) {
		super(settings, clusterName, threadPool, transportService, clusterService, nodeSettingsService, discoveryNodeService, pingService, Version.CURRENT, discoverySettings);
		this.redisService = redisService;
		logger.debug("Starting Redis Discovery");
		ImmutableList<? extends ZenPing> zenPings = pingService.zenPings();
		UnicastZenPing unicastZenPing = null;
		for (ZenPing zenPing : zenPings) {
			if (zenPing instanceof UnicastZenPing) {
				unicastZenPing = (UnicastZenPing) zenPing;
				break;
			}
		}
		logger.debug("Zenping {}", unicastZenPing);
		if (unicastZenPing != null) {
			unicastZenPing.addHostsProvider(new RedisUnicastHostsProvider(settings, redisService, networkService, transportService));
			pingService.zenPings(ImmutableList.of(unicastZenPing));
		} else {
			logger.warn("failed to apply gce unicast discovery, no unicast ping found");
		}
	}

	@Override
	protected void doStart() throws ElasticsearchException {
		super.doStart();
		TransportAddress address = nodeService().info().getTransport().getAddress().publishAddress();
		Matcher m = pattern.matcher(address.toString());
		if(m.find()){
			String addr = m.group(1);
			Integer port = Integer.valueOf(addr.substring(addr.lastIndexOf(":")+1, addr.length()));
			Node node = new Node(nodeService().info().getNode().getHostAddress(), port, nodeName());
			redisService.addNode(node);
		}
	}

	
	public static void main(String[] args) {
		
		String inet = "inet[/10.0.0.1:9200]";
		Matcher m = pattern.matcher(inet);
		
	}
}
