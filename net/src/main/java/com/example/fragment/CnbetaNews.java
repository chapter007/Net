package com.example.fragment;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.adapter.MyAdapter;
import com.example.adapter.MyAdapter.MyItemClickListener;
import com.example.database.Article;
import com.example.database.DbManager;
import com.example.net.NewsReader;
import com.example.net.R;
import com.example.swiperefresh.SwipeRefreshLayout;
import com.example.swiperefresh.SwipeRefreshLayout.OnLoadListener;
import com.example.swiperefresh.SwipeRefreshLayout.OnRefreshListener;
import com.example.util.Utility;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CnbetaNews extends Fragment implements OnRefreshListener,
		OnLoadListener {

	private SwipeRefreshLayout swipeLayout;
	private RecyclerView list;
	private MyAdapter adapter;
	private ProgressDialog progressDialog;
	private String Rowdata, Newdata;
	private SharedPreferences preferences;
	private boolean isCache;
	private Context mcontext;
    private DbManager manager;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mcontext = getActivity();
		View view = inflater.inflate(R.layout.activity_main, container, false);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
		list = (RecyclerView) view.findViewById(R.id.list);
        manager = new DbManager(mcontext);
		
		preferences = PreferenceManager.getDefaultSharedPreferences(mcontext);
		isCache = preferences.getBoolean("isCache", false);
		LinearLayoutManager layoutManager = new LinearLayoutManager(mcontext);
		list.setLayoutManager(layoutManager);
		list.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view,
					RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				outRect.set(0, 0, 0, 20);
			}
		});

		adapter = new MyAdapter(getActivity(), data);
		adapter.setOnItemClickListener(new MyItemClickListener() {

			@Override
			public void onItemClick(View view, int postion) {
				String href=(String) data.get(postion).get("href");
				String title=(String) data.get(postion).get("title");
				Intent intent = new Intent(getActivity(), NewsReader.class);
				intent.putExtra("href", href);
				intent.putExtra("title",title);
				startActivity(intent);
			}
		});
		list.setAdapter(adapter);

		swipeLayout.setOnLoadListener(this);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColor(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		new getNews().execute();
		return view;
	}
	
	private class getNews extends AsyncTask<String, integer, String> {

		@Override
		protected void onPreExecute() {
			data.clear();
			progressDialog = ProgressDialog
					.show(getActivity(), "正在获取新闻", "稍等片刻");
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				if (isCache) {
					ArrayList<Article> articles = manager.query();
					for (Article article : articles) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("title", article.title);
						map.put("topic", article.picurl);
						map.put("intro", article.intro);
						map.put("href", article.href);
						data.add(map);
					}
				} else {
					if (Utility.isNetworkConnected(mcontext)) {
						String url="http://www.cnbeta.com/";
						Rowdata=Jsoup.connect(url).timeout(100000).get().toString();
						
						parseData(Rowdata, true);
						ArrayList<Article> articles = new ArrayList<Article>();
						for (int i = 0; i < data.size(); i++) {
							Article article = new Article();
							article.title = (String) data.get(i).get("title");
							article.intro = (String) data.get(i).get("intro");
							article.picurl = (String) data.get(i).get("topic");
							article.href = (String) data.get(i).get("href");
							articles.add(article);
						}
						manager.add(articles);
						isCache = true;
						preferences.edit().putBoolean("isCache", isCache)
								.commit();

					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (progressDialog != null) {
				progressDialog.cancel();
			}
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}

	}

	private class getNewsOnRefresh extends AsyncTask<String, integer, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				String url="http://www.cnbeta.com/";
				Newdata = Jsoup.connect(url).timeout(100000).get().toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			try {
				parseData(Newdata, true);
				
				for (int i = 0; i < data.size(); i++) {
					Article article = new Article();
					article.title = (String) data.get(i).get("title");
					article.intro = (String) data.get(i).get("intro");
					article.picurl = (String) data.get(i).get("topic");
					article.href = (String) data.get(i).get("href");
					manager.updateArticle(article, i + 1);
				}
				adapter.notifyDataSetChanged();
				swipeLayout.setRefreshing(false);

			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			super.onPostExecute(result);
		}
	}

	public void parseData(String Rowdata, boolean isClear) throws JSONException {
		if (Rowdata!=null) {
			Document document = Jsoup.parse(Rowdata);
			Element allitem = null;
			if (isClear) {
				data.clear();
			}
			if (!isCache) {
				Element allnews_all = document.getElementById("allnews_all");
				allitem = allnews_all.select("div.items_area").first();
			} else {
				allitem = document;
			}
			if (document != null) {
				Elements items = allitem.getElementsByClass("item");
				for (Element item : items) {
					Map<String, Object> map = new HashMap<String, Object>();
					Element titleEle = item.select("a").first();
					if (titleEle != null) {
						String title = titleEle.text();
						String href = titleEle.attr("href");
						Element picEle = item.select("img").first();
						String picSrc = picEle.attr("src");
						Element infoEle = item.select("span.newsinfo").first();
						String info = infoEle.html();
						info = info.replaceAll("<.\\w*>|<a.+\">|&nbsp;|<div.+>|<span.+>", "");
						map.put("title", title);
						map.put("topic", picSrc);
						map.put("intro", info);
						map.put("href", href);
						data.add(map);
					}
				}
			}
		}
	}
	
	
	@Override
	public void onLoad() {
		Toast.makeText(mcontext, "没有更多了", Toast.LENGTH_SHORT).show();
		swipeLayout.setLoading(false);
	}

	@Override
	public void onRefresh() {
		if (!Utility.isNetworkConnected(mcontext)) {
			Toast.makeText(mcontext, "网络有问题", Toast.LENGTH_SHORT).show();
			swipeLayout.setRefreshing(false);
		} else {
			new getNewsOnRefresh().execute();
		}
	}
}
