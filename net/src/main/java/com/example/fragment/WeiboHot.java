package com.example.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.net.NewsReader;
import com.example.net.R;
import com.example.swiperefresh.SwipeRefreshLayout;
import com.example.swiperefresh.SwipeRefreshLayout.OnLoadListener;
import com.example.swiperefresh.SwipeRefreshLayout.OnRefreshListener;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;

public class WeiboHot extends Fragment implements OnRefreshListener,OnLoadListener{

	private ListView weibo_list;
	private SwipeRefreshLayout swipeLayout;
	private String url="http://d.weibo.com/";
	
	@SuppressLint("JavascriptInterface")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.weibohot, null);
		weibo_list=(ListView) view.findViewById(R.id.weibo_list);
		swipeLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_weibo);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setOnLoadListener(this);
		swipeLayout.setColor(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		//new getWeibo().execute();
		WebView gethtml=new WebView(getActivity());
		gethtml.getSettings().setJavaScriptEnabled(true);
		gethtml.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
		gethtml.setWebViewClient(new myWebViewClient());
		gethtml.loadUrl(url);
		
		
		return view;
	}
	
	class myWebViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url) {   
            view.loadUrl(url);   
            return true;
        }  
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebView","onPageStarted");
            super.onPageStarted(view, url, favicon);
        }    
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView","onPageFinished ");
            view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, url);
        }
	}
	
	final class InJavaScriptLocalObj {
        public void showSource(String html) {
            Log.d("HTML", html);
        }
    }
	
	public class getWeibo extends AsyncTask<String, integer, String>{

		@Override
		protected String doInBackground(String... params) {
			try {
				parseWeibo(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	public void parseWeibo(String url) throws IOException{
		RequestQueue mQueue = Volley.newRequestQueue(getActivity());
		myStringRequest stringRequest = new myStringRequest(Request.Method.GET,url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Document document=Jsoup.parse(response);
						Elements weibos=document.select("div.WB_cardwrap.WB_feed_type");
						for (Element weibo : weibos) {
							String pic=weibo.select("img.W_face_radius").first().attr("src");
							String user=weibo.select("a.W_f14").first().text();
							Log.i("userName", user);
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});
		mQueue.add(stringRequest);
	}
	
	public class myStringRequest extends StringRequest{
		public myStringRequest(int method, String url,
				Listener<String> listener, ErrorListener errorListener) {
			super(method, url, listener, errorListener);
		}
		
		@Override
		protected Response<String> parseNetworkResponse(NetworkResponse response) {
			 String str = null;
		        try {
		            str = new String(response.data,"GBK");
		        } catch (UnsupportedEncodingException e) {
		            e.printStackTrace();
		        }
			return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("WeiboHot");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}
}
