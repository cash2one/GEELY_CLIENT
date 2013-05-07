package com.geely.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

public class GetImage {
	private String imagePath = Environment.getExternalStorageDirectory()+"/goodparent/png/";
	private String imageName = "";
	
	public GetImage(){
		
	}
	
	public GetImage(String _imageName){
		imageName = _imageName+imageName;
	}

	// 读取的方法
	public byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		// 用数据装
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		inStream.close();
		// 关闭流一定要记得。
		return outstream.toByteArray();
	}

	public boolean getImage(String netUrl) throws Exception {
		File destDir = new File(imagePath);
		  if (!destDir.exists()) {
		   destDir.mkdirs();
		  }
		
//		// 要下载的图片的地址，
//		String urlPath = "http://api.go108.cn/api/luck_chart/chart.php?d_str=4a5a3a3a4a4a2a5-3a3a2a4a4a3a5a4";
		URL url = new URL(netUrl);
		// 获取到路径
		// http协议连接对象
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		// 这里是不能乱写的，详看API方法
//		conn.setConnectTimeout(6 * 1000);
		// 别超过10秒。
//		System.out.println(conn.getResponseCode());
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			byte[] data = readStream(inputStream);
			File file = new File(imagePath+imageName);
			// 给图片起名子
			FileOutputStream outStream = new FileOutputStream(file);
			// 写出对象
			outStream.write(data);
			// 写入
			outStream.close();
			// 关闭流
			return true;
		}else{
			return false;
		}
	}

	public Bitmap drawBitMapFromSDcard() {
		Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath+imageName);		
		return imageBitmap;
	}
	
//	public int getCurrImageName(){
//		
//	}
}
