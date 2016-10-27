package com.kg.base.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kg.base.util.NetUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by yuandong on 2016/10/23 0023.
 */
public class VolleyUtils {

    private static final String TAG = VolleyUtils.class.getSimpleName();

    private static final int VOLLEY_TIMEOUT = 30 * 1000;// 连接超时时间
    private static final int VOLLEY_REQUEST_TIMES = 1;// 请求次数
    private static ProgressDialog progressDialog;

    /**
     * post 请求数据
     *
     * @param context      指向当前activity
     * @param url          访问接口
     * @param params       post 请求参数
     * @param mClass       返回实体类型
     * @param isShowDialog 是否显示加载框
     * @param callBack     访问回调
     */
    public static <T> void post(Context context, String url, Map<String, String> params, Class<T> mClass,
                                boolean isShowDialog, final ResultCallBack callBack) {

        //判断有没有网络
        if (!NetUtils.checkNetWork(context)) {
            Toast.makeText(context, "没有网络", Toast.LENGTH_SHORT).show();
            return;
        }

        //创建加载框

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        //如果需要则显示
        if (isShowDialog) {
            progressDialog = ProgressDialog.show(context, "", "Loading...");
            progressDialog.setCancelable(true);
        }

        //数据访问
        GsonRequest<T> request = new GsonRequest<T>(url, params, mClass, new Response.Listener<T>() {
            @Override
            public void onResponse(T t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                callBack.onSuccess(t);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                callBack.onFail(volleyError.getMessage());
            }
        });

        //设置访问超时时间
        request.setRetryPolicy(new DefaultRetryPolicy(VOLLEY_TIMEOUT,
                VOLLEY_REQUEST_TIMES, 1.0f));

        VolleyController.newInstance().addToRequestQueue(request);
    }

    /**
     * 单个文件上传
     * @param context      指向当前activity
     * @param url          访问接口
     * @param params       post 请求参数
     * @param file         上传的文件
     * @param fileName     上传文件名称
     * @param mClass       返回实体类型
     * @param isShowDialog 是否显示加载框
     * @param callBack     访问回调
     */

    public static <T> void upLoad(Context context, String url, Map<String, String> params,
                                  File file, String fileName, Class<T> mClass, boolean isShowDialog,
                                  final ResultCallBack callBack) {

        //判断有没有网络
        if (!NetUtils.checkNetWork(context)) {
            Toast.makeText(context, "没有网络", Toast.LENGTH_SHORT).show();
            return;
        }

        //创建加载框
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        //如果需要则显示
        if (isShowDialog) {
            progressDialog = ProgressDialog.show(context, "", "Uploading...");
            progressDialog.setCancelable(true);
        }

        MultipartRequest<T> request = new MultipartRequest<T>(url, fileName, file, params, mClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T t) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        callBack.onSuccess(t);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        callBack.onFail(volleyError.getMessage());
                    }
                });
        VolleyController.newInstance().addToRequestQueue(request);
    }


}
