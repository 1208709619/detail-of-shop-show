package com.roncoo.eshop.cache.ha.cache.local;

import java.util.HashMap;
import java.util.Map;

public class LocalCache {

    public static final Map<Long,String> cityMap = new HashMap<>();

    static {
        cityMap.put(1L,"北京");
    }

    public static String getCityName(Long cityId){
        return cityMap.get(cityId);
    }
}
