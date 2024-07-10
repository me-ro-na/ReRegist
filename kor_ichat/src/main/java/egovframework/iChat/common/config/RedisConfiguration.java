package egovframework.iChat.common.config;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.ConnectionPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    public static JedisPooled jedisPool() {
        LOGGER.info("JEDIS CONNECTING");
        HostAndPort hostAndPort = new HostAndPort("localhost", 6379);
        
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(8);
        poolConfig.setMaxIdle(8);
        poolConfig.setMinIdle(0);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(1));
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(1));
        
        return new JedisPooled(hostAndPort,
    	    DefaultJedisClientConfig.builder()
    	        .socketTimeoutMillis(5000)  // set timeout to 5 seconds
    	        .connectionTimeoutMillis(5000) // set connection timeout to 5 seconds
    	        .build(),
    	    poolConfig
    	);
    }
}
