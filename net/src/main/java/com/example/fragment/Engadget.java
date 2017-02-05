package com.example.fragment;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.adapter.EngadgetAdapter;
import com.example.net.ApiReader;
import com.example.net.NewsReader;
import com.example.net.R;
import com.example.swiperefresh.SwipeRefreshLayout;
import com.example.swiperefresh.SwipeRefreshLayout.OnLoadListener;
import com.example.swiperefresh.SwipeRefreshLayout.OnRefreshListener;
import com.example.util.LruBitmapCache;
import com.example.util.Utility;

import android.R.integer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private static Engadget mInstance;
	private String zhihu="http://news-at.zhihu.com/api/4/news/";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.engadget, null);
		mInstance=this;
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
				String href = (String) data.get(arg2).get("href");
				String title = (String) data.get(arg2).get("title");
				Intent intent = new Intent(getActivity(), ApiReader.class);
				intent.putExtra("href", zhihu+href);
				intent.putExtra("title", title);
				intent.putExtra("comment",false);
				startActivity(intent);
			}
		});
		return view;
	}

	public static synchronized Engadget getInstance(){
		return mInstance;
	}

	public RequestQueue getRequestQueue(){
		if (mRequestQueue==null){
			mRequestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
		}
		return mRequestQueue;
	}

	public ImageLoader getImageLoader(){
		getRequestQueue();
		if (mImageLoader==null){
			mImageLoader=new ImageLoader(this.mRequestQueue,new LruBitmapCache());

		}
		return mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req,String tag){
		req.setTag(TextUtils.isEmpty(tag) ? "test tag" : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req){
		req.setTag("test tag");
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag){
		if (mRequestQueue!=null){
			mRequestQueue.cancelAll(tag);
		}
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

			JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, zhihu+"latest", null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							try {
								String date=response.getString("date");
								JSONArray stories=response.getJSONArray("stories");
								JSONArray top_stories=response.getJSONArray("top_stories");
								for (int i=0;i<stories.length();i++){
									Map<String, Object> map = new HashMap<String, Object>();
									JSONObject object= (JSONObject) stories.get(i);
									String id=object.getString("id");
									String image= (String) object.getJSONArray("images").get(0);
									//String ga_prefix=object.getString("ga_prefix");
									String title=object.getString("title");
									map.put("title", title);
									map.put("pic", image);
									map.put("href", id);
									data.add(map);
								}
								for (int i=0;i<top_stories.length();i++){
									JSONObject object= (JSONObject) top_stories.get(i);

								}
								adapter.notifyDataSetChanged();
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

}
