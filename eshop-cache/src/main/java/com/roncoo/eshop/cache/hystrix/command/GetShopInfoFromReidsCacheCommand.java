package com.roncoo.eshop.cache.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.spring.SpringContext;
import redis.clients.jedis.JedisCluster;

public class GetShopInfoFromReidsCacheCommand extends HystrixCommand<ShopInfo> {

    private  Long shopId;

    public GetShopInfoFromReidsCacheCommand( Long shopId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RedisGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(100)
                        .withCircuitBreakerRequestVolumeThreshold(1000)
                        .withCircuitBreakerErrorThresholdPercentage(70)
                        .withCircuitBreakerSleepWindowInMilliseconds(60000)
                )
        );
        this.shopId = shopId;
    }

    @Override
    protected ShopInfo run() throws Exception {
        JedisCluster jedisCluster = SpringContext.getApplicationContext().getBean(JedisCluster.class);
        String key = "shop_info_" + shopId;
        String json= jedisCluster.get(key);
        if (json!=null){
            return JSONObject.parseObject(json,ShopInfo.class);
        }else {
            return null;
        }
    }

    @Override
    protected ShopInfo getFallback() {
        return  null;
    }
}
