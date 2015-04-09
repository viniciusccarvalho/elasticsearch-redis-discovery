package org.elasticsearch.plugins.redis;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisLockTest {

	CountDownLatch latch = new CountDownLatch(2);
	
	@Test
	public void lock() throws Exception {
		JedisPool pool = new JedisPool("localhost");
		Worker w1 = new Worker(1000, pool.getResource());
		Worker w2 = new Worker(2000, pool.getResource());
		ExecutorService executor = Executors.newFixedThreadPool(2);
		executor.submit(w1);
		executor.submit(w2);
		latch.await();
		pool.destroy();
	}
	
	
	
	class Worker implements Runnable {
		
		int sleepTime;
		Jedis jedis;
		
		public Worker(int sleepTime, Jedis jedis){
			this.sleepTime = sleepTime;
			this.jedis = jedis;
		}
		
		@Override
		public void run() {
			try {
				String name = Thread.currentThread().getName();
				System.err.println(name + " Sleeping for " + sleepTime);
				Thread.sleep(sleepTime);
				System.err.println(name +" waking");
				Set<String> members =  jedis.smembers("cluster");
				members.add(name);
				jedis.sadd("cluster", name);
				printMembers(members);
				latch.countDown();
			} catch (Exception e) {
				// TODO: handle exception
			}finally{
				jedis.close();
			}
			
		}
		
		private void printMembers(Collection<String> members){
			StringBuilder buffer = new StringBuilder();
			for(String member : members){
				buffer.append("["+member+"]\n");
			}
			System.out.println(buffer.toString());
		}
		
	}
}
