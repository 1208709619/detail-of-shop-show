package com.roncoo.eshop.product.ha.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ProductController {

    @RequestMapping("/getProductInfo")
    @ResponseBody
    public String getProductInfo(Long productId){
        String productInfoJSON = "{\"id\": "+productId+", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1,\"modifiedTime\":\"2018-12-07 12:01:00\",\"cityId\":1,\"brandId\": 1}";
        return productInfoJSON;
    }

    @RequestMapping("/getProductInfos")
    @ResponseBody
    public String getProductInfo(String productIds){
        JSONArray jsonArray = new JSONArray();

        for(String productId:productIds.split(",")){
            String productInfoJSON = "{\"id\": "+productId+", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1,\"modifiedTime\":\"2018-12-07 12:01:00\",\"cityId\":1,\"brandId\": 1}";
            jsonArray.add(JSONObject.parse(productInfoJSON));
        }
        return jsonArray.toJSONString();
    }
}
