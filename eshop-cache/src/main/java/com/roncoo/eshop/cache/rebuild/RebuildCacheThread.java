package com.roncoo.eshop.cache.rebuild;

import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZooKeeperSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 缓存重建线程
 */
public class RebuildCacheThread implements Runnable {

    private static SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void run() {
        RebuildCacheQueue rebuildCacheQueue =RebuildCacheQueue.getInstance();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");

        while (true) {
            ProductInfo productInfo = rebuildCacheQueue.takeProductInfo();

            ZooKeeperSession zooKeeperSession= ZooKeeperSession.getInstance();
            try {
                zooKeeperSession.acquireDistributedLock(productInfo.getId());

                ProductInfo existedProductInfo = cacheService.getProductInfoFromReidsCache(productInfo.getId());
                if (existedProductInfo!=null){
                    Date date= null;
                    Date existedDate =null;

                    try {
                        date= simpleDateFormat.parse(productInfo.getModifiedTime());
                        existedDate = simpleDateFormat.parse(existedProductInfo.getModifiedTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(date.getTime()-existedDate.getTime()<=0){
                            System.out.println("current date="+productInfo.getModifiedTime()+" is before existed data="+existedDate.getTime());
                            continue;
                    }

                    System.out.println("current date[" + productInfo.getModifiedTime() + "] is after existed date[" + existedProductInfo.getModifiedTime() + "]");
                }else {
                    System.out.println("existed product info is null......");
                }

//                try {
//                    Thread.sleep(60*1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

                cacheService.saveProductInfo2LocalCache(productInfo);
                cacheService.saveProductInfo2ReidsCache(productInfo);

            }finally {
                // 释放分布式锁
                zooKeeperSession.releaseDistributedLock(productInfo.getId());
            }
        }
    }
}
