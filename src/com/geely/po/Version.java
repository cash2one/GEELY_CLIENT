package com.geely.po;

public class Version {
	/**
	 * 外部版本号，主要用于显示
	 */
	public String MobileVersionID;
	/**
	 * 内部版本号，主要用于升级时的判断
	 */
	public int VersionInnerID;
	/**
	 * 升级的描述
	 */
	public String Description;
	/**
	 * 发布时间
	 */
	public String PubDate;
	/**
	 * 是否必须升级 0 不必须  1 必须
	 */
	public int VersionRequest;
	/**
	 * 升级下载的地址
	 */
	public String DownloadUrl;
}
