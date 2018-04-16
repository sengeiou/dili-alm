package com.dili.sysadmin.sdk.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created by asiamastor on 2017/1/3.
 */

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 权限 redis 缓存配置;
 * 
 * @author asiamastor
 */

@Configuration("manageRedisConfig")
@ConfigurationProperties(prefix = "manage.redis")
@PropertySource({ "classpath:conf/manage-${spring.profiles.active}.properties" })
public class ManageRedisConfig {

	// @Value("${manage.redis.host}")
	private String host;
	// @Value("${manage.redis.port}")
	private Integer port = 6379;

	private Integer database = 0;

	@Bean("manageJedisConnectionFactory")
	public JedisConnectionFactory redisConnectionFactory() {
		JedisConnectionFactory redisConnectionFactory = new JedisConnectionFactory();
		// Defaults
		redisConnectionFactory.setHostName(host);
		redisConnectionFactory.setPort(port);
		redisConnectionFactory.setDatabase(database);
		return redisConnectionFactory;
	}

	/**
	 * redis模板操作类,类似于jdbcTemplate的一个类;
	 * <p>
	 * 虽然CacheManager也能获取到Cache对象，但是操作起来没有那么灵活；
	 * <p>
	 * 这里在扩展下：RedisTemplate这个类不见得很好操作，我们可以在进行扩展一个我们
	 * <p>
	 * 自己的缓存类，比如：RedisStorage类;
	 *
	 * @param redisConnectionFactory
	 *            : 通过Spring进行注入，参数在application.properties进行配置；
	 * @return
	 */
	@Bean("manageRedisTemplate")
	public RedisTemplate<String, String> redisTemplate(
			@Qualifier("manageJedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		// key序列化方式;（不然会出现乱码;）,但是如果方法上有Long等非String类型的话，会报类型转换错误；
		// 所以在没有自己定义key生成策略的时候，以下这个代码建议不要这么写，可以不配置或者自己实现ObjectRedisSerializer
		// 或者JdkSerializationRedisSerializer序列化方式;
		// RedisSerializer<String>redisSerializer = new
		// StringRedisSerializer();//Long类型不可以会出现异常信息;
		// redisTemplate.setKeySerializer(redisSerializer);
		// redisTemplate.setHashKeySerializer(redisSerializer);

		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getDatabase() {
		return database;
	}

	public void setDatabase(Integer database) {
		this.database = database;
	}
}
