package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求异步处理的service
 */
@Service("requestAsyncProcessService")
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {
    @Override
    public void process(Request request) {
        try{
            //做请求路由，根据每个请求的商品id，路由到对应的内存队列中去
            ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());

            //将请求放入队列中，完成路由操作
            queue.put(request);
        }catch (Exception e){
            e.printStackTrace();
        }



    }


    /**
     * 获取路由到的内存队列
     * @param productId 商品id
     * @return
     */
    public ArrayBlockingQueue<Request> getRoutingQueue(Integer productId){
        RequestQueue requestQueue = RequestQueue.getInstance();
        String key = String.valueOf(productId);
        int h;
        int hash =  (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        int index = (requestQueue.getQueueSize()-1)&hash;

        System.out.println("===============日志==============；路由内存队列，商品id="+productId+",队列索引="+index );

        return requestQueue.getQueue(index);
    }
}
