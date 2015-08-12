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

import com.example.adapter.EngadgetAdapter;
import com.example.net.NewsReader;
import com.example.net.R;
import com.example.swiperefresh.SwipeRefreshLayout;
import com.example.swiperefresh.SwipeRefreshLayout.OnLoadListener;
import com.example.swiperefresh.SwipeRefreshLayout.OnRefreshListener;
import com.example.util.Utility;

import android.R.integer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class Engadget extends Fragment implements OnRefreshListener,OnLoadListener{

	private ListView engadget_list;
	private SwipeRefreshLayout swipeLayout;
	private EngadgetAdapter adapter;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private Document document;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.engadget, null);
		engadget_list = (ListView) view.findViewById(R.id.engadget_list);
		swipeLayout = (SwipeRefreshLayout) view
				.findViewById(R.id.swipe_refresh_engadget);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setOnLoadListener(this);
		swipeLayout.setColor(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		
		if (Utility.isNetworkConnected(getActivity())) {
			new getEngadget().execute();
		} else {
			Toast.makeText(getActivity(), "网络有问题", Toast.LENGTH_LONG).show();
		}
		adapter=new EngadgetAdapter(getActivity(), data);
		engadget_list.setAdapter(adapter);
		engadget_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String href=(String) data.get(arg2).get("href");
				String title=(String) data.get(arg2).get("title");
				Intent intent = new Intent(getActivity(), NewsReader.class);
				intent.putExtra("href", href);
				intent.putExtra("title",title);
				startActivity(intent);
			}
		});
		return view;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
		//((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("ENGADGET");
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onRefresh() {
		Toast.makeText(getActivity(), "暂时没有更多", Toast.LENGTH_SHORT).show();
		swipeLayout.setRefreshing(false);
	}

	public class getEngadget extends AsyncTask<String, integer, String> {

		@Override
		protected void onPreExecute() {
			swipeLayout.setRefreshing(true);
			super.onPreExecute();
		}

		
		@Override
		protected String doInBackground(String... params) {
			//String url="http://cn.engadget.com/";
			try {
				document = Jsoup.connect("http://cn.engadget.com/").timeout(100000).get();
                Elements articles = document.select("article.post");
				for (Element article : articles) {
					Map<String, Object> map = new HashMap<String, Object>();
					String title = article.select("a[itemprop]").first().text();
					String href=article.select("a[itemprop]").first().attr("href");
					String pic=null;
					if (article.select("img[alt]").first()!=null) {
						pic = article.select("img[alt]").first().attr("src");						
					}else {
						pic = article.select("div>a[href]>img").first().attr("src");
					}
					map.put("title", title);
					map.put("pic", pic);
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
			swipeLayout.setRefreshing(false);
			adapter.notifyDataSetChanged();
			super.onPostExecute(result);
		}
	}

	@Override
	public void onLoad() {
		Toast.makeText(getActivity(), "暂无更多", Toast.LENGTH_SHORT).show();
		swipeLayout.setLoading(false);
	}
	
	/*public class myStringRequest extends StringRequest{

		public myStringRequest(int method, String url,
				Listener<String> listener, ErrorListener errorListener) {
			super(method, url, listener, errorListener);
		}
		
		@Override
		protected Response<String> parseNetworkResponse(NetworkResponse response) {
			 String str = null;
		        try {
		            str = new String(response.data,"utf-8");
		        } catch (UnsupportedEncodingException e) {
		            e.printStackTrace();
		        }
			return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
		}
	}*/
	
}
