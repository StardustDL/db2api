package com.faas;

import com.alibaba.fastjson.JSONObject;

public class Utils {
    public static String toJSON(Object obj) {
        return JSONObject.toJSONString(obj);
    }
}
