package com.geely.geely_client;

import android.app.Application;
import android.app.Notification;

import android.content.Context;
import android.content.SharedPreferences;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;


public class GeelyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        if (!"".equals(userId)) {
            JPushInterface.setAliasAndTags(this, userId, null);
        }

        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.drawable.ic_launcher;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL; //设置为自动消失
        builder.notificationDefaults = Notification.DEFAULT_VIBRATE |
            Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS; // 设置为铃声与震动都要
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }
}
