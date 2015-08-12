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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EngadgetAdapter extends BaseAdapter {

	private List<Map<String, Object>> list;
	private Context context;
	private PicCache mPicCache;
	private TextView title;
	private ImageView image;

	public EngadgetAdapter(Context mContext, List<Map<String, Object>> data) {
		list = data;
		context = mContext;
		mPicCache=new PicCache();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.engadget_info, null);
		}
		title=(TextView) convertView.findViewById(R.id.engadget_title);
		image=(ImageView) convertView.findViewById(R.id.engadget_img);
		//Log.i("title:", ""+title);
		title.setText((CharSequence) list.get(position).get("title"));
		String imageURL=(String) list.get(position).get("pic");
		Bitmap bitmap = mPicCache.loadBitmap(image, imageURL,
				new ImageCallBack() {
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if (bitmap == null) {
			image.setImageResource(R.drawable.eng_loading);
		} else {
			image.setImageBitmap(bitmap);
		}
		return convertView;
	}

}
