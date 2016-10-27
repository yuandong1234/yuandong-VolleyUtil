package com.kg.base.http;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kg.base.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;


/**
 * Created by yuandong on 2016/10/22 0022.
 */
public class GsonRequest<T> extends Request<T> {

    private final Response.Listener<T> mListener;
    private static Gson mGson = new Gson();
    private Class<T> mClass;
    private Map<String, String> mParams;//post 请求参数
    private TypeToken<T> mTypeToken;//用于json 数据集合

    public GsonRequest(int method, Map<String, String> params, String url,
                       Class<T> clazz, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mClass = clazz;
        mListener = listener;
        mParams = params;

        LogUtils.i("访问地址URL", getUrlAndParams(url));
    }

    public GsonRequest(int method, Map<String, String> params,
                       String url, TypeToken<T> typeToken, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        mTypeToken = typeToken;
        mListener = listener;
        mParams = params;
        LogUtils.i("访问地址URL", getUrlAndParams(url));
    }


    /**
     * get 请求 ,返回是单个对象
     *
     * @param url           访问接口
     * @param clazz         返回实体类（单个对象）
     * @param listener      访问成功监听
     * @param errorListener 访问失败监听
     */
    public GsonRequest(String url, Class<T> clazz, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.GET, null, url, clazz, listener, errorListener);
    }


    /**
     * get 请求  返回是对象集合
     *
     * @param url           访问接口
     * @param typeToken     返回实体类数组（对象数组）
     * @param listener      访问成功监听
     * @param errorListener 访问失败监听
     */
    public GsonRequest(String url, TypeToken<T> typeToken,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {

        this(Method.GET, null, url, typeToken, listener, errorListener);

    }


    /**
     * post 请求 传参数
     *
     * @param url           访问接口
     * @param params        参数
     * @param clazz         返回实体类
     * @param listener      访问成功监听
     * @param errorListener 访问失败监听
     */

    public GsonRequest(String url, Map<String, String> params, Class<T> clazz
            , Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        this(Method.POST, params, url, clazz, listener, errorListener);
    }


    /**
     * 用于post请求参数
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mParams == null ? super.getParams() : mParams;
    }

    /**
     * 用于header头信息
     *
     * @return
     * @throws AuthFailureError
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        //设置访问自己服务器时必须传递的参数，密钥等
//        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("Charset", "UTF-8");
//        headers.put("Content-Type", "application/x-javascript");
//        headers.put("Accept-Encoding", "gzip,deflate");
//        return headers;
        return super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            LogUtils.json("json 数据",jsonString);

            if (mTypeToken == null)
                return Response.success(mGson.fromJson(jsonString, mClass),
                        HttpHeaderParser.parseCacheHeaders(response));//用Gson解析返回Java对象
            else
                return (Response<T>) Response.success(mGson.fromJson(jsonString, mTypeToken.getType()),
                        HttpHeaderParser.parseCacheHeaders(response));//通过构造TypeToken让Gson解析成自定义的对象类型

        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T t) {
        mListener.onResponse(t);
    }


    //拼接url和参数
    private String getUrlAndParams(String url) {

        StringBuilder requestUrl = new StringBuilder(url);

        requestUrl.append('?');

        if (mParams != null && mParams.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : mParams.entrySet()) {
                builder.append(entry.getKey());
                builder.append('=');
                builder.append(entry.getValue());
                builder.append('&');
            }
            String params = builder.toString();
            requestUrl.append(params);
        }
        String tempUrl = requestUrl.toString();
        return tempUrl.substring(0, tempUrl.length() - 1);
    }
}
