package com.example.net;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import com.example.swiperefresh.SwipeBackActivity;
import com.example.swiperefresh.SwipeBackActivity;
import com.example.util.FloatingActionButton;
import com.example.util.HttpCallbackListener;
import com.example.util.HttpUtils;
import com.example.util.Utility;

import android.R.integer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NewsReader extends SwipeBackActivity{
	private static final String TAG ="NewsReader";
	private static final int UPDATE_UI = 1,GOT_COMMENT=2;
	private String title;
	private String cburl;
	private WebView webView,comments,get_comment;
	private LinearLayout loading,comments_loading,comments_background;

	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case UPDATE_UI:
					webView.setVisibility(View.VISIBLE);
					loading.setVisibility(View.GONE);
					String result=msg.getData().getString("result");
					if (result!=null) {
						webView.loadDataWithBaseURL("", result, "text/html", "utf-8", "");
					}else {
						Toast.makeText(NewsReader.this, "no content", Toast.LENGTH_SHORT).show();
					}
					break;
				case GOT_COMMENT:
					String status=msg.getData().getString("status");
					String comment=msg.getData().getString("comment");
					if(comment!=null&&status=="ok") {
						Document doc=Jsoup.parse(comment);
						Element comment_e = doc.getElementById("comments-box");
						Log.i(TAG, "handleMessage: "+comment_e);
						comments_loading.setVisibility(View.GONE);
                        comments_background.setVisibility(View.VISIBLE);
						comments.loadDataWithBaseURL("", comment_e.toString(), "text/html", "utf-8", "");
					}else{
						Utility.showToast(NewsReader.this, "并没有获取到评论数据");
					}
					break;
			}
		}
	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        webView=(WebView) findViewById(R.id.web);
        comments= (WebView) findViewById(R.id.comments);
        loading= (LinearLayout) findViewById(R.id.loading);
		comments_background= (LinearLayout) findViewById(R.id.comments_background);
        comments_loading= (LinearLayout) findViewById(R.id.comment_loading);

        loading.setVisibility(View.VISIBLE);
        comments_background.setVisibility(View.GONE);
        comments_loading.setVisibility(View.VISIBLE);
		webView.setVisibility(View.GONE);

		webView.setWebViewClient(new WebViewClient());
		WebSettings webSettings=webView.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webSettings.setJavaScriptEnabled(true);
		Intent intent=getIntent();
		title=intent.getStringExtra("title");
		getSupportActionBar().setTitle(title);
        cburl=intent.getStringExtra("href");

		get_comment=new WebView(NewsReader.this);
		get_comment.addJavascriptInterface(new InJavaScriptLocalObj(), "myObj");
		get_comment.setWebViewClient(new MyWebViewClient());
		get_comment.getSettings().setJavaScriptEnabled(true);
		get_comment.loadUrl(cburl);

		//handlerThread=new HandlerThread("handler_thread");
		//handlerThread.start();//创建了一个新的线程

		/*Getweb task=new Getweb();
		task.execute(cburl);*/

		Log.i(TAG, "获取新闻主体内容");
		HttpUtils.getResponseFromURL(cburl, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				Log.i(TAG, "onFinish: ");
				Document document=Jsoup.parse(response);
				Element html =null;
				Element article_content=document.select("div.cnbeta-article").first();
				if (article_content!=null) {
					Element Eintro=article_content.select("div.article-summary>p").first();
					Element content=article_content.select("div.article-content").first();
					String newHtmlContent=Eintro.toString()+content.toString();
					html =Jsoup.parse(newHtmlContent);
					Elements ele_Img = html.getElementsByTag("img");
					if (ele_Img.size() != 0){
						for (Element e_Img : ele_Img) {
							e_Img.attr("style", "width:100%");
						}
					}
				}

				/*这里是子线程，不能直接操作ui，需要用到handler*/
				Message message=new Message();
				message.what=UPDATE_UI;
				Bundle bundle=new Bundle();
				bundle.putString("result",html.toString());
				message.setData(bundle);
				handler.sendMessage(message);
			}

			@Override
			public void onError(Exception e) {

			}
		});
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

	@Override
	protected int getLayoutResource() {
		return R.layout.newsreader;
	}

    final class MyWebViewClient extends WebViewClient{

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i(TAG, "onPageStarted: ");
			super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
			view.loadUrl("javascript:window.myObj.showSource(document.getElementsByTagName('html')[0].innerHTML);");
			Log.i(TAG, "onPageFinished: ");
			//这个网页加载的太慢了
			super.onPageFinished(view, url);
        }
    }

	class InJavaScriptLocalObj{

        @JavascriptInterface
        public void showSource(final String html){//这是和js交互的方法
            Message msg =new Message();
			msg.what=GOT_COMMENT;
            Bundle bundle=new Bundle();
            if(html!=null){
                bundle.putString("status","ok");
				bundle.putString("comment",html);
            }else {
                bundle.putString("status","no");
            }
            msg.setData(bundle);
			handler.sendMessage(msg);
        }
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		comments.destroy();
		get_comment.destroy();
		webView.destroy();
		Utility.log("myNews","quit");
	}
}
