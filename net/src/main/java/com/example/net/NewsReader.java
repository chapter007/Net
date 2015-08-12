package com.example.net;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//import com.example.swiperefresh.SwipeBackActivity;
import com.example.swiperefresh.SwipeBackActivity;
import com.example.util.FloatingActionButton;
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
import android.widget.ProgressBar;
import android.widget.Toast;

public class NewsReader extends SwipeBackActivity{
	private String title,article_id,cburl,engadget_comments,cbhtml;
	private WebView webView,comments,get_web;
	private ProgressBar loading;
	private FloatingActionButton mFAB;
    private HandlerThread handlerThread;
    private View MyDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        webView=(WebView) findViewById(R.id.web);
        comments= (WebView) findViewById(R.id.comments);
        loading=(ProgressBar) findViewById(R.id.loading);
		loading.setVisibility(View.VISIBLE);
		webView.setVisibility(View.GONE);
		webView.setWebViewClient(new WebViewClient());
		WebSettings webSettings=webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		Intent intent=getIntent();
		article_id=intent.getStringExtra("href");
		title=intent.getStringExtra("title");
		getSupportActionBar().setTitle(title);
        cburl="http://www.cnbeta.com"+article_id;
		if (article_id.contains("http")) {
			Getengadget taskGetengadget=new Getengadget();
			taskGetengadget.execute(article_id);
		}else {
            get_web=new WebView(NewsReader.this);
            get_web.getSettings().setJavaScriptEnabled(true);
            get_web.addJavascriptInterface(new InJavaScriptLocalObj(),"local_obj");
            get_web.setWebViewClient(new MyWebViewClient());

            handlerThread=new HandlerThread("handler_thread");
            handlerThread.start();//创建了一个新的线程
			Getweb task=new Getweb();
			task.execute(cburl);
            get_web.loadUrl(cburl);
		}
	}
	
	
	class Getweb extends AsyncTask<String, integer, String>{	
			@Override
			protected String doInBackground(String... params) {
				String url=params[0];
				Element html = null;
				try {
					Document document=Jsoup.connect(url).timeout(10000).get();
					Element article_content=document.select("section.article_content").first();
					if (article_content!=null) {
						Element Eintro=article_content.select("div.introduction").first();
						Element content=article_content.select("div.content").first();
						String newHtmlContent=Eintro.toString()+content.toString();
						html=Jsoup.parse(newHtmlContent);
						Elements ele_Img = html.getElementsByTag("img");
						if (ele_Img.size() != 0){
							for (Element e_Img : ele_Img) {
								e_Img.attr("style", "width:100%");
							}
						}
						html.select("div.introduction>div").first().remove();
					}else {
						return null;
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				return html.toString();
			}
			
			@Override
			protected void onPostExecute(String result) {
				webView.setVisibility(View.VISIBLE);
				loading.setVisibility(View.GONE);
				if (result!=null) {
					webView.loadDataWithBaseURL("", result, "text/html", "utf-8", "");
				}else {
					Toast.makeText(NewsReader.this, "û�л�ȡ������", Toast.LENGTH_SHORT).show();
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

	class Getengadget extends AsyncTask<String, integer, String>{
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
				Toast.makeText(NewsReader.this, "没得到内容~", Toast.LENGTH_SHORT).show();
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
                Log.i("web comment", "" + comment);
                comments.post(new Runnable() {
                    @Override
                    public void run() {
                        comments.loadDataWithBaseURL("", comment.toString(), "text/html", "utf-8", "");
                    }
                });
            }else{
                Utility.showToast(NewsReader.this, "并没有获取到评论数据");
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
