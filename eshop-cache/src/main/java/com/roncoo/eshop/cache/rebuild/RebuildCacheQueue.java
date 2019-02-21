package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 */
public class RebuildCacheQueue {


    private ArrayBlockingQueue<ProductInfo> queue = new ArrayBlockingQueue<ProductInfo>(1000);

    public void putProductInfo(ProductInfo productInfo){
        try {
            this.queue.put(productInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public ProductInfo takeProductInfo(){
        try{
            return queue.take();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static class Singleton{
        private static  RebuildCacheQueue  instance;

        static {
            instance=new RebuildCacheQueue();
        }

        public static RebuildCacheQueue getInstance(){
            return instance;
        }

    }


    public static RebuildCacheQueue getInstance(){
        return Singleton.getInstance();
    }

    public static void init(){
        Singleton.getInstance();
    }

}
