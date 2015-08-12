package com.example.util;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class PicCache {
	private HashMap<String, SoftReference<Bitmap>> imageCache = null;

	public PicCache() {
		imageCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	@SuppressLint("HandlerLeak")
	public Bitmap loadBitmap(final ImageView imageView, final String imageURL,
			final ImageCallBack imageCallBack) {
		if (imageCache.containsKey(imageURL)) {
			SoftReference<Bitmap> reference = imageCache.get(imageURL);
			Bitmap bitmap = reference.get();
			if (bitmap != null) {
				return bitmap;
			}
		} else {
			/*String bitmapName = imageURL
			String bitmapName = imageURL
					.substring(imageURL.lastIndexOf("/") + 1);
			File cacheDir = new File(Environment.getExternalStorageDirectory()+"/DCIM/cnbetaCache");
			File[] cacheFiles = cacheDir.listFiles();
			int i = 0;
			if (null != cacheFiles) {
				for (; i < cacheFiles.length; i++) {
					if (bitmapName.equals(cacheFiles[i].getName())) {
						break;
					}
				}
				if (i < cacheFiles.length) {
					Bitmap bitmap=BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/DCIM/cnbetaCache/"
							+ bitmapName);
					return bitmap;
				}

			}*/
			
		}
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				imageCallBack.imageLoad(imageView, (Bitmap) msg.obj);
			}
		};
		
		new Thread() {
			@Override
			public void run() {
				InputStream bitmapIs = HttpUtils.getStreamFromURL(imageURL);
				Bitmap bitmap = BitmapFactory.decodeStream(bitmapIs);
				imageCache.put(imageURL, new SoftReference<Bitmap>(bitmap));
				Message msg = handler.obtainMessage(0, bitmap);
				handler.sendMessage(msg);

				/*File dir = new File(Environment.getExternalStorageDirectory()+"/DCIM/cnbetaCache");
				File dir = new File(Environment.getExternalStorageDirectory()+"/DCIM/cnbetaCache");
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File bitmapFile = new File(Environment.getExternalStorageDirectory()+"/DCIM/cnbetaCache"
						,imageURL.substring(imageURL.lastIndexOf("/") + 1));
				if (!bitmapFile.exists()) {
					try {
						bitmapFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(bitmapFile);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
			}
		}.start();
		return null;
	}

	public interface ImageCallBack {
		public void imageLoad(ImageView imageView, Bitmap bitmap);
	}
}

