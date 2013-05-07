package com.geely.geely_client;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.view.KeyEvent;

import android.widget.TextView;
import android.widget.Toast;


public class Activity5 extends Activity {
    private final static String TAG = "Activity5";
    private long exitTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("更多");
        setContentView(textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再点击一次退出",
                    Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent();
                intent.setAction("ExitApp");
                //发送广播退出所有activity
                sendBroadcast(intent);

                //延迟0.1秒退出应用
                new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 100);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
