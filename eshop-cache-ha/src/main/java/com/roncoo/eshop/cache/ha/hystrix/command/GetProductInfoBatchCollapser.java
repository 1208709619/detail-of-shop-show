package com.roncoo.eshop.cache.ha.hystrix.command;

import com.alibaba.fastjson.JSONArray;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.roncoo.eshop.cache.ha.http.HttpClientUtils;
import com.roncoo.eshop.cache.ha.model.ProductInfo;

import java.util.Collection;
import java.util.List;

public class GetProductInfoBatchCollapser extends HystrixCollapser<List<ProductInfo>,ProductInfo,Long> {

    private Long productId;

    public GetProductInfoBatchCollapser(Long productId) {
        this.productId = productId;
    }


    @Override
    public Long getRequestArgument() {
        return productId;
    }

    @Override
    protected HystrixCommand<List<ProductInfo>> createCommand(Collection<CollapsedRequest<ProductInfo, Long>> requests) {
        return new BatchCommand(requests);
    }

    @Override
    protected void mapResponseToRequests(List<ProductInfo> batchResponse, Collection<CollapsedRequest<ProductInfo, Long>> requests) {
        int count = 0;
        for (CollapsedRequest<ProductInfo, Long> request:requests){
            request.setResponse(batchResponse.get(count++));
        }

    }

    @Override
    protected String getCacheKey() {
        return "product_info_"+productId;
    }

    private static final class BatchCommand extends HystrixCommand<List<ProductInfo>>{

        private final Collection<CollapsedRequest<ProductInfo,Long>> requests;

        protected BatchCommand(Collection<CollapsedRequest<ProductInfo,Long>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ProductInfoService")).andCommandKey(HystrixCommandKey.Factory.asKey("GetProductInfoBatchCommand")));
            this.requests=requests;
        }

        @Override
        protected List<ProductInfo> run() throws Exception {
            StringBuilder paramsBuilder = new StringBuilder("");

            for (CollapsedRequest<ProductInfo,Long> request:requests) {
                paramsBuilder.append(request.getArgument()).append(",");
            }

            String params = paramsBuilder.toString();
            params = params.substring(0,params.length()-1);

            String url = "http://localhost:8082/getProductInfos?productIds="+params;
            String response = HttpClientUtils.sendGetRequest(url);

            List<ProductInfo> productInfoList = JSONArray.parseArray(response,ProductInfo.class);
            return productInfoList;
        }
    }
}
