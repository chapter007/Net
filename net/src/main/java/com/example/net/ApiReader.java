package com.example.net;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.fragment.Engadget;
import com.example.swiperefresh.SwipeBackActivity;
import com.example.util.FloatingActionButton;
import com.example.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by zhangjie on 2016/2/13.
 */
public class ApiReader extends SwipeBackActivity{
    private String title,article_id,cburl,engadget_comments,cbhtml;
    private WebView webView,comments,get_web;
    private LinearLayout loading,comments_loading;
    private FloatingActionButton mFAB;
    private HandlerThread handlerThread;
    private View MyDialog;
    private boolean comment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView=(WebView) findViewById(R.id.web);
        comments= (WebView) findViewById(R.id.comments);
        loading= (LinearLayout) findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        comments_loading= (LinearLayout) findViewById(R.id.comment_loading);
        comments_loading.setVisibility(View.VISIBLE);
        webView.setVisibility(View.GONE);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings=webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        Intent intent=getIntent();
        article_id=intent.getStringExtra("href");
        title=intent.getStringExtra("title");
        comment=intent.getBooleanExtra("comment",true);
        getSupportActionBar().setTitle(title);
        if (!comment) {
            comments.setVisibility(View.GONE);
            comments_loading.setVisibility(View.GONE);
        }
        getApiData task=new getApiData();
        task.execute(article_id);

    }

    class getApiData extends AsyncTask<String, android.R.integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url=params[0];
            JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String html =response.getString("body");
                                JSONArray css=response.getJSONArray("css");
                                String cssTag="<link rel=\"stylesheet\" href="+css.get(0)+">";
                                html=html+cssTag;
                                webView.setVisibility(View.VISIBLE);
                                loading.setVisibility(View.GONE);
                                if (html!=null) {
                                    webView.loadDataWithBaseURL("", html, "text/html", "utf-8", "");
                                }else {
                                    Utility.showToast(ApiReader.this,"no content");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("test-volley", "Error: " + error.getMessage());
                }
            });
            Engadget.getInstance().addToRequestQueue(jsonObjectRequest,"test_volley");
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            webView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            if (result!=null) {
                webView.loadDataWithBaseURL("", result, "text/html", "utf-8", "");
            }else {
                Log.i("content:",""+result);
                //Utility.showToast(ApiReader.this,"no content");
            }

            super.onPostExecute(result);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!this.getClass().equals(MainActivity.class)) {
                    finish();
                }
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class Getengadget extends AsyncTask<String, android.R.integer, String>{
        @Override
        protected String doInBackground(String... params) {
            String url=params[0];
            Element article_content = null;
            try {
                Document document=Jsoup.connect(url).timeout(10000).get();
                article_content=document.select("div[itemprop]").first();
                engadget_comments=document.select("#comments").toString();
                if (article_content!=null) {
                    Elements ele_Img = article_content.getElementsByTag("img");
                    if (ele_Img.size() != 0){
                        for (Element e_Img : ele_Img) {
                            e_Img.attr("style", "width:100%");
                        }
                    }
                }else {
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (article_content!=null) {
                return article_content.toString();
            }else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            webView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
            if (result!=null&&engadget_comments!=null) {
                webView.loadDataWithBaseURL("", result, "text/html", "utf-8", "");
                comments.loadDataWithBaseURL("", engadget_comments, "text/html", "utf-8", "");
            }else {
                Utility.showToast(ApiReader.this, "没得到内容");
            }

            super.onPostExecute(result);
        }

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.newsreader;
    }

    class MyHandler extends Handler {
        public MyHandler(){}

        public MyHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            String status=bundle.getString("status");
            Utility.log("handler?", status);
            if(cbhtml!=null&&status=="ok") {
                final Element comment = Jsoup.parse(cbhtml).select("div.commt_list").first();
                comment.attr("style", "display:''");
                comment.getElementsByClass("comment_avatars").remove();
                //Log.i("web comment", "" + comment);
                comments.post(new Runnable() {
                    @Override
                    public void run() {
                        comments_loading.setVisibility(View.GONE);
                        comments.loadDataWithBaseURL("", comment.toString(), "text/html", "utf-8", "");
                    }
                });
            }else{
                Utility.showToast(ApiReader.this, "并没有获取到评论数据");
            }
            super.handleMessage(msg);
        }
    }

    final class MyWebViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("myWebView", "onStart");
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d("myWebView", "onFinished");
            view.loadUrl("javascript:window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
            super.onPageFinished(view, url);
        }
    }

    final class InJavaScriptLocalObj{
        @JavascriptInterface
        public void showSource(final String html){//这是和js交互的方法
            cbhtml=html;
            Log.d("html from WebView","~~"+cbhtml);
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
}
