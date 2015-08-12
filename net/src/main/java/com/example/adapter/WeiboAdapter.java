package com.example.adapter;

import java.util.List;
import java.util.Map;

import com.example.net.R;
import com.example.util.PicCache;
import com.example.util.PicCache.ImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WeiboAdapter extends BaseAdapter{

	private List<Map<String, Object>> list;
	private Context context;
	private PicCache mPicCache;
	private TextView userid;
	private ImageView userpic;
	private WebView weibo;
	
	public WeiboAdapter(List<Map<String, Object>> mlist,Context mcontext) {
		context=mcontext;
		list=mlist;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.weibo_info, null);
		}
		userid=(TextView) convertView.findViewById(R.id.user_id);
		userpic=(ImageView) convertView.findViewById(R.id.user_pic);
		weibo=(WebView) convertView.findViewById(R.id.weibo_content);
		userid.setText((CharSequence) list.get(position).get("userid"));
		String imageURL=(String) list.get(position).get("userpic");
		String weiboString=(String) list.get(position).get("weibocontent");
		weibo.loadDataWithBaseURL("", weiboString, "text/html", "gbk", "");
		Bitmap bitmap = mPicCache.loadBitmap(userpic, imageURL,
				new ImageCallBack() {
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if (bitmap == null) {
			userpic.setImageResource(R.drawable.eng_loading);
		} else {
			userpic.setImageBitmap(bitmap);
		}
		return convertView;
	}

}
