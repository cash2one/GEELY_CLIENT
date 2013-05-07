package com.geely.data;

import com.geely.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;


public class GetData {
    public final static String SERVER_IP = "http://222.243.162.29:4040/GEELY_SERVER/";
    public final static String LOGIN_URL = "clientLogin_login.do";
    public final static String GET_MES_INFO_URL = "clientMesinfo_getMesInfo.do";
    public final static String GET_MES_DETAIL_INFO_URL = "clientMesinfo_getMesDetailInfo.do";
    public final static String GET_MIS_INFO_URL = "clientMisinfo_getMisInfo.do";
    public final static String GET_METTING_LIST = "clientLogin_getUnreadMetting.do";
    public final static String GET_WARNING_LIST = "clientLogin_getUnreadWarning.do";
    public final static String VERSION_UPDATE = "apkfile/";

    /**
     * 登录
     * @param userName 用户名
     * @param passWord 密码
     * @return
     */
    public static String login(String userName, String passWord) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userName", userName);
        param.put("passWord", passWord);

        String result = HttpUtil.request(SERVER_IP + LOGIN_URL, param, "post");

        return result;
    }

    /**
     * 获取一段时间的mes信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public static String getMesInfo(String startTime, String endTime) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        String result = HttpUtil.request(SERVER_IP + GET_MES_INFO_URL, param,
                "post");

        return result;
    }

    /**
     * 获取一段时间的mes某类别的详细信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    public static String getMesDetailInfo(String startTime, String endTime,
        String lb) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("lb", lb);

        String result = HttpUtil.request(SERVER_IP + GET_MES_DETAIL_INFO_URL,
                param, "post");

        return result;
    }

    /**
     * 获取一段时间的mis信息
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param misType
     * @return
     */
    public static String getMisInfo(String startTime, String endTime,
        String misType) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("startTime", startTime);
        param.put("endTime", endTime);
        param.put("misType", misType);

        String result = HttpUtil.request(SERVER_IP + GET_MIS_INFO_URL, param,
                "post");

        return result;
    }

    /**
     * 获取用户未读会议列表
     * @param userId
     * @return
     */
    public static String getUnreadMetting(String userId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);

        String result = HttpUtil.request(SERVER_IP + GET_METTING_LIST, param,
                "post");

        return result;
    }

    /**
     * 获取用户未读预警列表
     * @param userId
     * @return
     */
    public static String getUnreadWarning(String userId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);

        String result = HttpUtil.request(SERVER_IP + GET_WARNING_LIST, param,
                "post");

        return result;
    }
}
