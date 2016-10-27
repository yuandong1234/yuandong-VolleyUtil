package com.kg.base.http;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kg.AppContext.MyApplication;


/**
 * Created by yuandong on 2016/10/23 0023.
 */
public class VolleyController {


    // 创建一个TAG，方便调试或Log
    private static final String TAG = "VolleyController";
    // 创建一个全局的请求队列
    private RequestQueue mRequestQueue;
    // 创建一个static ApplicationController对象，便于全局访问
    private static VolleyController mInstance;
    private Context mContext;


    private VolleyController() {
    }
    private static class LazyHolder {
        private static VolleyController instance = new VolleyController();
    }

    // 用于返回一个VolleyController单例
    public static VolleyController newInstance() {
        return LazyHolder.instance;
    }


    // 用于返回全局RequestQueue对象，如果为空则创建它
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(MyApplication.mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // 如果tag为空的话，就是用默认TAG
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    // 通过各Request对象的Tag属性取消请求
    public void cancelRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
