package com.geely.geely_client;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.view.KeyEvent;


/**
 *
 * <p>Title: G-MCS</p>
 * <p>Description:基础activity</p>
 * <p>创建日期:2013-4-7</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class BaseActivity extends Activity {
    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("ExitApp");
        this.registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            this.unregisterReceiver(this.broadcastReceiver);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) &&
                (event.getRepeatCount() == 0)) {
            this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    public void handleActivityResult(int requestCode, int resultCode,
        Intent data) {
    }
}
