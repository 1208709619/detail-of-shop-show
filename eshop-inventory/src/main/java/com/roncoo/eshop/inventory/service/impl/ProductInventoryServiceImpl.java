package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.dao.RedisDAO;
import com.roncoo.eshop.inventory.mapper.ProductInventoryMapper;
import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("productInventoryService")
public class ProductInventoryServiceImpl implements ProductInventoryService {

    @Resource
    private ProductInventoryMapper productInventoryMapper;

    @Resource
    private RedisDAO redisDAO;

    @Override
    public void updateProductInventory(ProductInventory productInventory) {
        productInventoryMapper.updateProductInventory(productInventory);
        System.out.println("===============日志==============；已修改数据库中的库存，商品id="+productInventory.getProductId()+",库存数量="+productInventory.getInventoryCnt() );

    }

    @Override
    public void removeProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:"+productInventory.getProductId();
        redisDAO.delete(key);
        System.out.println("===============日志==============；删除redis的缓存，key="+key);

    }

    @Override
    public ProductInventory getProductInventoryCache(Integer productId) {
        Long inventoryCnt = 0L;

        String key = "product:inventory:"+productId;
        String result =  redisDAO.get(key);
        if(result!=null && !"".equals(result)){
            try{
                inventoryCnt=Long.valueOf(result);
                return  new ProductInventory(productId,inventoryCnt);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;

    }

    @Override
    public ProductInventory findProductInventory(Integer productId) {
        return productInventoryMapper.findProductInventory(productId);
    }

    @Override
    public void setProductInventoryCache(ProductInventory productInventory) {
        String key = "product:inventory:"+productInventory.getProductId();
        redisDAO.set(key,String.valueOf(productInventory.getInventoryCnt()));
        System.out.println("===============日志==============；已更新商品库存的缓存，商品id="+productInventory.getProductId()+",库存数量="+productInventory.getInventoryCnt() );
    }
}
