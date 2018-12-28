/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtk.app.remotecamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.mediatek.camera.service.RemoteCameraController;
import com.mediatek.ctrl.sync.DataSyncController;
import com.szkct.bluetoothgyl.BluetoothMtkChat;
import com.szkct.bluetoothgyl.L2Send;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MainService;
import com.szkct.weloopbtsmartdevice.util.DateUtil;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

// ----------------------------------------------------------------------

public class RemoteCamera extends Activity implements RemoteCameraService.CustomCameraListener {
    private final String TAG = "AppManager/Camera";

    private Preview mPreview;

    private Camera mCamera;

    private int numberOfCameras;

    static int ratation = 0;

    private RemoteCameraController mController = RemoteCameraController.getInstance();

    // private int cameraCurrentlyLocked;
    private boolean isNotifiedToCloseByBTDialer = false;

    private boolean isFinishing = false;
    private MainService service = null;
    public static Context sContext;

    public static boolean isSendExitTakephoto = false;
    // The first rear facing camera
    // private int defaultCameraId;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 123) {

                int orientation = msg.arg1;
                Log.i(TAG, "Sensor Change orientation:" + String.valueOf(orientation));
                if (orientation > 45 && orientation < 135) {
                    // SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                    ratation = 270;
                } else if (orientation > 135 && orientation < 225) {
                    // SCREEN_ORIENTATION_REVERSE_PORTRAIT
                    ratation = 180;

                } else if (orientation > 225 && orientation < 315) {
                    // SCREEN_ORIENTATION_LANDSCAPE
                    ratation = 90;

                } else if ((orientation > 315 && orientation < 360)
                        || (orientation > 0 && orientation < 45)) {
                    // SCREEN_ORIENTATION_PORTRAIT
                    ratation = 0;
                }
            }
        }
    };

    private final SubSensorListener mSubSensorListener = new SubSensorListener(handler);

    class SubSensorListener implements SensorEventListener {
        private static final int _DATA_X = 0;

        private static final int _DATA_Y = 1;

        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        private final Handler handler;

        public SubSensorListener(Handler handler) {
            this.handler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            Log.i(TAG, "Sensor Change event:");
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - (int) Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }

            if (handler != null) {
                handler.obtainMessage(123, orientation, 0).sendToTarget();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if (!RemoteCameraService.inLaunchProgress && !isFinishing) {
            finish();
        }
        isFinishing = false;
        
        sContext = this;
        RemoteCameraService.setListener(this);
        // regist the broadcast Receiver
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SensorManager sm = (SensorManager) BTNotificationApplication.getInstance()
                .getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(mSubSensorListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI, handler);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);
        service = MainService.getInstance();
        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                // defaultCameraId = i;
            }
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
      
        // open twice, if MTK camera didn't release before.
        for (int i = 0; i < 2; i++) {
            // Open the default i.e. the first rear facing camera.
            try {
                mCamera = Camera.open();
                if (mCamera == null) {
                    Log.i(TAG, "Can't open the camera");
                    isNotifiedToCloseByBTDialer = false;
                    mController.sendOnStart(false);
                    RemoteCameraService.inLaunchProgress = false;
                    finish();
                    return;
                }
                break;
            } catch (Exception e) {
                Log.i(TAG, "onResume, i = " + i + ", e = " + e);
                if (i == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                } else {
                    isNotifiedToCloseByBTDialer = false;
                    mController.sendOnStart(false);
                    RemoteCameraService.inLaunchProgress = false;
                    finish();
                    return;
                }
            }
        }

        if (mPreview != null) {
            mPreview.setCamera(mCamera);
            mController.sendOnStart(true);
            RemoteCameraService.isLaunched = true;
            RemoteCameraService.isIntheProgressOfExit = false;
            RemoteCameraService.inLaunchProgress = false;
        }
    }

    @Override
    public void finish() {
        Log.i(TAG, "finish");
        isFinishing = true;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreview.setCamera(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        SensorManager sm = (SensorManager) BTNotificationApplication.getInstance()
                .getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        sm.unregisterListener(mSubSensorListener);
        RemoteCameraService.isIntheProgressOfExit = false;

        super.finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        
        Log.i(TAG, "onPause: isNotifiedToCloseByBTDialer is:" + isNotifiedToCloseByBTDialer);
        if (!isFinishing) {
            finish();
        }
//        service.sendMessage("0020" + "remote_exit_activity");
        if (isNotifiedToCloseByBTDialer) {
            isNotifiedToCloseByBTDialer = false;
        } else {
            mController.sendOnDestory();

        }
        RemoteCameraService.setListener(null);

        RemoteCameraService.isLaunched = false;
        RemoteCameraService.isIntheProgressOfExit = false;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitRemote();
            finish();
            return true;
        }
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        exitRemote();

        Log.i(TAG, "onDestroy: isNotifiedToCloseByBTDialer is:" + isNotifiedToCloseByBTDialer);
    }

    /**
     * 退出当前activity 做的操作(退出拍照)
     */
    private void exitRemote(){
    if (SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("2")      //X2手环
            || SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")) {   //智能表
        if(MainService.isDeviceSendExitCommand){
            MainService.isDeviceSendExitCommand = false;
        }else {
            String protocolCode =  SharedPreUtil.readPre(sContext, SharedPreUtil.FIRMEWAREINFO, SharedPreUtil.PROTOCOLCODE);
            if(DateUtil.versionCompare("V1.1.36",protocolCode)
                    && SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("1")){   //现根据协议版本号，大于1.1.37
                L2Send.sendNewExitTakephoto();
                isSendExitTakephoto = true;
            }else {
                // 退出拍照时，发送退出拍照的命令
                L2Send.sendExitTakephoto();
                isSendExitTakephoto = true;
            }
        }
    }else if(SharedPreUtil.readPre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WATCH).equals("3")){  //MTk
        if(MainService.isDeviceSendExitCommand){
            MainService.isDeviceSendExitCommand = false;
        }else {
            // 退出拍照时，发送退出拍照的命令
            BluetoothMtkChat.getInstance().sendCloseCramer();
            isSendExitTakephoto = true;
        }
    }
}
    @Override
    public void onExitCamera() {
        isNotifiedToCloseByBTDialer = true;
        RemoteCameraService.isIntheProgressOfExit = true;
        finish();
    }

    @Override
    public void onTakePicture() {
        if (mPreview != null) {
            mPreview.takePicture(ratation);
        }
    }

}

