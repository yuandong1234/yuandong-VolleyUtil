package com.kg.base.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.kg.base.util.LogUtils;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.toolsfinal.Logger;

/**
 * Created by yuandong on 2016/10/25.
 * 用于图片上传
 */
public class MultipartRequest<T> extends Request<T>{

    private static String TAG=MultipartRequest.class.getSimpleName();

    private MultipartEntity entity = new MultipartEntity();
    private final Response.Listener<T> mListener;
    private List<File> mFileParts;
    private String mFilePartName;
    private Map<String, String> mParams;
    private static Gson mGson = new Gson();//用于解析数据
    private Class<T> mClass;

    /**
     * 单个文件
     * @param url
     * @param filePartName
     * @param file
     * @param params
     * @param clazz
     * @param errorListener
     * @param listener
     */
    public MultipartRequest(String url,  String filePartName, File file,
                            Map<String, String> params,Class<T> clazz, Response.Listener<T> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);

        mFileParts = new ArrayList<File>();
        if (file != null) {
            mFileParts.add(file);
        }
        mFilePartName = filePartName;
        mListener = listener;
        mParams = params;
        mClass=clazz;
        buildMultipartEntity();
    }


    /**
     * 多个文件，对应一个key
     * @param url
     * @param filePartName
     * @param files
     * @param params
     * @param clazz
     * @param errorListener
     * @param listener
     */
    public MultipartRequest(String url, String filePartName, List<File> files,
                            Map<String, String> params,Class<T> clazz,
                            Response.ErrorListener errorListener,
                            Response.Listener<T> listener) {
        super(Method.POST, url, errorListener);
        mFilePartName = filePartName;
        mListener = listener;
        mFileParts = files;
        mParams = params;
        mClass=clazz;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        if (mFileParts != null && mFileParts.size() > 0) {
            for (File file : mFileParts) {
                entity.addPart(mFilePartName, new FileBody(file));
            }
            long l = entity.getContentLength();
            LogUtils.e("上传图片",mFileParts.size()+"个，长度："+l);
        }

        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(TAG,"UnsupportedEncodingException");
        }
    }


    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            LogUtils.e(TAG,"IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        VolleyLog.d("getHeaders");
        Map<String, String> headers = super.getHeaders();
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        return headers;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        LogUtils.e(TAG,"parseNetworkResponse");

        if (VolleyLog.DEBUG) {
            if (response.headers != null) {
                for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                    LogUtils.e(TAG,entry.getKey() + "=" + entry.getValue());
                }
            }
        }

        String jsonString;

        try {
            jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            LogUtils.json(TAG,"返回数据："+jsonString);
        } catch (UnsupportedEncodingException e) {
            jsonString = new String(response.data);
            LogUtils.json(TAG,"返回数据："+jsonString);
        }

        return Response.success(mGson.fromJson(jsonString, mClass), HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(T t) {
        mListener.onResponse(t);
    }

}
