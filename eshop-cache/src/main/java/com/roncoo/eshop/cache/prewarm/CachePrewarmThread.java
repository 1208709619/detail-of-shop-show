package com.roncoo.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.service.CacheService;
import com.roncoo.eshop.cache.spring.SpringContext;
import com.roncoo.eshop.cache.zk.ZooKeeperSession;

/**
 * 缓存预热线程
 */
public class CachePrewarmThread extends Thread {



    @Override
    public void run() {
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext().getBean("cacheService");
        ZooKeeperSession zooKeeperSession=ZooKeeperSession.getInstance();

        //获取storm taskId列表
        String taskidList  =zooKeeperSession.getNodeData("/taskid-list");
        System.out.println("[CachePrewarmThread获取到taskid列表]taskidList="+taskidList);

        if(taskidList!=null && !taskidList.equals("")){
            String[] taskIdSplited = taskidList.split(",");
            for (String taskIdStr:taskIdSplited){
                String taskidLockPath = "/taskid-lock-"+taskIdStr;
                boolean result=  zooKeeperSession.acquireFastFailedDistributedLock(taskidLockPath);
                if(!result){
                    continue;
                }

                String taskidStatusLockPath = "/taskid-status-lock-"+taskIdStr;
                try{
                    zooKeeperSession.acquireDistributedLock(taskidStatusLockPath);
                    String taskidStatus =  zooKeeperSession.getNodeData("/taskid-status-"+taskIdStr);
                    System.out.println("[CachePrewarmThread获取到task的预热状态]taskid="+taskIdStr+",taskidStatus="+taskidStatus);

                    if(taskidStatus.equals("")){
                        String productidList =zooKeeperSession.getNodeData("/task-hot-product-list-"+taskIdStr);
                        System.out.println("[CachePrewarmThread获取到task的热门商品列表]taskid="+taskIdStr+",productidList="+productidList);
                        JSONArray productidJsonArray=JSONArray.parseArray(productidList);
                        for (int i = 0;i<productidJsonArray.size();i++){
                            Long productId = productidJsonArray.getLong(i);
                            String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                            ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                            cacheService.saveProductInfo2LocalCache(productInfo);
                            System.out.println("[CachePrewarmThread将商品数据设置到本地缓存中]productInfo="+productInfo);
                            cacheService.saveProductInfo2ReidsCache(productInfo);
                            System.out.println("[CachePrewarmThread将商品数据设置到redis缓存中]productInfo="+productInfo);
                        }

                        zooKeeperSession.createNode("/taskid-status-"+taskIdStr);
                        zooKeeperSession.setNodeData("/taskid-status-"+taskIdStr, "success");
                    }
                }finally {
                    zooKeeperSession.releaseDistributedLock(taskidStatusLockPath);
                }

                zooKeeperSession.releaseDistributedLock(taskidLockPath);
            }
        }

    }
}
