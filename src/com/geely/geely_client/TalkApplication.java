package com.geely.geely_client;

import android.app.Application;

import com.geely.exception.CrashHandler;


/**
 *
 *
 * <p>Title: 好爸妈客户端</p>
 * <p>Description:自定义Application</p>
 * <p>创建日期:2012-12-26</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class TalkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler handler = CrashHandler.getInstance();
        handler.init(getApplicationContext());
    }
}
