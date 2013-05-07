package com.geely.exception;

import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.SharedPreferences.Editor;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import android.os.Build;
import android.os.Environment;
import android.os.Looper;

import android.util.Log;

import android.widget.Toast;

import com.geely.util.FileUtil;
import com.geely.util.HttpUtil;
import com.geely.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *
 * <p>Title: 好爸妈客户端</p>
 * <p>Description:全局异常崩溃处理</p>
 * <p>创建日期:2012-12-26</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private final static String TAG = "CrashHandler";
    private static CrashHandler instance = new CrashHandler();
    private final static String EXCEPTION_URL = "http://"; //异常提交的URL地址
    private Context mContext;
    private UncaughtExceptionHandler mDefaultHandler;
    private Map<String, String> infos = new HashMap<String, String>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    /**
     * 初始化处理
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        if (!handleException(throwable) && (mDefaultHandler != null)) {
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "发生线程异常", e);
            }

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 提交异常信息到服务器
     * @param fileName 异常文件路径
     */
    private void submitException(String fileName) {
        if (HttpUtil.isNetworkAvailable(mContext)) {
            Map<String, Object> param = new HashMap<String, Object>();
            String content = FileUtil.readFileToString(fileName);
            param.put("content", content);

            String result = HttpUtil.request(EXCEPTION_URL, param, "post");

            if ("0".equals(result)) {
                SharedPreferences crashs = mContext.getSharedPreferences("crash",
                        Activity.MODE_PRIVATE);
                Editor edit = crashs.edit();
                edit.putBoolean(fileName, false);
                edit.commit();
            }
        }
    }

    /**
     * 未捕获异常的处理
     * @param throwable 异常信息
     * @return 是否处理了异常信息
     */
    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }

        new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.",
                        Toast.LENGTH_LONG).show();
                    Looper.loop();
                    super.run();
                }
            }.start();
        collectDeviceInfo();

        String fileName = saveCrashInfoToFile(throwable);

        if (!StringUtil.isEmpty(fileName)) {
            SharedPreferences crashs = mContext.getSharedPreferences("crash",
                    Activity.MODE_PRIVATE);
            Editor edit = crashs.edit();
            edit.putBoolean(fileName, true);
            edit.commit();
            submitException(fileName);
        }

        return true;
    }

    /**
     * 手机设计设备信息
     */
    private void collectDeviceInfo() {
        PackageManager pm = mContext.getPackageManager();

        try {
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);

            if (pi != null) {
                String versionName = (pi.versionName == null) ? "null"
                                                              : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "收集包信息出错", e);
        }

        Field[] fields = Build.class.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "获取手机基本信息时出错", e);
            }
        }
    }

    /**
     * 保存奔溃信息到文件中
     * @param ex 异常信息
     * @return 保持的文件路径
     */
    private String saveCrashInfoToFile(Throwable ex) {
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();

        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.close();

        String result = writer.toString();
        sb.append(result);

        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".log";
            String path = "";

            if (Environment.getExternalStorageState()
                               .equals(Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory().getPath() +
                    "/crash/";

                File dir = new File(path);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } else {
                path = "/data/data/";

                String packageName = mContext.getPackageName();
                path += (packageName + "/crash/");

                File dir = new File(path);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            }

            return path + fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }

        return null;
    }
}
