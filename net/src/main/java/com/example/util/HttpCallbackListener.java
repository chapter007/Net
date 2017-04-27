package com.example.util;

/**
 * Created by zhangjie on 2017/4/27.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
