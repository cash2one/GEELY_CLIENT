package com.geely.geely_client;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;

import android.os.Bundle;

import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.geely.data.GetData;

import com.geely.db.MessageDao;

import com.geely.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG,
            "onReceive - " + intent.getAction() + ", extras: " +
            printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收Registration Id : " + regId);

            //send the Registration Id to your server...
        } else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "接收UnRegistration Id : " + regId);

            //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(
                    intent.getAction())) {
            Log.d(TAG,
                "接收到推送下来的自定义消息: " +
                bundle.getString(JPushInterface.EXTRA_MESSAGE));
            getMessageInfo(context,
                bundle.getString(JPushInterface.EXTRA_MESSAGE));
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(
                    intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知");

            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(
                    intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");

            //打开自定义的Activity
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();

        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }

        return sb.toString();
    }

    /**
    * 获取服务端未读信息
    */
    private void getMessageInfo(final Context context, final String meesageType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("userId", "");
        Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MessageDao dao = new MessageDao(context);
                        String result = "";
                        String msgType = "";
                        String title = "";
                        String message = "";

                        if ("meeting".equals(meesageType)) {
                            result = GetData.getUnreadMetting(userId);
                            msgType = "通知";
                        } else if ("warning".equals(meesageType)) {
                            result = GetData.getUnreadWarning(userId);
                            msgType = "预警";
                        }

                        JSONObject obj = null;
                        JSONArray array = null;

                        try {
                            if (!StringUtil.isEmpty(result)) {
                                obj = new JSONObject(result);

                                if ("200".equals(obj.getString("RESULTCODE"))) {
                                    array = obj.getJSONArray("dataList");

                                    int length = array.length();

                                    if (length > 0) {
                                        if ("meeting".equals(meesageType)) {
                                            dao.inserMeetingArray(array,
                                                Integer.valueOf(userId));
                                        } else {
                                            dao.inserWarningArray(array,
                                                Integer.valueOf(userId));
                                        }

                                        Notification notification = new Notification();
                                        notification.icon = R.drawable.ic_launcher;
                                        notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
                                        notification.when = System.currentTimeMillis();

                                        if (length == 1) {
                                            title = array.getJSONObject(0)
                                                         .getString("CRUSER") +
                                                "发来一条" + msgType;
                                            message = array.getJSONObject(0)
                                                           .getString("TIME") +
                                                " " +
                                                array.getJSONObject(0)
                                                     .getString("TITLE");
                                        } else if (length > 1) {
                                            title = "G-MCS";
                                            message = "收到" + length + "条" +
                                                msgType;
                                        }

                                        notification.tickerText = message;

                                        notification.ledARGB = Color.GREEN;
                                        notification.ledOnMS = 300; //亮的时间
                                        notification.ledOffMS = 0; //灭的时间

                                        Intent intent = new Intent(context,
                                                MainActivity.class);
                                        intent.putExtra("msgType", meesageType);

                                        PendingIntent contentIntent = PendingIntent.getActivity(context,
                                                0, intent,
                                                PendingIntent.FLAG_UPDATE_CURRENT);
                                        notification.setLatestEventInfo(context,
                                            title, message, contentIntent);
                                        notification.contentIntent = contentIntent;

                                        //notification.contentView = contentView;
                                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        notificationManager.notify(R.string.app_name,
                                            notification);
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
