package com.geely.geely_client;

import android.app.NotificationManager;
import android.app.TabActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.TabHost;

import android.widget.TabHost.TabSpec;

import android.widget.TextView;

import com.geely.config.Config.TabConfig;

import com.geely.db.MessageDao;

import com.geely.util.HttpUtil;
import com.geely.util.StringUtil;


public class MainActivity extends TabActivity {
    private TabHost m_tabHost;
    private LayoutInflater mLayoutInflater;
    private String msgType;
    private NotificationManager notiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (HttpUtil.isNetworkAvailable(this)) {
            UpdateManager manager = new UpdateManager(this);
            manager.checkUpdate();
        }

        init();
        notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notiManager.cancel(R.string.app_name);

        Intent intent = getIntent();
        msgType = intent.getStringExtra("msgType");

        if (!StringUtil.isEmpty(msgType)) {
            if ("meeting".equals(msgType)) {
                m_tabHost.setCurrentTab(2);
            } else if ("warning".equals(msgType)) {
                m_tabHost.setCurrentTab(3);
            }
        }
    }

    private void init() {
        m_tabHost = getTabHost();
        mLayoutInflater = LayoutInflater.from(this);

        int count = TabConfig.mTabClassArray.length;

        for (int i = 0; i < count; i++) {
            TabSpec tabSpec = m_tabHost.newTabSpec(TabConfig.mTextviewArray[i])
                                       .setIndicator(getTabItemView(i))
                                       .setContent(getTabItemIntent(i));
            m_tabHost.addTab(tabSpec);
            m_tabHost.getTabWidget().getChildAt(i)
                     .setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private View getTabItemView(int index) {
        View view = mLayoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);

        if (imageView != null) {
            imageView.setImageResource(TabConfig.mImageViewArray[index]);
        }

        TextView textView = (TextView) view.findViewById(R.id.textview);

        textView.setText(TabConfig.mTextviewArray[index]);

        return view;
    }

    private Intent getTabItemIntent(int index) {
        Intent intent = new Intent(this, TabConfig.mTabClassArray[index]);

        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMessageNum();
    }

    /**
     * 更新下面显示的信息数目
     */
    public void updateMessageNum() {
        View mettingView = m_tabHost.getTabWidget().getChildAt(2);
        View warningView = m_tabHost.getTabWidget().getChildAt(3);
        TextView mettingNum = (TextView) mettingView.findViewById(R.id.main_tab_new_tv);
        TextView warningNum = (TextView) warningView.findViewById(R.id.main_tab_new_tv);
        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        MessageDao dao = new MessageDao(this);
        int mnum = dao.getUnreadMettingNum(Integer.valueOf(userId));
        int wnum = dao.getUnreadWarningNum(Integer.valueOf(userId));
        dao.close();

        if (mnum > 0) {
            mettingNum.setText(mnum + "");
            mettingNum.setVisibility(View.VISIBLE);
        } else {
            mettingNum.setText("");
            mettingNum.setVisibility(View.INVISIBLE);
        }

        if (wnum > 0) {
            warningNum.setText(wnum + "");
            warningNum.setVisibility(View.VISIBLE);
        } else {
            warningNum.setText("");
            warningNum.setVisibility(View.INVISIBLE);
        }
    }
}
