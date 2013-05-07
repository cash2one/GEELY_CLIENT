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


public class MeetingActivity extends BaseActivity implements OnClickListener {
    private long exitTime;
    private View normal;
    private View other;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_metting);

        normal = findViewById(R.id.normal_meeting);
        other = findViewById(R.id.other_meeting);
        normal.setOnClickListener(this);
        other.setOnClickListener(this);
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

    /**
     * 更新下面显示的信息数目
     */
    public void updateMessageNum() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        MessageDao dao = new MessageDao(this);
        int normalNum = dao.getUnreadMettingNumByType(Integer.valueOf(userId),
                "01");
        TextView normalNumView = (TextView) findViewById(R.id.normail_meeting_num);
        dao.close();
        if (normalNum > 0) {
            normalNumView.setText(normalNum + "");
            normalNumView.setVisibility(View.VISIBLE);
        } else {
            normalNumView.setText("");
            normalNumView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        String subType = view.getTag().toString();
        Intent intent = new Intent(this, MessageListActivity.class);
        intent.putExtra("type", "meeting");
        intent.putExtra("subType", subType);
        startActivity(intent);
    }
}
