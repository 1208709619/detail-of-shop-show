package com.roncoo.eshop.cache.ha.hystrix.command;

import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.ha.cache.local.LocalCache;

public class GetCityNameCommand extends HystrixCommand<String> {

    private Long cityId;
    public GetCityNameCommand(Long cityId){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GetCityNameGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetCityNameCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetCityNamePool"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                        .withExecutionIsolationSemaphoreMaxConcurrentRequests(15)));
        this.cityId=cityId;
    }

    @Override
    protected String run() throws Exception {
        return LocalCache.getCityName(cityId);
    }
}
