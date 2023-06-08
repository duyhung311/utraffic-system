package com.hcmut.admin.utraffictest.util;

import com.google.gson.Gson;

public class GsonUtil {
    private static Gson gson;

    public static String objectToJsonString(Object o){
        if(gson == null){
            gson = new Gson();
        }
        return gson.toJson(o);
    }
    public static <T> T JsonToObject(String json, Class<T> cls){
        if(gson == null){
            gson = new Gson();
        }
       return gson.fromJson(json,cls);
    }
}
