package com.geely.util;

import android.content.Context;

import android.content.pm.PackageManager.NameNotFoundException;

import android.os.Looper;

import android.widget.Toast;

import com.geely.data.GetData;

import com.geely.po.Version;

import org.json.JSONException;
import org.json.JSONObject;


public class PackageUtil {
    private final static String TAG = "PackageUtil";

    public static Version getServerVersion(int currentVersionCode,
        Context context) throws Exception {
        String baseUrl = GetData.SERVER_IP;
        String jsonUrl = "clientLogin_getLastVersion.do";
        String apiUrl = baseUrl + jsonUrl;
        String result = HttpUtil.request(apiUrl, null, "post");

        if (!StringUtil.isEmpty(result)) {
            try {
                JSONObject obj = new JSONObject(result);
                Version version = new Version();
                version.VersionInnerID = obj.getInt("VERSION_CODE");
                version.MobileVersionID = obj.getString("VERSION_NAME");
                version.Description = obj.getString("DESCRIPTION");
                version.PubDate = obj.getString("PUB_DATE");
                version.VersionRequest = obj.getInt("REQUEST");
                version.DownloadUrl = obj.getString("FILE_NAME");

                return version;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Looper.prepare();
            Toast.makeText(context, "网络连接超时", Toast.LENGTH_LONG).show();
            Looper.loop();

            return null;
        }

        return null;
    }

    /**
     * 获取应用程序的版本号
     *
     * @param context
     * @return
     * @throws NameNotFoundException
     */
    public static int getVersionCode(Context context)
        throws NameNotFoundException {
        int verCode = context.getPackageManager()
                             .getPackageInfo("com.geely.geely_client", 0).versionCode;

        // context.getPackageManager().get
        return verCode;
    }

    /**
     * 获取应用程序的外部版本号
     *
     * @param context
     * @return
     * @throws NameNotFoundException
     */
    public static String getVersionName(Context context)
        throws NameNotFoundException {
        String versionName = context.getPackageManager()
                                    .getPackageInfo("com.geely.geely_client", 0).versionName;

        return versionName;
    }
}
