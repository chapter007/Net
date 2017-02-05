package com.example.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.adapter.MyAdapter;
import com.example.adapter.MyAdapter.MyItemClickListener;
import com.example.net.NewsReader;
import com.example.net.R;
import com.example.swiperefresh.SwipeRefreshLayout;
import com.example.swiperefresh.SwipeRefreshLayout.OnLoadListener;
import com.example.swiperefresh.SwipeRefreshLayout.OnRefreshListener;

import android.R.integer;

import com.example.util.Utility;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class CnbetaTop extends Fragment implements
		 OnRefreshListener,OnLoadListener {
	private RecyclerView list;
	private SwipeRefreshLayout swipeLayout;
	private MyAdapter adapter;
	private int count = 0;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.activity_popular, container,false);
		list = (RecyclerView) view.findViewById(R.id.list_popular);
		swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_pop);

		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        list.setLayoutManager(layoutManager);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setOnLoadListener(this);
		swipeLayout.setColor(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);

		list.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(Rect outRect, View view,
					RecyclerView parent, RecyclerView.State state) {
				super.getItemOffsets(outRect, view, parent, state);
				outRect.set(0, 0, 0, 20);
			}
		});

		if (Utility.isNetworkConnected(getActivity())) {
			new getNews().execute();
		} else {
			Toast.makeText(getActivity(), "网络未连接", Toast.LENGTH_LONG).show();
		}

		adapter = new MyAdapter(getActivity(), data);
		list.setAdapter(adapter);
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
		return view;
	}

	public class getNews extends AsyncTask<String, integer, String> {

		@Override
		protected void onPreExecute() {
			swipeLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		int i = 0;
		@Override
		protected String doInBackground(String... params) {
			try {
				data.clear();
				Document document = Jsoup.connect(
						"http://www.cnbeta.com/top10.htm").timeout(100000).get();
				Element hit_rank = document.select("div#hits_rank").first();
				Elements items=hit_rank.select("div.item");
				for (Element item : items) {
					Map<String, Object> map = new HashMap<String, Object>();
					Element titleEle = item.select("a").first();
					String title = titleEle.text();
					String href = titleEle.attr("href");
					Element picEle = item.select("img").first();
					String picSrc = picEle.attr("src");
					Element infoEle = item.select("span.newsinfo").first();
					String info = infoEle.html();
					info = info.replaceAll("<.\\w*>||&nbsp", "");
					map.put("title", title);
					map.put("topic", picSrc);
					map.put("intro", info);
					map.put("href", href);
					data.add(map);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			count = i;
			adapter.notifyDataSetChanged();
			swipeLayout.setRefreshing(false);
			super.onPostExecute(result);
		}
	}

	@Override
	public void onRefresh() {
		Toast.makeText(getActivity(), "无新内容", Toast.LENGTH_SHORT)
				.show();
		swipeLayout.setRefreshing(false);
	}

	@Override
	public void onLoad() {
		Toast.makeText(getActivity(), "正在刷新", Toast.LENGTH_SHORT)
		.show();
		swipeLayout.setLoading(false);
	}

}
