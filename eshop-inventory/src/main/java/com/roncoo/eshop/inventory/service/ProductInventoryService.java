package com.roncoo.eshop.inventory.service;

import com.roncoo.eshop.inventory.model.ProductInventory;

public interface ProductInventoryService {

    /**
     * 更新商品库存
     * @param productInventory
     */
    void updateProductInventory(ProductInventory productInventory);

    /**
     * 删除redis中的商品库存的缓存
     * @param productInventory
     */
    void removeProductInventoryCache(ProductInventory productInventory);

    /**
     * 获取redis中的商品库存缓存
     * @param productId
     * @return
     */
    ProductInventory getProductInventoryCache(Integer productId);


    /**
     * 根据商品id查询商品库存
     * @param productId
     * @return
     */
    ProductInventory findProductInventory(Integer productId);

    /**
     * 设置商品库存的缓存
     * @param productInventory
     */
    void setProductInventoryCache(ProductInventory productInventory);
}
