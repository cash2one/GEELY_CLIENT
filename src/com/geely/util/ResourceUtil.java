package com.geely.util;

import android.content.Context;

import android.content.res.Resources;

import android.graphics.drawable.Drawable;


/**
 *
 * <p>Title: 信息田园（手机客户端）</p>
 * <p>Description:资源信息工具类</p>
 * <p>创建日期:2011-12-9</p>
 * @author ZhouChao
 * @version 2.0
 * <p>九龙晖科技有限公司</p>
 */
public class ResourceUtil {
    private Context context = null;

    public ResourceUtil(Context context) {
        this.context = context;
    }

    /**
     * 获取包下图片资源
     * @param paramString
     * @return
     */
    public Drawable getDrawable(String name) {
        Resources resource = context.getResources();
        int i = getDrawableIdentifier(name);

        return resource.getDrawable(i);
    }

    /**
     * 获取包下图片id
     * @param name
     * @return
     */
    public int getDrawableIdentifier(String name) {
        return getResourceIdentifier(name, "drawable");
    }

    /***
     * 获取包下id资源的id
     * @param name
     * @return
     */
    public int getIdIdentifier(String name) {
        return getResourceIdentifier(name, "id");
    }

    /**
     * 获取andorid系统默认attr属性资源
     * @param name
     * @return
     */
    public int getInternalAttributeIdentifier(String name) {
        return getInternalResourceIdentifier(name, "attr");
    }

    /**
     * 获取android系统图片
     * @param name
     * @return
     */
    public Drawable getInternalDrawable(String name) {
        Resources resource = context.getResources();
        int i = getInternalDrawableIdentifier(name);

        return resource.getDrawable(i);
    }

    /**
     * 获取android系统默认图片
     * @param name
     * @return
     */
    public int getInternalDrawableIdentifier(String name) {
        return getInternalResourceIdentifier(name, "drawable");
    }

    /**
     * 获取android系统默认资源
     * @param name 资源名称
     * @param type 资源类型
     * @return
     */
    private int getInternalResourceIdentifier(String name, String type) {
        return getResourceIdentifier(name, type, "android");
    }

    /**
     * 根据名称回去布局文件Id
     * @param name
     * @return
     */
    public int getLayoutIdentifier(String name) {
        return getResourceIdentifier(name, "layout");
    }

    /**
     * 根据资源名称和资源类型获取资源Id
     * @param name
     * @param type
     * @return
     */
    private int getResourceIdentifier(String name, String type) {
        String packageName = context.getPackageName();

        return getResourceIdentifier(name, type, packageName);
    }

    /***
     * 获取资源Id
     * @param name 资源名称
     * @param type 资源类型
     * @param packageName 包名称
     * @return
     */
    private int getResourceIdentifier(String name, String type,
        String packageName) {
        return context.getResources().getIdentifier(name, type, packageName);
    }

    /**
     * 获取string资源
     * @param name
     * @return
     */
    public String getString(String name) {
        Resources resource = context.getResources();
        int i = getStringIdentifier(name);

        return resource.getString(i);
    }

    /**
     * 获取string资源id
     * @param name
     * @return
     */
    public int getStringIdentifier(String name) {
        return getResourceIdentifier(name, "string");
    }
}
