package com.szkct.map.utils;

import android.graphics.Bitmap;
import android.location.LocationManager;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

/**
 * 作者：xiaodai.
 * 2016/11/29.
 * 版本：v1.0
 */

public class Util {

    public static int SPORT_STATUS = 0;

    /**
     * 计算两点之间距离
     *
     * @param start
     * @param end
     * @return 米
     */
    public static double getDistance(LatLng start, LatLng end) {
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;

//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        //地球半径
        double R = 6371;

        //两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos( Math.sin( lat1 ) * Math.sin( lat2 ) + Math.cos( lat1 ) * Math.cos( lat2 ) * Math.cos( lon2 - lon1 ) ) * R;

        return d*1000 ;
    }

    /**
     * View 转bitmap
     * @param view
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure( View.MeasureSpec.makeMeasureSpec( 0, View.MeasureSpec.UNSPECIFIED ),
                View.MeasureSpec.makeMeasureSpec( 0, View.MeasureSpec.UNSPECIFIED ) );
        view.layout( 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight() );
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }




    // 判断GPS状态
    public static boolean isGpsEnabled(LocationManager locationManager) {
        boolean isOpenGPS = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // boolean isOpenNetwork = locationManager
        // .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isOpenGPS) {
            return true;
        }
        return false;
    }



}
