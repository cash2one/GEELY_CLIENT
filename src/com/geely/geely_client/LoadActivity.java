package com.geely.geely_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;

import cn.jpush.android.api.InstrumentedActivity;

import com.geely.data.GetData;

import com.geely.db.MessageDao;

import com.geely.util.HttpUtil;
import com.geely.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 *
 * <p>Title: 好爸妈客户端</p>
 * <p>Description:数据加载界面</p>
 * <p>创建日期:2013-1-30</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class LoadActivity extends InstrumentedActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading);
        getMessageInfo();
        intentToMain();
    }

    public void intentToMain() {
        //延迟一秒加载频道数据
        new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent intent = new Intent();
                    intent.setClass(LoadActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.zaker_fade_in,
                        R.anim.zaker_push_up_out);
                }
            }, 1000);
    }

    /**
     * 获取服务端未读信息
     */
    private void getMessageInfo() {
        if (HttpUtil.isNetworkAvailable(this)) {
            SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                    Context.MODE_PRIVATE);
            final String userId = sharedPreferences.getString("userId", "");
            Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MessageDao dao = new MessageDao(LoadActivity.this);
                            String result = GetData.getUnreadMetting(userId);
                            String result2 = GetData.getUnreadWarning(userId);
                            JSONObject obj = null;
                            JSONArray array = null;

                            try {
                                if (!StringUtil.isEmpty(result)) {
                                    obj = new JSONObject(result);

                                    if ("200".equals(obj.getString("RESULTCODE"))) {
                                        array = obj.getJSONArray("dataList");

                                        if (array.length() > 0) {
                                            dao.inserMeetingArray(array,
                                                Integer.valueOf(userId));
                                        }
                                    }
                                }

                                if (!StringUtil.isEmpty(result2)) {
                                    obj = new JSONObject(result2);

                                    if ("200".equals(obj.getString("RESULTCODE"))) {
                                        array = obj.getJSONArray("dataList");

                                        if (array.length() > 0) {
                                            dao.inserWarningArray(array,
                                                Integer.valueOf(userId));
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            dao.close();
                        }
                    });
            thread.start();
        }
    }
}
