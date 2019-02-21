package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONObject;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class GetProductInfoBatchCommand extends HystrixObservableCommand<ProductInfo> {

    private String[] productIds ;

    public GetProductInfoBatchCommand (String[] productIds) {
        super(HystrixCommandGroupKey.Factory.asKey("GetProductInfoGroup"));
        this.productIds = productIds;
    }

    @Override
    protected Observable<ProductInfo> construct() {
        return Observable.create(new Observable.OnSubscribe<ProductInfo>() {
            @Override
            public void call(Subscriber<? super ProductInfo> subscriber) {
                try{
                        for (String productId:productIds){
                            String url = "http://127.0.0.1:8082/getProductInfo?productId="+productId;
                            String response = HttpClientUtils.sendGetRequest(url);
                            ProductInfo productInfo =  JSONObject.parseObject(response,ProductInfo.class);
                            subscriber.onNext(productInfo);
                        }
                        subscriber.onCompleted();
                }catch (Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
