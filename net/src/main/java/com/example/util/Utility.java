package com.example.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.net.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class Utility {
	static Document document = null;
	public static String getData(String url,Context context) throws JSONException, IOException {	
		RequestQueue mQueue = Volley.newRequestQueue(context);
		StringRequest stringRequest = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						document = Jsoup.parse(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});
		mQueue.add(stringRequest);
		Log.d("TAG", document.toString());
		return document.toString();
	}

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.i("NetWorkState", "Unavailabel");
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						Log.i("NetWorkState", "Availabel");
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    /*public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }*/

    public static void showToast(Context context,String toast){
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
    }

    public static void log(String title,String log){
        Log.i(title,log);
    }

    public static void getFileFromBytes(String name,String path) {
        byte[] b=name.getBytes();
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(path);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
