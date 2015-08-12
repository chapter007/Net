package com.example.net;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class Comment extends ToolBar {
	private WebView web_comment,get_web;
	private ProgressBar loading;
	private String cbhtml;
    private HandlerThread handlerThread;
    private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle("评论");
		web_comment = (WebView) findViewById(R.id.web_comment);
        get_web=new WebView(Comment.this);
        get_web.getSettings().setJavaScriptEnabled(true);
        get_web.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
        get_web.setWebViewClient(new MyWebViewClient());

        Intent intent = getIntent();
        final String url=intent.getStringExtra("url");
        Log.i("url:", url);

        handlerThread=new HandlerThread("handler_thread");
        handlerThread.start();//创建了一个新的线程
        get_web.loadUrl(url);
        progressDialog=ProgressDialog.show(Comment.this, "评论才是本体", "等等也值得！！！其实是我没弄好这块。。");

        /*Task myTask=new Task();
        myTask.execute(url);*/

    }

	@Override
	protected int getLayoutResource() {
		// TODO Auto-generated method stub
		return R.layout.comment;
	}

	public void getFileFromBytes(String name, String path) {
		byte[] b = name.getBytes();
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

    private class Task extends AsyncTask<String, android.R.integer, String>{

        @Override
        protected void onPreExecute() {
            progressDialog=ProgressDialog.show(Comment.this, "getweb..", "wait me...");
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            showToast("貌似完成了页面加载");
            super.onPostExecute(s);
        }
    }

	public void volley(String url) {
		RequestQueue mQueue = Volley.newRequestQueue(this);
		StringRequest stringRequest = new StringRequest(url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("TAG", response);
						getFileFromBytes(response, "/sdcard/test.html");
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("TAG", error.getMessage(), error);
					}
				});
		mQueue.add(stringRequest);

	}

    final class MyWebViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("myWebView","onStart");
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("myWebView", "onFinished");
            view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
            super.onPageFinished(view, url);
         }
    }

    class MyHandler extends Handler{
        public MyHandler(){}

        public MyHandler(Looper looper){
           super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            String status=bundle.getString("status");
            log("handler?",status);
            if(cbhtml!=null&&status=="ok") {
                progressDialog.dismiss();
                final Element comment = Jsoup.parse(cbhtml).select("div.commt_list").first();
                comment.attr("style", "display:''");
                Log.i("web comment", "" + comment);
                web_comment.post(new Runnable() {
                    @Override
                    public void run() {
                        web_comment.loadDataWithBaseURL("", comment.toString(), "text/html", "utf-8", "");
                    }
                });
            }else{
                progressDialog.dismiss();
                showToast("并没有获取到评论数据");
            }
            super.handleMessage(msg);
        }
    }

    final class InJavaScriptLocalObj{
        @JavascriptInterface
        public void showSource(final String html){//这是和js交互的方法
            cbhtml=html;
            Log.d("html from WebView","~~"+cbhtml);
            /*if(cbhtml!=null) {
                progressDialog.dismiss();
                Element comment = Jsoup.parse(cbhtml).select("div.commt_list").first();
                comment.attr("style", "display:''");
                Log.i("web comment", "" + comment);
                //web_comment.loadDataWithBaseURL("", comment.toString(), "text/html", "utf-8", "");
            }else{
                progressDialog.dismiss();
                showToast("并没有获取到评论数据");
            }*/
            MyHandler myHandler=new MyHandler(handlerThread.getLooper());
            Message msg =myHandler.obtainMessage();
            Bundle bundle=new Bundle();
            if(cbhtml!=null){
                bundle.putString("status","ok");
            }else {
                bundle.putString("status","no");
            }
            msg.setData(bundle);
            msg.sendToTarget();
        }
    }

    private void showToast(String toast){
        Toast.makeText(this,toast,Toast.LENGTH_SHORT).show();
    }

    private void log(String title,String log){
        Log.i(title,log);
    }
}

