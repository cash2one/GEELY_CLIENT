package com.geely.util;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * <p>Title:好爸妈客户端开发</p>
 * <p>Description:http连接工具类</p>
 * <p>创建日期:2013-1-11</p>
 * @author yuanmingxiang
 * @version 1.0
 * <p>拓维教育-好爸妈项目组</p>;
 * <p>http://www.hbm100.com</p>;
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil: ";

    //    private static final String CHARSET = "utf-8";

    /**
     * 发送http请求
     * @param uri 请求uri地址
     * @param param 请求参数
     * @param httpMethod 请求方法
     * @return
     */
    public static String request(String uri, Map<String, Object> param,
        String httpMethod) {
        try {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 10000;
            HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);

            int timeoutSocket = 10000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            if (StringUtil.isEmpty(uri)) {
                Log.e(TAG, "无效的URL地址");

                return "";
            }

            if (StringUtil.isEmpty(httpMethod)) {
                Log.e(TAG, "无效的httpMethod");

                return "";
            } else if ("get".equalsIgnoreCase(httpMethod)) {
                if ((param != null) && !param.isEmpty()) {
                    for (String key : param.keySet()) {
                        if (uri.contains("?")) {
                            uri += ("&" + (key + "=" + param.get(key)));
                        } else {
                            uri += ("?" + key + "=" + param.get(key));
                        }
                    }
                }

                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpGet request = new HttpGet();
                System.out.println(uri);
                request.setURI(new URI(uri));

                HttpResponse response = client.execute(request);

                /*若状态码为200 ok*/
                if (response.getStatusLine().getStatusCode() == 200) {
                    /*取出响应字符串*/
                    String strResult = EntityUtils.toString(response.getEntity());

                    return strResult;
                } else {
                    Log.e(TAG,
                        "Error Response: " +
                        response.getStatusLine().toString());
                }
            } else if ("post".equalsIgnoreCase(httpMethod)) {
                HttpClient hc = new DefaultHttpClient(httpParameters);
                HttpPost request = new HttpPost(uri);
                List<NameValuePair> params = new ArrayList<NameValuePair>();

                if ((param != null) && !param.isEmpty()) {
                    for (String key : param.keySet()) {
                        params.add(new BasicNameValuePair(key,
                                param.get(key).toString()));
                    }

                    if ((params != null) && !params.isEmpty()) {
                        request.setEntity(new UrlEncodedFormEntity(params,
                                HTTP.UTF_8));
                    }
                }

                HttpResponse response = hc.execute(request);
                BufferedReader br = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer();
                String line = "";

                if ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                return sb.toString();
            } else {
                Log.e(TAG, "无效的httpMethod");
            }
        } catch (ClientProtocolException e) {
            Log.e(TAG, "网络连接异常。msg:" + e.getMessage());
        } catch (Exception ee) {
            ee.printStackTrace();
            Log.e(TAG, "http连接异常。msg:" + ee.getMessage());
        }

        return "";
    }

    /**
     * 获取远程地址文件大小
     * @param remoleUrl
     * @return
     */
    public static long getRemoleFileSize(String remoleUrl) {
        long size = 0;
        URL url;

        try {
            url = new URL(remoleUrl);

            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            size = connect.getContentLength();
        } catch (Exception e) {
            Log.e(TAG, "获取远程文件大小出错。msg:" + e.getMessage());
        }

        return size;
    }

    /**
         * 检查网络是否可用
         *
         * @return 如果可用，返回true，不可用则返回false
         */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo[] info = cm.getAllNetworkInfo();

        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }

        // return true;
        return false;
    }

    /**
    * 判断当前连接模式是否是WIFI
    * @param context
    * @return 是wifi返回true
    */
    public static boolean isWIFIConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // //获取系统的连接服务
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo(); // //获取网络的连接情况

        if (null != activeNetworkInfo && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true; // wifi连接
        } else {
            return false;
        }
    }

    /**
     * 使用Http下载文件，并保存在手机目录中
     *
     * @param urlStr
     * @param path
     * @param fileName
     * @return -1:文件下载出错 0:文件下载成功
     */
    public static boolean downFile(String urlStr, String path, String fileName) {
        InputStream inputStream = null;
        long filesize = 0;
        long readfilesize = 0;

        try {
            if (!path.endsWith("/")) {
                path += "/";
            }

            if (FileUtil.FileIsExist(path + fileName)) {
                FileUtil.deleteFile(path + fileName);
            }

            HttpClient client = new DefaultHttpClient();
            // 设置网络连接超时和读数据超时
            client.getParams()
                  .setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000)
                  .setParameter(CoreConnectionPNames.SO_TIMEOUT, 600000);

            HttpGet httpget = new HttpGet(urlStr);
            HttpResponse response = client.execute(httpget);

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                return false;
            }

            InputStream fileStream = response.getEntity().getContent();
            filesize = response.getEntity().getContentLength();
            readfilesize = 0;

            File file = new File(path);

            if (!file.exists()) {
                FileUtil.createFileDirs(path);
            }

            FileOutputStream output = new FileOutputStream(path + fileName);
            ;

            byte[] buffer = new byte[1024];
            int len = 0;

            while ((len = fileStream.read(buffer)) > 0) {
                output.write(buffer, 0, len);
                readfilesize += len; //累计下载的文件大小
            }

            fileStream.close();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            if (readfilesize < filesize) {
                if (FileUtil.FileIsExist(path + fileName)) {
                    FileUtil.deleteFile(path + fileName);
                }
            }
        }

        return true;
    }
}
