package com.szkct.weloopbtsmartdevice.login;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.szkct.weloopbtsmartdevice.data.DataEntity;

/**
 * Created by ${Justin} on 2017/12/15.
 */
public abstract class MyCallback<T> extends RequestCallBack<T> {
    private static final String TAG = MyCallback.class.getSimpleName();
    /**
     * 服务器特殊异常情况,如网络错误等
     */
    private static final int ERROR = 10000;


    @Override
    public void onSuccess(ResponseInfo<T> responseInfo) {

        DataEntity tempData = null;
//        try {
        tempData = new Gson().fromJson(responseInfo.result.toString(), DataEntity.class);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logg.e(TAG, "onSuccess: 这代码是永远不会执行的="+responseInfo.result.toString());
//            tempData = new DataEntity();
//        }

        //T_FLAG 2018/1/29 当用户没有数据时,服务回成功的指令但什么数据都没带,服务端改了之后这代码可以删除
        if (tempData == null) {
            DataError<String> error = new DataError();
            error.setCode(ERROR);
            error.setData("onSuccess: 服务器异常");
            myFailure(error);
            return;
        }

        if (tempData.getCode() == 0) {
            mySuccess(responseInfo.result);
        } else {
            DataError error = new Gson().fromJson(responseInfo.result.toString(), DataError.class);
            myFailure(error);
        }
    }

    @Override
    public void onFailure(HttpException e, String s) {
        DataError<String> error = new DataError();
        error.setCode(ERROR);
        error.setData(s + " HttpException=" + s);
        myFailure(error);
    }

    public abstract void mySuccess(T responseInfo);

    public abstract void myFailure(DataError error);

}
