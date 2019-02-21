package com.roncoo.eshop.cache.ha.hystrix.command;


import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.*;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

public class GetProductInfoCommand extends HystrixCommand<ProductInfo> {

    private Long productId;

    public GetProductInfoCommand( Long productId) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfoCommand"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("GetProductInfoPool"))
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                        .withCoreSize(15)
                        .withQueueSizeRejectionThreshold(10))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withCircuitBreakerRequestVolumeThreshold(30)
                        .withCircuitBreakerErrorThresholdPercentage(40)
                        .withCircuitBreakerSleepWindowInMilliseconds(3000)
                )

        );
        this.productId = productId;
    }

    @Override
    protected ProductInfo run() throws Exception {
        //拿到一个商品id
        //调用商品服务的接口，获取商品id对应的商品最新数据
        //用HttpClient去调用商品服务的http接口
        String url = "http://127.0.0.1:8082/getProductInfo?productId="+productId;
        String response = HttpClientUtils.sendGetRequest(url);
        ProductInfo productInfo= JSONObject.parseObject(response,ProductInfo.class);

        Long cityId= productInfo.getCityId();

        GetCityNameCommand getCityNameCommand  = new GetCityNameCommand(cityId);
        String cityName = getCityNameCommand.execute();
        productInfo.setCityName(cityName);

        return productInfo;
    }

//    @Override
//    protected String getCacheKey() {
//        return "product_info_"+productId;
//    }
}
