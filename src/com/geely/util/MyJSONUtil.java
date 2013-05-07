package com.geely.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;

import java.lang.reflect.Type;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>Title: 好爸妈客户端开发</p>
 * <p>Description:json工具类</p>
 * <p>创建日期:2013-1-14</p>
 * @author yuanmingxiang
 * @version 1.0
 * <p>拓维教育-好爸妈项目组</p>;
 * <p>http://www.hbm100.com</p>;
 */
public class MyJSONUtil {
    private static final String TAG = "MyJsonUtil :";
    private static Gson gson = new Gson();

    /**
     * <b>function:</b>json字符串转换成list<map>
     * @author DJL
     */
    public static List readJsonToList(String json) {
        String js = json;
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        List reList = new ArrayList();

        list = gson.fromJson(js, list.getClass());
        System.out.println(list.size());

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            Set<String> set = map.keySet();

            for (Iterator<String> it = set.iterator(); it.hasNext();) {
                String key = it.next();
                System.out.println(key + ":" + map.get(key));
                reList.add(map.get(key));
            }
        }

        return reList;
    }

    /**
     * Json对象集合 转为 ContentValues 对象List
     * @param josn
     * @return
     */
    public static List<ContentValues> readJsonToContentValuesList(String josn,
        String loginname) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String js = josn;
        List<HashMap> list = new ArrayList<HashMap>();
        Type type = new TypeToken<List<HashMap<String, String>>>() {
                }.getType();

        List<ContentValues> reList = new ArrayList<ContentValues>();

        list = gson.fromJson(josn, type);

        for (int i = 0; i < list.size(); i++) {
            IdGenerator ig = new IdGenerator();
            Object obj = list.get(i);
            Map<String, String> map = list.get(i);
            Set<String> set = map.keySet();
            ContentValues cv = new ContentValues();

            for (Iterator<String> it = set.iterator(); it.hasNext();) {
                String key = it.next();

                if (!"id".equals(key)) {
                    cv.put(key, map.get(key));
                } else {
                    cv.put("messageid", map.get(key));
                }
            }

            cv.put("id", ig.getId());
            cv.put("messageid", map.get("id"));
            cv.put("sendflag", "02");
            cv.put("loginname", loginname);
            cv.put("mtime", df.format(new Date()));
            reList.add(cv);
        }

        return reList;
    }

    /**
     * 返回Bundle类型的 用户登录信息
     * @param jsonStr
     */
    public static Bundle getMapByJosn(String jsonStr) {
        Bundle userinfo = new Bundle();

        try {
            JSONObject obj = new JSONObject(new JSONTokener(jsonStr));
            System.out.println("date===" + obj.get("childDate").toString());

            String data = "".equals(obj.get("childDate").toString())
                ? "2012-12-12" : obj.get("childDate").toString();
            String sex = "".equals(obj.get("childSex").toString()) ? "01"
                                                                   : obj.get(
                    "childSex").toString();
            int id = Integer.parseInt(obj.get("uid").toString());
            userinfo.putString("RESULTCODE", obj.get("resultCode").toString());
            userinfo.putInt("USER_ID", id);
            userinfo.putString("BABY_DATA", data);
            userinfo.putString("BABY_SEX", sex);
        } catch (JSONException e) {
        }

        return userinfo;
    }

    /**
     * 返回Bundle类型的 用户登录信息
     * @param jsonStr
     */
    public static List josnToList(String jsonStr) {
        List list = null;

        try {
            list = gson.fromJson(jsonStr, List.class);

            //list = objectMapper.readValue(jsonStr, List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 登录的请求帮助方法
     * @param url
     */
    public static HttpResponse doPost(String url) {
        HttpResponse httpResponse = null;

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            //发出get请求
            httpResponse = httpClient.execute(httpGet);
        } catch (Exception e) {
        }

        return httpResponse;
    }


    /**
     * 将list对象转换为json字符串
     * @param list
     * @return
     */
    public static String writeListToJson(List<Object> list) {
        try {
            String json = gson.toJson(list);

            //String json = objectMapper.writeValueAsString(list);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "list转换为json字符串出错。msg:" + e.getMessage());

            return "";
        }
    }
}
