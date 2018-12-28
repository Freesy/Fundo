package com.szkct.weloopbtsmartdevice.net;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mtk.app.applist.FileUtils;
import com.szkct.weloopbtsmartdevice.data.BaseEntity;
import com.szkct.weloopbtsmartdevice.data.WatchInfoData;
import com.szkct.weloopbtsmartdevice.data.greendao.dao.WatchInfoDataDao;
import com.szkct.weloopbtsmartdevice.util.Base64Utils;
import com.szkct.weloopbtsmartdevice.util.DBHelper;
import com.szkct.weloopbtsmartdevice.util.MessageEvent;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.dao.internal.TableStatements;
import de.greenrobot.dao.query.Query;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

import static com.facebook.stetho.inspector.network.ResponseHandlingInputStream.TAG;
import static com.szkct.weloopbtsmartdevice.main.MainService.ALARM_CLOCK;
import static com.szkct.weloopbtsmartdevice.main.MainService.ASSISTANT_INPUT;
import static com.szkct.weloopbtsmartdevice.main.MainService.AUTO_HEART;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.BT_CALL;
import static com.szkct.weloopbtsmartdevice.main.MainService.CALL_NOTIFICATION;
import static com.szkct.weloopbtsmartdevice.main.MainService.CAMEAR;
import static com.szkct.weloopbtsmartdevice.main.MainService.DIAL_PUSH;
import static com.szkct.weloopbtsmartdevice.main.MainService.FAZE_MODE;
import static com.szkct.weloopbtsmartdevice.main.MainService.FIND_DEVICE;
import static com.szkct.weloopbtsmartdevice.main.MainService.FIRMWARE_SUPPORT;
import static com.szkct.weloopbtsmartdevice.main.MainService.GESTURE_CONTROL;
import static com.szkct.weloopbtsmartdevice.main.MainService.ISSYNWATCHINFO;
import static com.szkct.weloopbtsmartdevice.main.MainService.REMIND_MODE;
import static com.szkct.weloopbtsmartdevice.main.MainService.SEDENTARY_CLOCK;
import static com.szkct.weloopbtsmartdevice.main.MainService.SMS_NOTIFICATION;
import static com.szkct.weloopbtsmartdevice.main.MainService.SOS_CALL;
import static com.szkct.weloopbtsmartdevice.main.MainService.UNIT;
import static com.szkct.weloopbtsmartdevice.main.MainService.WATER_CLOCK;
import static com.szkct.weloopbtsmartdevice.main.MainService.WECHAT_SPORT;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLOOD_OXYGEN;
import static com.szkct.weloopbtsmartdevice.main.MainService.SPORT;
import static com.szkct.weloopbtsmartdevice.main.MainService.PRESSURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.MESSAGE_PUSH;
import static com.szkct.weloopbtsmartdevice.main.MainService.ANTI_LOST;
import static com.szkct.weloopbtsmartdevice.main.MainService.QR_CODE;
import static com.szkct.weloopbtsmartdevice.main.MainService.WEATHER_PUSH;
import static com.szkct.weloopbtsmartdevice.main.MainService.HEART;
import static com.szkct.weloopbtsmartdevice.main.MainService.POINTER_CALIBRATION;
import static com.szkct.weloopbtsmartdevice.main.MainService.SLEEP;
import static com.szkct.weloopbtsmartdevice.main.MainService.FAPIAO;
import static com.szkct.weloopbtsmartdevice.main.MainService.SHOUKUANEWM;
import static com.szkct.weloopbtsmartdevice.main.MainService.BLEMUSIC;
import static com.szkct.weloopbtsmartdevice.main.MainService.ECG;
import static com.szkct.weloopbtsmartdevice.main.MainService.BODYTEMPERATURE;
import static com.szkct.weloopbtsmartdevice.main.MainService.WECHAT_SPORT;
public class HTTPController {
	
	private static HTTPController hc = null;
	private HTTPController(){};
	
	private RequestQueue mRequestQueue = null;
	private String data = "";
	
	public static synchronized HTTPController getInstance(){
		if(hc == null){
			hc = new HTTPController();
		}
		return hc;
	}
	
	public void open(Context context){
		if(mRequestQueue == null){
			mRequestQueue = Volley.newRequestQueue(context);
		}
	}
	
