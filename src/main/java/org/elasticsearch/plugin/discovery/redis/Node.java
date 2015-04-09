package org.elasticsearch.plugin.discovery.redis;


public class Node {
	
	private String ip;
	private Integer port;
	private String name;
	
	
	public Node(String ip, Integer port, String name) {
		this.ip = ip;
		this.port = port;
		this.name = name;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
