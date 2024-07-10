package egovframework.iChat.common.service;

import egovframework.iChat.common.config.RedisConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;

@Service
public class RedisService {

    private static JedisPooled jedis;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    public RedisService() {
        this.jedis = RedisConfiguration.jedisPool();
    }

    public static void test() {
        try {
            // Redis 연산 수행 예제
            String response = jedis.ping();
            LOGGER.info("Redis connected: ", response);

            // 예제: 키-값 설정
            jedis.set("mykey", "myvalue");
            String value = jedis.get("mykey");
            LOGGER.debug("mykey의 값: ", value);
        } catch (Exception e) {
            LOGGER.error("Redis 연산 실패: ", e.getMessage());
        }
    }
}