// ----------------------------------------------------------------------

/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered
 * preview of the Camera to the surface. We need to center the SurfaceView
 * because not all devices have cameras that support preview sizes at the same
 * aspect ratio as the device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {

    public static final File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    private final String TAG = "AppManager/Camera/Preivew";

    private final SurfaceView mSurfaceView;

    private final SurfaceHolder mHolder;

    private Size mPreviewSize;

    private Size mPictureSize;

    private List<Size> mSupportedPreviewSizes;

    private List<Size> mSupportedPictureSizes;

    private Camera mCamera;

    private int ratation;

    private final Context mContext;

    private long mCurrentTime;

    Activity mAcitivity;

    private final int REMOTE_WIDTH = 240;

    private final int REMOTE_LENGTH = 240;

    private RemoteCameraController mController = RemoteCameraController.getInstance();

    private final TakePictureCallback mTakePictureCallback = new TakePictureCallback();

    private final AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();

    Preview(Context context) {
        super(context);
        mContext = context;
        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);
        mCurrentTime = 0;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        // mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void sendCaptureFail() {
        mController.sendOnStart(false);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;

        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();

            requestLayout();
        }
    }

    public void takePicture(int rote) {
        if (mCamera != null) {
            ratation = rote;
            // mCamera.enableShutterSound(true);
            try {
                mCamera.autoFocus(mAutoFocusCallback);
            } catch (Exception e) {
                Log.i(TAG, "Autofocus faill");
                sendCaptureFail();
            }

        }
    }

    private final class AutoFocusCallback implements Camera.AutoFocusCallback {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            Log.i(TAG, "onAutoFocus Callback");
            try {
                camera.cancelAutoFocus();
                camera.takePicture(null, null, mTakePictureCallback);
               // if(mContext != null){
               //     Toast.makeText(mContext,getResources().getString(R.string.take_photo_ok),Toast.LENGTH_SHORT).show();
               // }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    private final class TakePictureCallback implements PictureCallback {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            Log.i(TAG, "onPictureTaken");
            String state = Environment.getExternalStorageState();
            if ((!(Environment.MEDIA_MOUNTED.equals(state)))
                    || (Utils
                            .getAvailableStore(Environment.getExternalStorageDirectory().getPath()) < 2000)) {
                Log.i(TAG, "ExternalStorage Fail");
                sendCaptureFail();
            } else {

                Toast.makeText(getContext(), getResources().getString(R.string.photo_success), Toast.LENGTH_SHORT).show();
                sendCaptureData(data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (ratation == 0 || ratation == 180) {
                    matrix.postRotate(ratation + 90);
                }
                if (ratation == 90 || ratation == 270) {
                    matrix.postRotate(ratation - 90);
                }

               

                String pictureName = "IMG_fundo_" + System.currentTimeMillis() + ".jpg";
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                        matrix, true);

                // create parameters for Intent with filename

                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsolutePath() + "/Fundo");
                directory.mkdirs();

                File file = new File(directory, pictureName);

                // File file = new File(Environment.getExternalStorageState(),
                // System.currentTimeMillis() + ".jpg");
                try {
                    FileOutputStream fout = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fout);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    bos.flush();
                    bos.close();
                    MediaScannerConnection.scanFile(RemoteCamera.sContext, new String[]{file.getAbsolutePath()},null, null);
                    //mtk add 部分手机拍照找不到图片路径
                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                    Uri uri;
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                        uri = Uri.fromFile(file);
                    }else{
                        uri =  TUriParse.getUriForFile(BTNotificationApplication.getInstance(), file);
                    }
                    Log.e("uri:", "uri  :"+ uri);
                  //  Uri  uri = Uri.fromFile(file);
                    scanIntent.setData(uri);
                    mContext.sendBroadcast(scanIntent);
                    RemoteCameraService.intinotification(uri);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    String errorString = e.toString();
                    if (errorString == null) {
                        errorString = "capture error";
                    }
                    Log.w(TAG, errorString);
                }

            }

            Log.i("Remote Capture", "Capture success");

            try {
                camera.setDisplayOrientation(0);
                camera.startPreview();
            } catch (Exception e) {
                String errorString = e.toString();
                if (errorString == null) {
                    errorString = "capture error";
                }
                Log.w(TAG, errorString);
            }
        }

        private Bitmap sendCaptureData(byte[] data) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            double scaleRation;
            int thumbnailWidth = bitmap.getWidth();
            int thumbnailHeight = bitmap.getHeight();

            /*
             * if (thumbnailWidth > thumbnailHeight ) { scaleRation =
             * thumbnailWidth / 170; } else { scaleRation = thumbnailHeight /
             * 170; }
             */

            if (thumbnailWidth < thumbnailHeight) {
                scaleRation = thumbnailWidth / REMOTE_WIDTH;
            } else {
                scaleRation = thumbnailHeight / REMOTE_LENGTH;
            }

            Matrix matrix = new Matrix();
            if (ratation == 0 || ratation == 180) {
                matrix.postRotate(ratation + 90);
            }
            if (ratation == 90 || ratation == 270) {
                matrix.postRotate(ratation - 90);
            }

            // bitmap = Bitmap.createScaledBitmap(bitmap, 240, 240, false);

            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (thumbnailWidth / scaleRation),
                    (int) (thumbnailHeight / scaleRation), false);
            // bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int)(thumbnailWidth /
            // scaleRation), (int)(thumbnailHeight / scaleRation), matrix,
            // true);
            bitmap = Bitmap.createBitmap(bitmap,
                    (int) ((thumbnailWidth / scaleRation - REMOTE_WIDTH) / 2),
                    (int) ((thumbnailHeight / scaleRation - REMOTE_LENGTH) / 2), (int) 240,
                    (int) 240, matrix, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

            byte[] captureJpegData = baos.toByteArray();
            mController.sendPicture(captureJpegData);
            return bitmap;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return true;
    }

    public void switchCamera(Camera camera) {
        setCamera(camera);
        try {
            camera.setPreviewDisplay(mHolder);
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
        if (mSupportedPictureSizes != null) {
            mPictureSize = getOptimalPreviewSize(mSupportedPictureSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2,
                        height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2, width,
                        (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        Log.i(TAG, "surfaceCreated");
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;
        int length = sizes.size();
        // Try to find an size match aspect ratio and size
        for (int i = length - 1; i > 0; --i) {
            Size size = sizes.get(i);
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if (mCamera == null) {
            return;
        }

        Log.i(TAG, "surfaceChanged");
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        parameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        requestLayout();

        mCamera.setParameters(parameters);

        mCamera.setPreviewCallback(new PreviewCallback() {
            public synchronized void onPreviewFrame(byte[] data, Camera camera) {

                // RemoteCameraService.needPreview
                if (RemoteCameraService.needPreview) {
                    if (mCurrentTime == 0) {
                        mCurrentTime = System.currentTimeMillis();
                    }
                    long deltaTime = System.currentTimeMillis() - mCurrentTime;
                    if (deltaTime > 333) {
                        Log.i("CameraPreview", "video data come ...");
                        mCurrentTime = System.currentTimeMillis();
                        Camera.Parameters parameters = camera.getParameters();
                        int imageFormat = parameters.getPreviewFormat();
                        int previewWidth = parameters.getPreviewSize().width;
                        int previewHight = parameters.getPreviewSize().height;
                        Rect rect = new Rect(0, 0, previewWidth, previewHight);
                        YuvImage yuvImg = new YuvImage(data, imageFormat, previewWidth,
                                previewHight, null);
                        ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                        yuvImg.compressToJpeg(rect, 70, outputstream);

                        // int a = outputstream.toByteArray().length;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(outputstream.toByteArray(),
                                0, outputstream.size());

                        double scaleRation;
                        /*
                         * if (previewWidth > previewHight ) { scaleRation =
                         * previewWidth / 170 ; } else { scaleRation =
                         * previewHight / 170 ; }
                         */

                        if (previewWidth < previewHight) {
                            scaleRation = previewWidth / REMOTE_WIDTH;
                        } else {
                            scaleRation = previewHight / REMOTE_LENGTH;
                        }

                        Matrix matrix = new Matrix();
                        ratation = RemoteCamera.ratation;
                        if (ratation == 0 || ratation == 180) {
                            matrix.postRotate(ratation + 90);
                        }
                        if (ratation == 90 || ratation == 270) {
                            matrix.postRotate(ratation - 90);
                        }

                        bitmap = Bitmap.createScaledBitmap(bitmap,
                                (int) (previewWidth / scaleRation),
                                (int) (previewHight / scaleRation), false);

                        // bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        // bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                        bitmap = Bitmap.createBitmap(bitmap,
                                (int) ((previewWidth / scaleRation - REMOTE_WIDTH) / 2),
                                (int) ((previewHight / scaleRation - REMOTE_LENGTH) / 2),
                                (int) REMOTE_WIDTH, (int) REMOTE_LENGTH, matrix, true);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);

                        byte[] captureJpegData = baos.toByteArray();

                        DataSyncController.getInstance(mContext).sendPreviewData(captureJpegData);
                        Log.i("CameraPreview", "vedio data has sent ...");

                    } else {
                        Log.i("CameraPreview", "vedio data did not need to send ...");
                    }
                }

            }
        });

        mCamera.startPreview();

    }

}
