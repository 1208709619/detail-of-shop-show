package com.roncoo.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

public class GetProductInfoFromReidsCacheCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public GetProductInfoFromReidsCacheCommand( Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                 .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                         .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60000)
                )
        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        JedisCluster jedisCluster = SpringContext.getApplicationContext().getBean(JedisCluster.class);
        String key = "product_info_" + productId;
        String json= jedisCluster.get(key);
        if (json!=null){
            return JSONObject.parseObject(json,ProductInfo.class);
        }else {
            return null;
        }
    }

    @Override
    protected ProductInfo getFallback() {
        return null;
    }
}
