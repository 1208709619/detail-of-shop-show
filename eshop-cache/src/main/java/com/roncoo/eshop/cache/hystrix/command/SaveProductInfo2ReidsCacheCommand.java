package com.roncoo.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.spring.SpringContext;
import redis.clients.jedis.JedisCluster;
import scala.util.parsing.combinator.testing.Str;

public class SaveProductInfo2ReidsCacheCommand extends HystrixCommand<Boolean> {

    private ProductInfo productInfo;

    public SaveProductInfo2ReidsCacheCommand( ProductInfo productInfo) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60000)
                )
        );
        this.productInfo = productInfo;
    }

    @Override
    protected Boolean run() throws Exception {
        JedisCluster jedisCluster = SpringContext.getApplicationContext().getBean(JedisCluster.class);
        String key = "product_info_" + productInfo.getId();
        jedisCluster.set(key, JSONObject.toJSONString(productInfo));
        return true;
    }

    @Override
    protected Boolean getFallback() {
        return false;
    }
}
