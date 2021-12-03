package com.app.playutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

public class ScreenShotHelper {

    interface OnScreenShotListener {
        void onFinish(Bitmap bitmap);
        void stop();
    }

    
    private OnScreenShotListener mOnScreenShotListener;

    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private final SoftReference<Context> mRefContext;
    private Display mDisplay;
    private DisplayMetrics metric;

    @SuppressLint("WrongConstant")
    public ScreenShotHelper(Context context, int resultCode, Intent data, OnScreenShotListener onScreenShotListener) {
        this.mOnScreenShotListener = onScreenShotListener;
        this.mRefContext = new SoftReference<Context>(context);

        metric = Resources.getSystem().getDisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getRealMetrics(metric);

        mMediaProjection = getMediaProjectionManager().getMediaProjection(resultCode, data);
        mImageReader = ImageReader.newInstance(metric.widthPixels, metric.heightPixels, PixelFormat.RGBA_8888, 1);

    }

    public void startScreenShot() {
        createVirtualDisplay();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new CreateBitmapTask().execute();
            }
        }, 500);
    }

    public void stopScreenShot() {
        createVirtualDisplay();
        if (mVirtualDisplay != null){
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaProjection != null){
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (mOnScreenShotListener != null) {
            mOnScreenShotListener.stop();
        }

    }

    public class CreateBitmapTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... params) {
            Image image = mImageReader.acquireLatestImage();
            if (image != null){
                int width = image.getWidth();
                int height = image.getHeight();
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();

                int pixelStride = planes[0].getPixelStride();

                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                image.close();
                return bitmap;
            } else {
                return null;
            }
        }
        public void saveBitmap(String name, Bitmap bm) {
            Log.d("Save Bitmap", "Ready to save picture");
            //指定我们想要存储文件的地址
            String TargetPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + "手机录屏助手/" + "/";

            Log.d("Save Bitmap", "Save Path=" + TargetPath);
            //判断指定文件夹的路径是否存在
            if (!fileIsExist(TargetPath)) {
                Log.d("Save Bitmap", "TargetPath isn't exist");
            } else {
                //如果指定文件夹创建成功，那么我们则需要进行图片存储操作
                File saveFile = new File(TargetPath, name);

                try {
                    FileOutputStream saveImgOut = new FileOutputStream(saveFile);
                    // compress - 压缩的意思
                    bm.compress(Bitmap.CompressFormat.PNG, 100, saveImgOut);
                    //存储完成后需要清除相关的进程
                    saveImgOut.flush();
                    saveImgOut.close();
                    Log.d("Save Bitmap", "The picture is save to your phone!");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public boolean fileIsExist(String fileName)
        {
            //传入指定的路径，然后判断路径是否存在
            File file=new File(fileName);
            if (file.exists())
                return true;
            else{
                //file.mkdirs() 创建文件夹的意思
                return file.mkdirs();
            }
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (mOnScreenShotListener != null) {
                mOnScreenShotListener.onFinish(bitmap);
                //saveBitmap((System.currentTimeMillis() + ".png"),bitmap);
            }
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {
        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private void createVirtualDisplay() {
        if (mVirtualDisplay == null){
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                    "screen-mirror",
                    metric.widthPixels,
                    metric.heightPixels,
                    metric.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(),
                    null,
                    null
            );
        }

    }

    private Context getContext() {
        return mRefContext.get();
    }

}
