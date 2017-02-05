package com.example.util;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by zhangjie on 2016/2/12.
 */
public class LruBitmapCache extends LruCache<String,Bitmap> implements ImageLoader.ImageCache{
    public LruBitmapCache(int maxSize) {
        super(maxSize);
    }

    public LruBitmapCache(){
        this(getDefaultLruCacheSize());
    }

    public static int getDefaultLruCacheSize(){
        int maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        return cacheSize;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes()*value.getHeight()/1024;
    }

    @Override
    public Bitmap getBitmap(String s) {
        return get(s);
    }

    @Override
    public void putBitmap(String s, Bitmap bitmap) {
        put(s,bitmap);
    }
}
