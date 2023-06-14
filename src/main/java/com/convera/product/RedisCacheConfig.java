package com.convera.product;
<<<<<<< HEAD
//
=======

>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
//
//import java.time.Duration;
//
//
//import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.cache.RedisCacheConfiguration;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;
//
//@Configuration
//@EnableCaching
//public class RedisCacheConfig {
<<<<<<< HEAD
//	
//	
//	@Bean
//	public RedisCacheConfiguration cacheConfiguration() {
//	    return RedisCacheConfiguration.defaultCacheConfig()
//	      .entryTtl(Duration.ofMinutes(60))
//	      .disableCachingNullValues()
//	      .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//	}
//	
//	
//	@Bean
//	public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//	    return (builder) -> builder
//	      .withCacheConfiguration("quickQuoteCacheByCustomerId",
//	        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
//	      .withCacheConfiguration("quickQuoteCacheByQuoteId",
//	  	    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))     
//	      .withCacheConfiguration("paymentCache",
//	        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
//	      .withCacheConfiguration("tutorialCache",
//	  	        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
//
//	      .withCacheConfiguration("dashboardCache",
//	  	    RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
//	}
//	
//
//}
=======
//
//
//    @Bean
//    public RedisCacheConfiguration cacheConfiguration() {
//        return RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(5))
//                .disableCachingNullValues()
//                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//    }
//
//
//    @Bean
//    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
//        return (builder) -> builder
//                .withCacheConfiguration("quickQuotesCache",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
//                .withCacheConfiguration("quickQuotesById",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
//                .withCacheConfiguration("paymentCache",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
//                .withCacheConfiguration("dashboardCache",
//                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)));
//    }
//
//
//}

>>>>>>> 3ce2f843ef84df5ca9476f2325af492689de7228