	/** 
	 * 异步请求网络数据 
	 * 
	 * @param
	 * @param
	 * @param url 
	 * @param handler
	 * @param handlerWhat 
	 */  
	public void getNetworkData(final String url,final Handler handler,final int handlerWhat){
		StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = handlerWhat;
				msg.obj = response;
				handler.sendMessage(msg);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = handlerWhat;
				Log.e("error", error.toString()+"22222");
				msg.obj = "1";
				handler.sendMessage(msg);
			}
		});
		mRequestQueue.add(sr);
	}

	/**
	 * 异步请求网络数据 StringRequest GET
	 *
	 * @param url         请求链接
	 * @param handler     用于接受返回值的Handler
	 * @param handlerWhat Handler中Message.what类型
	 */
	public void getNetworkStringData(final String url, final Handler handler, final int handlerWhat) {
		StringRequest sr = new StringRequest(Request.Method.GET, url, new Listener<String>() {
			@Override
			public void onResponse(String response) {  // {"HeWeather5":[{"basic":{"city":"南山","cnty":"中国","id":"CN101280604","lat":"22.53122139","lon":"113.92942810","prov":"广东"},"status":"ok"}]}
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = handlerWhat;
				msg.obj = response;
				handler.sendMessage(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Message msg = handler.obtainMessage();
				msg.what = handlerWhat;
				msg.obj = "-1";
				handler.sendMessage(msg);
			}
		});
		sr.setRetryPolicy(new DefaultRetryPolicy(20*1000, 1, 1.0f));     // todo -- add by lx 20180714
		mRequestQueue.add(sr);
	}
	
	
/*	*//** 
	 * 上传
	 * 
	 * @param url 
	 * @param File 
	 *//* 
	public void upload(String url, File myFile,final Handler handler) {
        RequestParams params = new RequestParams();
        try {
            params.put("Images", myFile);
            System.out.println("myFile = "+myFile);
            AsyncHttpClient client = new AsyncHttpClient();
            
            client.post(url, params, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					String s = new String(arg2);
					System.out.println("成功  s = "+s);
					try {
						JSONObject jsonObj = new JSONObject(s);
						String result = jsonObj.getString("result");
						if(result.equals("0")){
							String face = jsonObj.getString("face");
							face = Constants.URLREVISEPHTOTPREFIX+face;
							Message msg = handler.obtainMessage();
							msg.what = MyDataActivity.PHTOT_UPLOAD;
							msg.obj = face;
							handler.sendMessage(msg);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
					}
				}
				
				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
//					String s = new String(arg2);
					System.out.println("失败  s = ");
//					Message msg = handler.obtainMessage();
//					msg.what = MyDataActivity.PHTOT_UPLOAD;
//					msg.obj = arg2;
//					handler.sendMessage(msg);
				}
			});
            
        } catch(Exception e) {
            
        }
	} */
	
	/** 
	 * 下载图片 
	 * 
	 */ 
	public void downloadImage(final String url,final String bitName,final Handler handler,final int what) {
		ImageRequest imgRequest = new ImageRequest(url, new Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap arg0) {
				saveBitmap(bitName, arg0);
				Message msg = handler.obtainMessage();
				msg.what = what;
				msg.obj = arg0;
				handler.sendMessage(msg);
			}
		}, 0, 0, Config.ARGB_8888, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Log.e("HTTPController", "网络断开");
			}
		});
		mRequestQueue.add(imgRequest);
	}
	
	public void saveBitmap(String bitName,Bitmap mBitmap){
		try {
			FileOutputStream fos = new FileOutputStream(FileUtils.SDPATH+"/"+bitName);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			Log.e("HTTPController", " 图片保存成功");
			Log.e("HTTPController", "PhotoName"+bitName);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * 将字符串转成MD5值 
	 *  
	 * @param string 
	 * @return 
	 */  
	public String stringToMD5(String string) {  
	    byte[] hash;  
	  
	    try {  
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));  
	    } catch (NoSuchAlgorithmException e) {  
	        e.printStackTrace();  
	        return null;  
	    } catch (UnsupportedEncodingException e) {  
	        e.printStackTrace();  
	        return null;  
	    }  
	  
	    StringBuilder hex = new StringBuilder(hash.length * 2);  
	    for (byte b : hash) {  
	        if ((b & 0xFF) < 0x10)  
	            hex.append("0");  
	        hex.append(Integer.toHexString(b & 0xFF));  
	    }  
	  
	    return hex.toString();  
	}

	public void doPostNetWork(String url, final Map<String,String> map,final Handler handler,final int What)
	{
		StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				Message msg = handler.obtainMessage();
				msg.what = What;
				msg.obj = response;
				handler.sendMessage(msg);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.e("HTTPController","网络请求失败");
			}
		}){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return map;
			}
		};
		mRequestQueue.add(stringRequest);
	}

	/**
	 * 添加型号适配 2017.9.20
	 */
	public static void SynWatchInfo(final Context context, final DBHelper db, final int platformCode) {
		/*final List<WatchInfoData> list = new ArrayList<>();
		final JSONObject json = new JSONObject();
		try {
			json.put("number", platformCode);
		} catch (JSONException e) {
			e.printStackTrace();
		}*/
		Query query = db.getWatchInfoDataDao().queryBuilder()
				.where(WatchInfoDataDao.Properties.number.eq(platformCode)).build();
        if(query != null){
            List<WatchInfoData> watchInfoDataList = query.list();
            if (null != watchInfoDataList && watchInfoDataList.size() > 0) {
                WatchInfoData watchInfoData = watchInfoDataList.get(0);
                QR_CODE = "1".equals(watchInfoData.getQrcodenotice());
                WECHAT_SPORT = "1".equals(watchInfoData.getWechatSport());
                AUTO_HEART = "1".equals(watchInfoData.getAutoheart());
                MESSAGE_PUSH = "1".equals(watchInfoData.getAppnotice());
                CALL_NOTIFICATION = "1".equals(watchInfoData.getCallnotice());
                CAMEAR = "1".equals(watchInfoData.getSmartphoto());
                WEATHER_PUSH = "1".equals(watchInfoData.getWeathernotice());
                REMIND_MODE = "1".equals(watchInfoData.getRemindMode());
                BLOOD_OXYGEN = "1".equals(watchInfoData.getOxygen());
                ALARM_CLOCK = "1".equals(watchInfoData.getSmartalarm());
                SMS_NOTIFICATION = "1".equals(watchInfoData.getSmsnotice());
                SPORT = "1".equals(watchInfoData.getSports());
                PRESSURE = "1".equals(watchInfoData.getMeteorology());
                FIRMWARE_SUPPORT = "1".equals(watchInfoData.getFirware());
                SEDENTARY_CLOCK = "1".equals(watchInfoData.getLongsit());
                BLOOD_PRESSURE = "1".equals(watchInfoData.getBlood());
                HEART = "1".equals(watchInfoData.getHeart());
                DIAL_PUSH = "1".equals(watchInfoData.getWatchnotice());
                WATER_CLOCK = "1".equals(watchInfoData.getDrinknotice());
                FAZE_MODE = "1".equals(watchInfoData.getNodisturb());
                GESTURE_CONTROL = "1".equals(watchInfoData.getRaisingbright());
                BT_CALL = "1".equals(watchInfoData.getBtcall());
                UNIT = "1".equals(watchInfoData.getUnitSetup());
                POINTER_CALIBRATION = "1".equals(watchInfoData.getPointerCalibration());
                SLEEP = "1".equals(watchInfoData.getSleep());
                SOS_CALL = "1".equals(watchInfoData.getSos());
                ASSISTANT_INPUT = "1".equals(watchInfoData.getAssistInput());

                FAPIAO = "1".equals(watchInfoData.getFaPiao());
                SHOUKUANEWM = "1".equals(watchInfoData.getShouKuanewm());

				BLEMUSIC = "1".equals(watchInfoData.getBluetoothMusic());
				ECG = "1".equals(watchInfoData.getEcg());
				BODYTEMPERATURE = "1".equals(watchInfoData.getBodyTemperature());
				ISSYNWATCHINFO = true;
                EventBus.getDefault().post(new MessageEvent("update_view"));
            }
        }
		/*Observable.just(db.getWatchInfoDataDao().queryBuilder()
				.where(WatchInfoDataDao.Properties.number.eq(platformCode)).build().list())
				.flatMap(new Function<List<WatchInfoData>, ObservableSource<BaseEntity<WatchInfoData>>>() {
					@Override
					public ObservableSource<BaseEntity<WatchInfoData>> apply(List<WatchInfoData> watchInfoDataList) throws Exception {
						if (null != watchInfoDataList && watchInfoDataList.size() > 0) {
							WatchInfoData watchInfoData = watchInfoDataList.get(0);
							QR_CODE = "1".equals(watchInfoData.getQrcodenotice());
							WECHAT_SPORT = "1".equals(watchInfoData.getWechatSport());
							AUTO_HEART = "1".equals(watchInfoData.getAutoheart());
							MESSAGE_PUSH = "1".equals(watchInfoData.getAppnotice());
							CALL_NOTIFICATION = "1".equals(watchInfoData.getCallnotice());
							CAMEAR = "1".equals(watchInfoData.getSmartphoto());
							WEATHER_PUSH = "1".equals(watchInfoData.getWeathernotice());
							REMIND_MODE = "1".equals(watchInfoData.getRemindMode());
							BLOOD_OXYGEN = "1".equals(watchInfoData.getOxygen());
							ALARM_CLOCK = "1".equals(watchInfoData.getSmartalarm());
							SMS_NOTIFICATION = "1".equals(watchInfoData.getSmsnotice());
							SPORT = "1".equals(watchInfoData.getSports());
							PRESSURE = "1".equals(watchInfoData.getMeteorology());
							FIRMWARE_SUPPORT = "1".equals(watchInfoData.getFirware());
							SEDENTARY_CLOCK = "1".equals(watchInfoData.getLongsit());
							BLOOD_PRESSURE = "1".equals(watchInfoData.getBlood());
							HEART = "1".equals(watchInfoData.getHeart());
							DIAL_PUSH = "1".equals(watchInfoData.getWatchnotice());
							WATER_CLOCK = "1".equals(watchInfoData.getDrinknotice());
							FAZE_MODE = "1".equals(watchInfoData.getNodisturb());
							GESTURE_CONTROL = "1".equals(watchInfoData.getRaisingbright());
							BT_CALL = "1".equals(watchInfoData.getBtcall());
							UNIT = "1".equals(watchInfoData.getUnitSetup());
							POINTER_CALIBRATION = "1".equals(watchInfoData.getPointerCalibration());
							SLEEP = "1".equals(watchInfoData.getSleep());
							SOS_CALL = "1".equals(watchInfoData.getSos());
							ASSISTANT_INPUT = "1".equals(watchInfoData.getAssistInput());
							
							FAPIAO = "1".equals(watchInfoData.getFaPiao());
							SHOUKUANEWM = "1".equals(watchInfoData.getShouKuanewm());
							ISSYNWATCHINFO = true;
							EventBus.getDefault().post(new MessageEvent("update_view"));
							if(!NetWorkUtils.isConnect(context)){
								return Observable.create(new ObservableOnSubscribe<BaseEntity<WatchInfoData>>() {
									@Override
									public void subscribe( ObservableEmitter<BaseEntity<WatchInfoData>> e) throws Exception {
										BaseEntity<WatchInfoData> baseEntity = new BaseEntity<WatchInfoData>();
										baseEntity.setMsgCode(1);
										e.onNext(baseEntity);
									}
								});
							}
							list.addAll(watchInfoDataList);
						}
						return RetrofitFactory.getInstance().getModelAdaption(Base64Utils.getBase64(json.toString()));
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Consumer<BaseEntity<WatchInfoData>>() {
					@Override
					public void accept(BaseEntity<WatchInfoData> watchInfoDataBaseEntity) {
						try {
							Log.i("[HTTPController]", watchInfoDataBaseEntity.toString());
							if (null != watchInfoDataBaseEntity) {
								if (watchInfoDataBaseEntity.isSuccess()) {
									WatchInfoData watchInfoData = watchInfoDataBaseEntity.getData();
									if (list.size() > 0) {
										WatchInfoData watchInfoDataDao = list.get(0);
										watchInfoDataDao.setBoard(watchInfoData.getBoard());
										watchInfoDataDao.setBtcall(watchInfoData.getBtcall());
										watchInfoDataDao.setRaisingbright(watchInfoData.getRaisingbright());
										watchInfoDataDao.setAppnotice(watchInfoData.getAppnotice());
										watchInfoDataDao.setBlood(watchInfoData.getBlood());
										watchInfoDataDao.setAutoheart(watchInfoData.getAutoheart());
										watchInfoDataDao.setCallnotice(watchInfoData.getCallnotice());
										watchInfoDataDao.setDrinknotice(watchInfoData.getDrinknotice());
										watchInfoDataDao.setFirware(watchInfoData.getFirware());
										watchInfoDataDao.setHeart(watchInfoData.getHeart());
										watchInfoDataDao.setWeathernotice(watchInfoData.getWeathernotice());
										watchInfoDataDao.setLongsit(watchInfoData.getLongsit());
										watchInfoDataDao.setWatchnotice(watchInfoData.getWatchnotice());
										watchInfoDataDao.setSports(watchInfoData.getSports());
										watchInfoDataDao.setSmsnotice(watchInfoData.getSmsnotice());
										watchInfoDataDao.setSmartphoto(watchInfoData.getSmartphoto());
										watchInfoDataDao.setQrcodenotice(watchInfoData.getQrcodenotice());
										watchInfoDataDao.setMeteorology(watchInfoData.getMeteorology());
										watchInfoDataDao.setModel(watchInfoData.getModel());
										watchInfoDataDao.setNodisturb(watchInfoData.getNodisturb());
										watchInfoDataDao.setNumber(watchInfoData.getNumber());
										watchInfoDataDao.setOxygen(watchInfoData.getOxygen());
										watchInfoDataDao.setPlatform(watchInfoData.getPlatform());
										watchInfoDataDao.setRemindMode(watchInfoData.getRemindMode());
										watchInfoDataDao.setWechatSport(watchInfoData.getWechatSport());
										watchInfoDataDao.setUpdate_time(System.currentTimeMillis() + "");
										watchInfoDataDao.setUnitSetup(watchInfoData.getUnitSetup());
										watchInfoDataDao.setTimes(watchInfoData.getTimes());
										watchInfoDataDao.setPointerCalibration(watchInfoData.getPointerCalibration());
										watchInfoDataDao.setSleep(watchInfoData.getSleep());
										watchInfoDataDao.setSos(watchInfoData.getSos());
										watchInfoDataDao.setAssistInput(watchInfoData.getAssistInput());

										watchInfoDataDao.setFaPiao(watchInfoData.getFaPiao());
										watchInfoDataDao.setShouKuanewm(watchInfoData.getShouKuanewm());

										db.updataWatchInfoData(watchInfoDataDao);
										list.clear();
									} else {
										watchInfoData.setUpdate_time(System.currentTimeMillis() + "");
										db.saveWatchInfoData(watchInfoData);
									}
									QR_CODE = "1".equals(watchInfoData.getQrcodenotice());
									WECHAT_SPORT = "1".equals(watchInfoData.getWechatSport());
									AUTO_HEART = "1".equals(watchInfoData.getAutoheart());
									MESSAGE_PUSH = "1".equals(watchInfoData.getAppnotice());
									CALL_NOTIFICATION = "1".equals(watchInfoData.getCallnotice());
									CAMEAR = "1".equals(watchInfoData.getSmartphoto());
									WEATHER_PUSH = "1".equals(watchInfoData.getWeathernotice());
									REMIND_MODE = "1".equals(watchInfoData.getRemindMode());
									BLOOD_OXYGEN = "1".equals(watchInfoData.getOxygen());
									ALARM_CLOCK = "1".equals(watchInfoData.getSmartalarm());
									SMS_NOTIFICATION = "1".equals(watchInfoData.getSmsnotice());
									SPORT = "1".equals(watchInfoData.getSports());
									PRESSURE = "1".equals(watchInfoData.getMeteorology());
									FIRMWARE_SUPPORT = "1".equals(watchInfoData.getFirware());
									SEDENTARY_CLOCK = "1".equals(watchInfoData.getLongsit());
									BLOOD_PRESSURE = "1".equals(watchInfoData.getBlood());
									HEART = "1".equals(watchInfoData.getHeart());
									DIAL_PUSH = "1".equals(watchInfoData.getWatchnotice());
									WATER_CLOCK = "1".equals(watchInfoData.getDrinknotice());
									FAZE_MODE = "1".equals(watchInfoData.getNodisturb());
									GESTURE_CONTROL = "1".equals(watchInfoData.getRaisingbright());
									BT_CALL = "1".equals(watchInfoData.getBtcall());
									UNIT = "1".equals(watchInfoData.getUnitSetup());
									POINTER_CALIBRATION = "1".equals(watchInfoData.getPointerCalibration());
									SLEEP = "1".equals(watchInfoData.getSleep());
									SOS_CALL = "1".equals(watchInfoData.getSos());
									ASSISTANT_INPUT = "1".equals(watchInfoData.getAssistInput());
										FAPIAO = "1".equals(watchInfoData.getFaPiao());
									SHOUKUANEWM = "1".equals(watchInfoData.getShouKuanewm());     
									ISSYNWATCHINFO = true;
									EventBus.getDefault().post(new MessageEvent("update_view"));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							if(!TextUtils.isEmpty(e.getMessage())) {
								Log.i("[HTTPController]", e.getMessage());
							}
						}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept( Throwable throwable) throws Exception {
						if(!TextUtils.isEmpty(throwable.getMessage())) {
							Log.i("[HTTPController]", throwable.getMessage());
						}
					}
				});*/
	}
}
