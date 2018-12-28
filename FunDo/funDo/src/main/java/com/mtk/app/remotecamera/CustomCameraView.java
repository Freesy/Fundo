package com.mtk.app.remotecamera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.kct.fundo.btnotification.R;
import com.szkct.takephoto.uitl.TUriParse;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.util.ToastManage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
@SuppressLint({"ShowToast", "ClickableViewAccessibility"})
public class CustomCameraView extends FrameLayout implements SurfaceHolder.Callback, AutoFocusCallback {

    public static final File PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

    public static final String TAG = CustomCameraView.class.getName();

    public static final String CUSTOMECAMERA_PHOTO_PATH = "/cameraviewpicture/";
    private static Context context = null;
    private static Camera camera = null;
    private SurfaceHolder surface_holder = null;
    private SurfaceView surface_camera = null;
    private int viewWidth = 0;
    private int viewHeight = 0;
    private OnTakePictureInfo onTakePictureInfo = null;
    private View view_focus = null;
    private PreviewFrameLayout frameLayout = null;

    public boolean isBackground() {
        return isBackground;
    }

    public void setBackground(boolean background) {
        isBackground = background;
        surfaceCreated(surface_holder);
    }

    private boolean isBackground = true;//是否为后置摄像头

    /**
     * 模式 NONE：无 FOCUSING：正在聚焦. FOCUSED:聚焦成功 FOCUSFAIL：聚焦失败
     */
    private enum MODE {
        NONE, FOCUSING, FOCUSED, FOCUSFAIL

    }

    public static boolean changeCamere = false ;

    private MODE mode = MODE.NONE;//  默认模式

    public CustomCameraView(Context context) {
        super(context);
    }

    private String picpath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/fundopic/";

    public static String shareFileDirectory = "fundopic";       // FunDoAppManager/FunDoCache

