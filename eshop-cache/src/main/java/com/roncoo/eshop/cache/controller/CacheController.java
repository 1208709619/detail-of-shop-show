package com.roncoo.eshop.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.roncoo.eshop.cache.hystrix.command.GetProductInfoCommand;
import com.roncoo.eshop.cache.model.ProductInfo;
import com.roncoo.eshop.cache.model.ShopInfo;
import com.roncoo.eshop.cache.prewarm.CachePrewarmThread;
import com.roncoo.eshop.cache.rebuild.RebuildCacheQueue;
import com.roncoo.eshop.cache.service.CacheService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class CacheController {

    @Resource
    private CacheService cacheService;

    @RequestMapping("/testPutCache")
    @ResponseBody
    public String testPutCache(ProductInfo productInfo){
        cacheService.saveLocalCache(productInfo);
        return "success";
    }

    @RequestMapping("/testGetCache")
    @ResponseBody
    public ProductInfo testGetCache(Long id){
         return  cacheService.getLocalCache(id);
    }


    @RequestMapping("/getProductInfo")
    @ResponseBody
    public ProductInfo getProductInfo(Long productId){
        //先从redis中获取数据
        ProductInfo productInfo =cacheService.getProductInfoFromReidsCache(productId);
        if (productInfo!=null){
            System.out.println("============日志==============：从redis缓存中获取商品信息："+productInfo);
        }
        if (null==productInfo){
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            if (productInfo!=null){
                System.out.println("============日志==============：从ehcache缓存中获取商品信息："+productInfo);
            }
        }


        if(productInfo ==null){
            //需要从数据源中重新拉取数据，重建缓存
//            String productInfoJSON = "{\"id\": "+productId+", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1,\"modifiedTime\":\"2018-12-07 12:01:00\"}";
//            productInfo = JSONObject.parseObject(productInfoJSON,ProductInfo.class);
            GetProductInfoCommand getProductInfoCommand= new GetProductInfoCommand(productId);
            productInfo= getProductInfoCommand.execute();
            //将数据推送到内存队列中
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();

            rebuildCacheQueue.putProductInfo(productInfo);
        }

        return productInfo;
    }


    @RequestMapping("/getShopInfo")
    @ResponseBody
    public ShopInfo getShopInfo(Long shopId){
        //先从redis中获取数据
        ShopInfo shopInfo =cacheService.getShopInfoFromReidsCache(shopId);

        if (shopInfo!=null){
            System.out.println("============日志==============：从redis缓存中获取店铺信息："+shopInfo);
        }

        if (null==shopInfo){
            shopInfo = cacheService.getShopInfoFromReidsCache(shopId);
            if (shopInfo!=null){
                System.out.println("============日志==============：从ehcache缓存中获取店铺信息："+shopInfo);
            }
        }

        if(shopInfo ==null){
            //需要从数据源中重新拉取数据，重建缓存
        }
        shopInfo = new ShopInfo();
        shopInfo.setId(2L);
        shopInfo.setGoodCommentRate(1.1);
        shopInfo.setLevel(3);
        shopInfo.setName("iphone品牌店");
        return shopInfo;
    }

    @RequestMapping("/prewarm")
    @ResponseBody
    public void prewarm(){
        new CachePrewarmThread().start();
    }

}
