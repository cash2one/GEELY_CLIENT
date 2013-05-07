package com.geely.geely_client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;

import android.view.KeyEvent;
import android.view.View;

import android.view.View.OnClickListener;

import android.widget.TextView;
import android.widget.Toast;

import com.geely.db.MessageDao;


public class WarningActivity extends BaseActivity implements OnClickListener {
    private long exitTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warning);

        View oneWarning = findViewById(R.id.one_warning);
        View twoWarning = findViewById(R.id.two_warning);
        View threeWarning = findViewById(R.id.three_warning);
        oneWarning.setOnClickListener(this);
        twoWarning.setOnClickListener(this);
        threeWarning.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageNum();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 更新下面显示的信息数目
     */
    public void updateMessageNum() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        MessageDao dao = new MessageDao(this);
        int oneNum = dao.getUnreadWarningNumByLb(Integer.valueOf(userId), "一级预警");
        int twoNum = dao.getUnreadWarningNumByLb(Integer.valueOf(userId), "二级预警");
        int threeNum = dao.getUnreadWarningNumByLb(Integer.valueOf(userId),
                "三级预警");
        dao.close();

        TextView oneView = (TextView) findViewById(R.id.one_warning_num);
        TextView twoView = (TextView) findViewById(R.id.two_warning_num);
        TextView threeView = (TextView) findViewById(R.id.three_warning_num);

        if (oneNum > 0) {
            oneView.setText(oneNum + "");
            oneView.setVisibility(View.VISIBLE);
        } else {
            oneView.setText("");
            oneView.setVisibility(View.INVISIBLE);
        }

        if (twoNum > 0) {
            twoView.setText(twoNum + "");
            twoView.setVisibility(View.VISIBLE);
        } else {
            twoView.setText("");
            twoView.setVisibility(View.INVISIBLE);
        }

        if (threeNum > 0) {
            threeView.setText(threeNum + "");
            threeView.setVisibility(View.VISIBLE);
        } else {
            threeView.setText("");
            threeView.setVisibility(View.INVISIBLE);
        }
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

    @Override
    public void onClick(View view) {
        String subType = view.getTag().toString();
        Intent intent = new Intent(this, MessageListActivity.class);
        intent.putExtra("type", "warning");
        intent.putExtra("subType", subType);
        startActivity(intent);
    }
}