    @SuppressLint("ClickableViewAccessibility")
    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.preview_frame, this);
        frameLayout = (PreviewFrameLayout) findViewById(R.id.frame_layout);
        surface_camera = (SurfaceView) findViewById(R.id.camera_preview);
        view_focus = findViewById(R.id.view_focus);
        surface_holder = surface_camera.getHolder();
        surface_holder.addCallback(this);
        frameLayout.setOnTouchListener(onTouchListener);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (checkCameraHardware()) {
            camera = getCameraInstance(changeCamere);
        }
        try {
            camera.setPreviewDisplay(surface_holder);
            updateCameraParameters();
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "________________surfaceCreated_______ catch (IOException e)");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "________________surfaceCreated_______ catch (Exception e)");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // try {
        // camera.setPreviewDisplay(surface_holder);
        // updateCameraParameters();
        // camera.startPreview();
        // } catch (IOException e) {
        // // TODO 自动生成的 catch 块
        // e.printStackTrace();
        // }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null && holder != null) {
            camera.stopPreview();

            changeCamere = false;

            try {//友盟很奇怪的错误异常  java.lang.RuntimeException: Camera is being used after Camera.release() was called
                camera.release();
            }catch (Exception e){}
        }

    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        viewWidth = MeasureSpec.getSize(widthSpec);
        viewHeight = MeasureSpec.getSize(heightSpec);
        Log.e(TAG, "屏幕大小" + viewWidth + "-------------" + viewHeight);

        super.onMeasure(MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY));
    }

    private boolean checkCameraHardware() {
        if (context != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private Camera getCameraInstance(boolean isChangeCamera) {
        Camera c = null;
        try {
            int cameraCount = 0;

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras(); // get cameras number

            for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
                Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
                if (isBackground && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) { // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置   TODO ---- 后置摄像头
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(null != camera && isChangeCamera){
                        camera.stopPreview();//停掉原来摄像头的预览
                        camera.release();//释放资源
                        camera = null;//取消原来摄像头
                    }
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    Log.e(TAG, "后置摄像头" + camIdx);
                    try {
                        c = Camera.open(camIdx);
                    } catch (RuntimeException e) {
                    }
                } else if (!isBackground && cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {    // TODO ---- 前置摄像头
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if(null != camera && isChangeCamera){
                        camera.stopPreview();//停掉原来摄像头的预览
                        camera.release();//释放资源
                        camera = null;//取消原来摄像头
                    }
                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    Log.e(TAG, "前置摄像头" + camIdx);
                    try {
                        c = Camera.open(camIdx);
                    } catch (RuntimeException e) {   //todo --- 切换为前置 是 异常了
                        e.printStackTrace();
                    }
                }
            }
            if (c == null) {
                Log.e(TAG,"open 为null ");
                c = Camera.open(0); // attempt to get a Camera instance
            }
        } catch (Exception e) {
            // Toast.makeText(context, "摄像头打开失败！", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }
        return c;
    }

    private void updateCameraParameters() {
        if (camera != null) {
            Camera.Parameters p = camera.getParameters();

            long time = new Date().getTime();
            p.setGpsTimestamp(time);

            //Size previewSize = findBestPreviewSize(p);
            Size previewSize = findBestPreviewSizeNew(p);
            Size pictureSize = findBestPictureSizeNew(p);
//			p.setPreviewSize(previewSize.width, previewSize.height);
//			p.setPictureSize(previewSize.width, previewSize.height);
//            for (Camera.Size size : p.getSupportedPictureSizes()) {
//                // 640 480
//                // 960 720
//                // 1024 768
//                // 1280 720
//                // 1600 1200
//                // 2560 1920
//                // 3264 2448
//                // 2048 1536
//                // 3264 1836
//                // 2048 1152
//                // 3264 2176
//                if (1600 <= size.width & size.width <= 1920) {
//                    p.setPreviewSize(size.width, size.height);
//                    p.setPictureSize(size.width, size.height);
//                    break;
//                }
//            }

            // Set the preview frame aspect ratio according to the picture size.
            if(previewSize!=null) {
                frameLayout.setAspectRatio((double) previewSize.width / previewSize.height);
                p.setPreviewSize(previewSize.width, previewSize.height);// 设置预浏尺寸，注意要在摄像头支持的范围内选择
                p.setPictureSize(pictureSize.width, pictureSize.height);// 设置照片分辨率，注意要在摄像头支持的范围内选择
                Log.e(TAG, "updateCameraParameters: 设置的分辨率"+previewSize.width+" "+previewSize.height);
            }

            if (context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);
                p.setRotation(90);
            }
            camera.setParameters(p);


        }
    }

    /**
     * 找到最合适的显示分辨率 （防止预览图像变形）
     *
     * @param parameters
     * @return
     */
    private Size findBestPreviewSize(Camera.Parameters parameters) {
        //系统支持的所有预览分辨率
        String previewSizeValueString = null;
        previewSizeValueString = parameters.get("preview-size-values");

        if (previewSizeValueString == null) {
            previewSizeValueString = parameters.get("preview-size-value");
        }

        if (previewSizeValueString == null) { // 有些手机例如m9获取不到支持的预览大小 就直接返回屏幕大小
            return camera.new Size(getScreenWH().widthPixels, getScreenWH().heightPixels);
        }
        float bestX = 0;
        float bestY = 0;

        float tmpRadio = 0;
        float viewRadio = 0;

        if (viewWidth != 0 && viewHeight != 0) {
            viewRadio = Math.min((float) viewWidth, (float) viewHeight) / Math.max((float) viewWidth, (float) viewHeight);
        }
        // System.out.println("CustomCameraView previewSizeValueString COMMA_PATTERN = "
        // + previewSizeValueString);

        String[] COMMA_PATTERN = previewSizeValueString.split(",");
        for (String prewsizeString : COMMA_PATTERN) {
            prewsizeString = prewsizeString.trim();

            int dimPosition = prewsizeString.indexOf('x');
            if (dimPosition == -1) {
                continue;
            }

            float newX = 0;
            float newY = 0;

            try {
                newX = Float.parseFloat(prewsizeString.substring(0, dimPosition));
                newY = Float.parseFloat(prewsizeString.substring(dimPosition + 1));
            } catch (NumberFormatException e) {
                continue;
            }

            float radio = Math.min(newX, newY) / Math.max(newX, newY);
            if (tmpRadio == 0) {
                tmpRadio = radio;
                bestX = newX;
                bestY = newY;
            } else if (tmpRadio != 0 && (Math.abs(radio - viewRadio)) < (Math.abs(tmpRadio - viewRadio))) {
                tmpRadio = radio;
                bestX = newX;
                bestY = newY;
            }
        }

        if (bestX > 0 && bestY > 0) {
            // System.out.println("CustomCameraView previewSizeValueString bestX = " +
            // bestX + ", bestY = " + bestY);
            return camera.new Size((int) bestX, (int) bestY);
        }
        return null;
    }

    //找到最合适的分辨率
    private Size findBestPreviewSizeNew(Camera.Parameters parameters){
        List<Size> previewSizes = camera.getParameters().getSupportedPreviewSizes();
        Size psize = null;
        int max=0,index=0;
        for (int i = 0; i < previewSizes.size(); i++) {
            psize = previewSizes.get(i);
            if(psize.width>max) {
                max = psize.width;
                index=i;
            }
            Log.e("pictureSize",psize.width+" x "+psize.height);
        }
        return previewSizes.get(index);
    }

    //找到最合适的图片分辨率
    private Size findBestPictureSizeNew(Camera.Parameters parameters){
        List<Size> previewSizes = camera.getParameters().getSupportedPictureSizes();
        Size psize = null;
        int max=0,index=0;
        for (int i = 0; i < previewSizes.size(); i++) {
            psize = previewSizes.get(i);
            if(psize.width>max) {
                max = psize.width;
                index=i;
            }
            Log.e("findBestPictureSizeNew",psize.width+" x "+psize.height);
        }
        return previewSizes.get(index);
    }

    /**
     * 点击显示焦点区域
     */
    OnTouchListener onTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // System.out.println("CustomCameraView view_focus.getWidth() " +
                // view_focus.getWidth() + ",  view_focus.getHeight() = " +
                // view_focus.getHeight());
                // System.out.println("CustomCameraView event.getX() " + event.getX() +
                // ",  event.getY() = " + event.getY());

                int width = view_focus.getWidth();
                int height = view_focus.getHeight();
                view_focus.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_focus_focusing));
                view_focus.setX(event.getX() - (width / 2));
                view_focus.setY(event.getY() - (height / 2));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                mode = MODE.FOCUSING;
                try {//在更多界面中，按住任意功能后（如查找手环），多次点击远程拍照，APK出现闪退   ...测试还真无聊
                    focusOnTouch(event);
                } catch (Exception e) {
                }
            }

            return true;
        }
    };

    /**
     * 设置焦点和测光区域
     *
     * @param event
     */
    public void focusOnTouch(MotionEvent event) {

        int[] location = new int[2];
        frameLayout.getLocationOnScreen(location);

        Rect focusRect = calculateTapArea(view_focus.getWidth(), view_focus.getHeight(), 1f, event.getRawX(), event.getRawY(),
                location[0], location[0] + frameLayout.getWidth(), location[1], location[1] + frameLayout.getHeight());
        Rect meteringRect = calculateTapArea(view_focus.getWidth(), view_focus.getHeight(), 1.5f, event.getRawX(), event.getRawY(),
                location[0], location[0] + frameLayout.getWidth(), location[1], location[1] + frameLayout.getHeight());

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        // System.out.println("CustomCameraView getMaxNumFocusAreas = " +
        // parameters.getMaxNumFocusAreas());
        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusRect, 1000));

            parameters.setFocusAreas(focusAreas);
        }

        // System.out.println("CustomCameraView getMaxNumMeteringAreas = " +
        // parameters.getMaxNumMeteringAreas());
        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
            meteringAreas.add(new Camera.Area(meteringRect, 1000));

            parameters.setMeteringAreas(meteringAreas);
        }

        try {
            camera.setParameters(parameters);
            camera.autoFocus(this);
        } catch (Exception e) {
        }
    }

    /**
     * 计算焦点及测光区域
     *
     * @param focusWidth
     * @param focusHeight
     * @param areaMultiple
     * @param x
     * @param y
     * @param previewleft
     * @param previewRight
     * @param previewTop
     * @param previewBottom
     * @return Rect(left, top, right, bottom) :  left、top、right、bottom是以显示区域中心为原点的坐标
     */
    public Rect calculateTapArea(int focusWidth, int focusHeight, float areaMultiple,
                                 float x, float y, int previewleft, int previewRight, int previewTop, int previewBottom) {
        int areaWidth = (int) (focusWidth * areaMultiple);
        int areaHeight = (int) (focusHeight * areaMultiple);
        int centerX = (previewleft + previewRight) / 2;
        int centerY = (previewTop + previewBottom) / 2;
        double unitx = ((double) previewRight - (double) previewleft) / 2000;
        double unity = ((double) previewBottom - (double) previewTop) / 2000;
        int left = clamp((int) (((x - areaWidth / 2) - centerX) / unitx), -1000, 1000);
        int top = clamp((int) (((y - areaHeight / 2) - centerY) / unity), -1000, 1000);
        int right = clamp((int) (left + areaWidth / unitx), -1000, 1000);
        int bottom = clamp((int) (top + areaHeight / unity), -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    public int clamp(int x, int min, int max) {
        if (x > max)
            return max;
        if (x < min)
            return min;
        return x;
    }

    protected DisplayMetrics getScreenWH() {
        DisplayMetrics dMetrics = new DisplayMetrics();
        dMetrics = this.getResources().getDisplayMetrics();
        return dMetrics;
    }

    boolean istakePicture=true;//防止连拍导致崩溃

    /**
     * 拍照
     */
    public void takePicture() {
        if(!istakePicture)
            return;
        if (mode == MODE.FOCUSFAIL || mode == MODE.FOCUSING) {
            if (onTakePictureInfo != null) {
                onTakePictureInfo.onTakePictureInofo(false, null);
            }
            return;
        }
        istakePicture=false;//枷锁
        if (camera != null) {
            camera.takePicture(null, null, new PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    // System.out.println("CustomCameraView onPictureTaken " );

                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);  // final Bitmap bm
                    try {
                        if (bm != null)
                                if(!isBackground) {
                                    //前置摄像头 将图片旋转180°保存
                                    bm = rotateBitmapByDegree(bm, 180);
                                }
                        saveImageToGallery(bm);


                    /*Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
                    String photoNmae = sdf.format(new Date());
                    // 创建一个位于SD卡上的文件

                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    File appDir = new File(Environment.getExternalStorageDirectory(), shareFileDirectory);
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    File file = new File(appDir, photoNmae + ".jpg");  //    public static String sharefileName = "share_icon.png";
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    try {
                        FileOutputStream fileOutStream = new FileOutputStream(file);

                        if(!isBackground) {
                            //前置摄像头 将图片旋转180°保存
                            bm = rotateBitmapByDegree(bm, 180);
                        }

                        //将图片旋转90°
//					Bitmap bm1 = rotateBitmapByDegree(bm, 90);
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, fileOutStream);
                        fileOutStream.close();
                        ToastManage.showToast(context.getApplicationContext(), context.getString(R.string.photo_success), 0);  // todo  --- 去掉提示语
//                MediaStore.Images.Media.insertImage(getContentResolver(), bm1, "", "");//把图片插入到系统图库
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

//                        Uri uri = Uri.fromFile(file);

                        Uri uri;
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
                            uri = Uri.fromFile(file);
                        }else{
                            uri =  TUriParse.getUriForFile(BTNotificationApplication.getInstance(), file);
                        }


                        intent.setData(uri);
                        context.sendBroadcast(intent);*/

                        camera.startPreview();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    istakePicture=true;//解锁
                }
            });

            mode = MODE.NONE;
        }
    }

    public static Bitmap rotateBitmapByDegree(Bitmap origin, int degree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public void setOnTakePictureInfo(OnTakePictureInfo _onTakePictureInfo) {
        this.onTakePictureInfo = _onTakePictureInfo;
    }

    public interface OnTakePictureInfo {

        /**
         * 拍照后返回拍照信息
         *
         * @param _success 拍照成功
         * @param _file    照片文件
         */
        public void onTakePictureInofo(boolean _success, File _file);
    }

    @Override
    public void onAutoFocus(boolean success, Camera _camera) {
        // System.out.println("CustomCameraView onAutoFocus success = " + success);
        if (success) {
            mode = MODE.FOCUSED;
            // Camera.Parameters parameters = _camera.getParameters();
            // float[] output = new float[3];
            // parameters.getFocusDistances(output);
            // List<Camera.Area> area = parameters.getFocusAreas();
            // Rect rect = area.get(0).rect;
            //
            // System.out.println("CustomCameraView onAutoFocus output1 = " +
            // output[1] + " ,output[2] = " + output[2] + " ,output[3]" + output[2]);
            // System.out.println("CustomCameraView onAutoFocus rect.top = " +
            // rect.top + " , rect.left = " + rect.left + " ,rect.right" + rect.right
            // +
            // " ,rect.bottom" + rect.bottom);

            view_focus.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_focus_focused));
        } else {
            mode = MODE.FOCUSFAIL;
            view_focus.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_focus_failed));
        }

        setFocusView();
    }

    private void setFocusView() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                view_focus.setBackgroundDrawable(null);

            }
        }, 1 * 1000);
    }

  /*  public void turnCamera(SurfaceHolder holder, Camera.AutoFocusCallback autoFocusCallback, int degree, int screenWidth, int screenHeight) {
        //切换前后摄像头
        //现在是后置，变更为前置
        if (camera != null && cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK) {
            camera.stopPreview();//停掉原来摄像头的预览
            camera.release();//释放资源
            camera = null;//取消原来摄像头
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//打开当前选中的摄像头
            try {
                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                camera.setDisplayOrientation(degree);
                camera.autoFocus(autoFocusCallback);
                setCameraParameters(screenWidth, screenHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();//开始预览
            cameraPosition = Camera.CameraInfo.CAMERA_FACING_FRONT;
            DataUtils.isBackCamera = false;
        } else if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //代表摄像头的方位，CAMERA_FACING_FRONT前置
            // CAMERA_FACING_BACK后置
            //现在是前置， 变更为后置
            camera.stopPreview();//停掉原来摄像头的预览
            camera.release();//释放资源
            camera = null;//取消原来摄像头
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开当前选中的摄像头
            try {
                camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                camera.setDisplayOrientation(degree);
                camera.autoFocus(autoFocusCallback);
                setCameraParameters(screenWidth, screenHeight);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();//开始预览
            cameraPosition = Camera.CameraInfo.CAMERA_FACING_BACK;
            DataUtils.isBackCamera = true;
        }
    }*/
    public void saveImageToGallery(Bitmap bmp) {
        if (bmp == null) {
            Toast.makeText(context, "保存失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(context,  getResources().getString(R.string.photo_success), Toast.LENGTH_SHORT).show();
        String pictureName = "IMG_fundo_" + System.currentTimeMillis() + ".jpg";
        File sdCard = Environment.getExternalStorageDirectory();
        File appDir = new File(sdCard.getAbsolutePath() + "/Fundo");
        appDir.mkdirs();
        File file = new File(appDir, pictureName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            fos.flush();
            fos.close();
       //     MediaScannerConnection.scanFile(RemoteCamera.sContext, new String[]{file.getAbsolutePath()},null, null);
        } catch (FileNotFoundException e) {
         //   Toast.makeText(context, "图片保存出错！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
         //   Toast.makeText(context, "图片保存出错！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
         //   Toast.makeText(context, "图片保存出错！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            uri = Uri.fromFile(file);
        }else{
            uri =  TUriParse.getUriForFile(BTNotificationApplication.getInstance(), file);
        }
        Log.e("shouhuan:uri:", "uri  :"+ uri);
        // 其次把文件插入到系统图库
      /*  try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), pictureName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));

    }
}

