package com.example.util;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtils {

	private static String TAG="HttpUtils";

	//use callback
	public static void getResponseFromURL(final String address,final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection=null;
				try {
					URL url=new URL(address);
					connection= (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					//connection.setDoOutput(true);加上这个会导致getInputStream()抛出java.io.FileNotFound的异常
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					Log.d(TAG, "run: reading from url");
					while ((line=reader.readLine())!=null){
						//Log.d(TAG, "run: "+line);
						response.append(line);
					}
					if (listener!=null){
						listener.onFinish(response.toString());
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
					if(listener!=null){
						listener.onError(e);
					}
				}finally {
					if(connection!=null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	public static InputStream getStreamFromURL(String imageURL) {
		InputStream in = null;
		try {
			URL url = new URL(imageURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			in = connection.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return in;
	}
}
