package com.geely.geely_client;

import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.KeyEvent;

import android.widget.TextView;
import android.widget.Toast;

import com.geely.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;


public class LoadingActivity extends Activity {
    private TextView textView;
    private boolean isInterrupt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!HttpUtil.isNetworkAvailable(this)) {
            Toast.makeText(this, "无可用的网络连接", Toast.LENGTH_SHORT).show();
            setResult(2);
            finish();
        }

        setContentView(R.layout.loading);
        textView = (TextView) findViewById(R.id.loading_text);

        Intent intent = getIntent();
        textView.setText(intent.getStringExtra("msg"));

        String[] keys = intent.getStringArrayExtra("keys");
        String[] values = intent.getStringArrayExtra("values");
        final String uri = intent.getStringExtra("url");
        final Map<String, Object> param = new HashMap<String, Object>();

        for (int i = 0; i < keys.length; i++) {
            param.put(keys[i], values[i]);
        }

        Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = HttpUtil.request(uri, param, "post");
                        Intent mIntent = new Intent();
                        mIntent.putExtra("result", result);

                        if (!isInterrupt) {
                            setResult(0, mIntent);
                            finish();
                        }
                    }
                });
        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            isInterrupt = true;
            setResult(2);
            finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
