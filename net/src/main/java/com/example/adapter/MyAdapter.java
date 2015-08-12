package com.example.adapter;

import java.util.List;
import java.util.Map;

import com.example.net.R;
import com.example.util.PicCache;
import com.example.util.PicCache.ImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
	
	private PicCache mPicCache;
	private List<Map<String, Object>> list;
	private MyItemClickListener itemClickListener;
	//private LayoutInflater layoutInflater;
	
	public MyAdapter(Context context,List<Map<String, Object>> data) {
		mPicCache=new PicCache();
		//layoutInflater=LayoutInflater.from(context);
		list=data;
	}

	 public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
	        View view = View.inflate(viewGroup.getContext(),
	        		R.layout.list_array, null);
	        ViewHolder holder = new ViewHolder(view,itemClickListener);
	        return holder;
	    }
	 
	 public void setOnItemClickListener(MyItemClickListener listener){
			this.itemClickListener = listener;
		}

	@Override
	public int getItemCount() {
		return list.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		String gtitle=(String) list.get(position).get("title");
		String gintro=(String) list.get(position).get("intro");
		String imageURL=(String) list.get(position).get("topic");
		
		viewHolder.title.setText(gtitle);
		viewHolder.intro.setText(gintro);
		Bitmap bitmap = mPicCache.loadBitmap(viewHolder.image, imageURL,
				new ImageCallBack() {
					public void imageLoad(ImageView imageView, Bitmap bitmap) {
						imageView.setImageBitmap(bitmap);
					}
				});
		if (bitmap == null) {
			viewHolder.image.setImageResource(R.drawable.cnbeta);
		} else {
			viewHolder.image.setImageBitmap(bitmap);
		}
	}
	
	public static class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener{
        private TextView title,intro;
        private ImageView image;
        private MyItemClickListener mListener;
        
        public ViewHolder(View itemView,MyItemClickListener listener) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text);
            intro=(TextView) itemView.findViewById(R.id.intro);
            image=(ImageView) itemView.findViewById(R.id.item_img);
            mListener=listener;
            itemView.setOnClickListener(this);
        }

		@Override
		public void onClick(View v) {
			if(mListener != null){
				mListener.onItemClick(v,getPosition());
			}
		}
    }
	
	public interface MyItemClickListener {
		public void onItemClick(View view,int postion);
	}
	
	
}
