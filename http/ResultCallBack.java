package com.kg.base.http;

/**
 * Created by yuandong on 2016/10/23 0023.
 */
public interface ResultCallBack<T> {

    public void onSuccess(T t);
    public void onFail(String result);
}
