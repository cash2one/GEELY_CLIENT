package com.geely.config;

import com.geely.geely_client.MeetingActivity;
import com.geely.geely_client.WarningActivity;
import com.geely.geely_client.Activity5;
import com.geely.geely_client.HomeActivity;
import com.geely.geely_client.QueryGroupTab;
import com.geely.geely_client.R;


public class Config {
    public static final class TabConfig {
        public static int[] mImageViewArray = {
                R.drawable.home_icon, R.drawable.query_icon,
                R.drawable.metting_icon, R.drawable.warning_icon,
                R.drawable.more_icon
            };
        public static String[] mTextviewArray = { "主页", "查询", "通知", "预警", "更多" };
        public static Class[] mTabClassArray = {
                HomeActivity.class, QueryGroupTab.class, MeetingActivity.class,
                WarningActivity.class, Activity5.class
            };
    }
}
