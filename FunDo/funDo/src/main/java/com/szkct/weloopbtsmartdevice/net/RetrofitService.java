package com.szkct.weloopbtsmartdevice.net;

import com.szkct.weloopbtsmartdevice.data.BaseEntity;
import com.szkct.weloopbtsmartdevice.data.DataEntity;
import com.szkct.weloopbtsmartdevice.data.WatchInfoData;
import com.szkct.weloopbtsmartdevice.login.UpImgData;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface RetrofitService {

    @GET("adaptiveFromApp/requestAdaptive.do?")
    Observable<BaseEntity<WatchInfoData>> getModelAdaption(@Query("params") String params);

    @GET("adaptiveFromApp/request.do?")
    Observable<BaseEntity<List<WatchInfoData>>> getModelAdaptions(@QueryMap HashMap<String,Object> map);

    //上传头像
    @Multipart
    @POST("user/uploadHeadImage.do")
    Observable<DataEntity<UpImgData>> uploadHeader(@PartMap HashMap<String, RequestBody> map, @Part MultipartBody.Part part);
}
