# yuandong-VolleyUtil
一个基于volley开源框架的二次封装网络访问工具

举一个例子：post请求

    //建立一个Map集合，用于存放post参数
    Map<String,String> params=new HashMap<>();
    params.put("key","value");
    params.put("key","value");
    VolleyUtils.post(context,"url",params,xxxx.class,true,new ResultCallBack<xxxx.class>(){

            @Override
            public void onSuccess(xxxx.class result) {
                //访问成功返回
            }

            @Override
            public void onFail(String result) {
                //访问失败返回
            }
        });
                
