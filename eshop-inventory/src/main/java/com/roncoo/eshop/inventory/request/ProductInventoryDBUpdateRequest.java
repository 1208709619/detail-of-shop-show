package com.roncoo.eshop.inventory.request;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.service.ProductInventoryService;

public class ProductInventoryDBUpdateRequest implements Request {

    //商品库存
    private ProductInventory productInventory;
    //商品库存service
    private ProductInventoryService productInventoryService;

    public ProductInventoryDBUpdateRequest(ProductInventory productInventory, ProductInventoryService productInventoryService) {
        this.productInventory = productInventory;
        this.productInventoryService = productInventoryService;
    }

    @Override
    public void process() {
        System.out.println("=================日志==============；数据库更新请求开始执行，商品id="+productInventory.getProductId()+",商品库存数量="+productInventory.getInventoryCnt());
        //删除redis缓存
        productInventoryService.   removeProductInventoryCache(productInventory);
        //为了模拟演示先删除了redis中的缓存，然后还没更新数据库的时候，读操作过来了，人工sleep
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        productInventoryService.updateProductInventory(productInventory);
    }

    @Override
    public Integer getProductId() {
        return productInventory.getProductId();
    }
}
