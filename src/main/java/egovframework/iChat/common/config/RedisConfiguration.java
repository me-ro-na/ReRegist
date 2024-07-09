package egovframework.iChat.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    public static JedisPool jedisPool() {
        LOGGER.info("JEDIS CONNECTING");
        return new JedisPool("localhost", 6379);
    }
}
