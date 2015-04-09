package org.elasticsearch.plugin.discovery.redis;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.elasticsearch.Version;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.network.NetworkService;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.discovery.zen.ping.unicast.UnicastHostsProvider;
import org.elasticsearch.transport.TransportService;

public class RedisUnicastHostsProvider extends AbstractComponent implements UnicastHostsProvider {

	private TimeValue refreshInterval;

	private RedisInstanceService redisService;

	private TransportService transportService;

	private NetworkService networkService;

	@Inject
	public RedisUnicastHostsProvider(Settings settings, RedisInstanceService redisService, NetworkService networkService, TransportService transportService) {
		super(settings);
		this.redisService = redisService;
		this.networkService = networkService;
		this.transportService = transportService;
	}

	@Override
	public List<DiscoveryNode> buildDynamicNodes() {
		logger.debug("Building dynamic nodes from redis service");
		List<DiscoveryNode> discoveryNodes = new ArrayList<>();
		String ipAddress = "";
		try {
			InetAddress inetAddress = networkService.resolvePublishHostAddress(null);
			if (inetAddress != null) {
				ipAddress = inetAddress.getHostAddress();
			}
		} catch (IOException e) {
		}
		logger.debug("This node ip address{} ", ipAddress);
		Set<Node> nodes = redisService.getClusterTopology();

		try {
			logger.debug("Found {} nodes on redis", nodes.size());
			for (Node n : nodes) {
				TransportAddress[] addresses = transportService.addressesFromString(n.getIp() + ":" + n.getPort());
				if (!n.getIp().equals(ipAddress)) {
					discoveryNodes.add(new DiscoveryNode(n.getName(), addresses[0], Version.CURRENT));
				}
			}
		} catch (Exception e) {
		}

		return discoveryNodes;
	}

}
