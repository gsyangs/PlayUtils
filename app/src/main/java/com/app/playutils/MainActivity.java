package com.app.playutils;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity implements ScreenShotHelper.OnScreenShotListener {

    private Button openServer;
    private Button opencvServer;
    private Button closeServer;
    private TextView info;
    private ImageView image;
    private static int REQUEST_MEDIA_PROJECTION = 0;
    private static int REQUEST_OTHER_PERMISSION = 1;
    private ScreenShotHelper screenShotHelper;
    private LocalReceiver mLocalReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);

        boolean openCvSuccess = OpenCVLoader.initDebug();
        openServer = findViewById(R.id.open_server);
        opencvServer = findViewById(R.id.opencv_server);
        closeServer = findViewById(R.id.close_server);
        image = findViewById(R.id.image);

        info = findViewById(R.id.info);

        if (openCvSuccess) {
            System.out.println("初始化Opencv成功！");
            Intent itemIntent = new Intent(this, PlayScreenService.class);
            startService(itemIntent);

            opencvServer.setOnClickListener(v -> {
                try {
                    Mat srcmat1 = Utils.loadResource(MainActivity.this, R.drawable.screenshot_1);
                    Mat dstmat1 = Utils.loadResource(MainActivity.this, R.drawable.mr_1);

                    Mat srcmat2 = Utils.loadResource(MainActivity.this, R.drawable.screenshot_2);
                    Mat dstmat2 = Utils.loadResource(MainActivity.this, R.drawable.mr_2);

                    DotPoint point1 = OpencvOCR.ocr(1, srcmat1, dstmat1);
                    DotPoint point2 = OpencvOCR.ocr(2, srcmat2, dstmat2);

                    Mat srcmat3 = Utils.loadResource(MainActivity.this, R.drawable.screenshot_3);
                    Bitmap bitmap = OpencvOCR.findColorPoint(srcmat3);
                    image.setImageBitmap(bitmap);

                    if (point1 != null && point2 != null) {
                        AntoApplication.getInstance().setPoint(point1);
                        AntoApplication.getInstance().setPoint(point2);
                        info.setText("第" + point1.getIndex() + " 步点击坐标：x：" + (point1.getMaxx() + point1.getMinX()) / 2
                                + " y：" + (point1.getMaxy() + point1.getMiny()) / 2 + "\n"
                                + "第" + point2.getIndex() + " 步点击坐标：x：" + (point2.getMaxx() + point2.getMinX()) / 2
                                + " y：" + (point2.getMaxy() + point2.getMiny()) / 2 + " \n ");
                    } else {
                        info.setText("opencv 初始化失败！ 无法运行脚本，请结束进程重新打开！");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            });


            openServer.setOnClickListener(v -> {
                if (AccessibilityUtils.openAccessibility(MainActivity.this) && startOverLay() && checkReadWritePermission(MainActivity.this)) {
                    Intent intent1 = getPackageManager().getLaunchIntentForPackage(Constant.packageName);
                    startActivity(intent1);
                }
            });

            closeServer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (screenShotHelper != null){
                        screenShotHelper.stopScreenShot();
                    }
                }
            });
        } else {
            info.setText("opencv 初始化失败！ 无法运行脚本，请结束进程重新打开！");
        }

        initBroadcast();

    }

    private void initBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.openScreen);

        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);

    }

    private boolean startOverLay() {
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(intent);
            return false;
        }
        return true;
    }

    public static boolean checkReadWritePermission(AppCompatActivity activity) {
        boolean isGranted = true;
        if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            isGranted = false;
        }
        if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            isGranted = false;
        }
        if (!isGranted) {
            activity.requestPermissions(
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    },
                    REQUEST_OTHER_PERMISSION);
        }
        return isGranted;
    }

    @Override
    public void onFinish(Bitmap bitmap) {
        //截图完成
        if (bitmap != null){
            //System.out.println("图片宽高：" + bitmap.getWidth() +  "   " + bitmap.getHeight());
            try {
                Mat dstmat1 = Utils.loadResource(MainActivity.this, R.drawable.mr_1);
                Mat dstmat2 = Utils.loadResource(MainActivity.this, R.drawable.mr_2);
                List<Mat> mats = new ArrayList<>();
                mats.add(dstmat1);
                mats.add(dstmat2);
                Mat srcmat = new Mat();
                Utils.bitmapToMat(bitmap,srcmat);
                DotPoint point = null;
                for (int i = 0 ; i < mats.size(); i++){
                    DotPoint p = OpencvOCR.ocr(i,srcmat,mats.get(i));
                    if (p.getMaxVal() > 0.55){
                        point = p;
                        break;
                    }
                }
                if (point != null){
                    AntoApplication.getInstance().setPoint(point);
                    System.out.println("相识度：" + point.getMaxVal() + "第" + point.getIndex() + " 步点击坐标：x：" + (point.getMaxx() + point.getMinX())/2
                            +" y："+ (point.getMaxy() + point.getMiny())/2 + "\n");

                    Intent intent = new Intent(Constant.dotClike);
                    intent.putExtra("data", point);
                    mLocalBroadcastManager.sendBroadcast(intent);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this,"识图失败,请稍等！",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void stop() {
        Toast.makeText(this,"服务关闭！",Toast.LENGTH_LONG).show();
        Intent itemIntent = new Intent(this, PlayScreenService.class);
        stopService(itemIntent);
    }


    //拿到广播去截图
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constant.openScreen) {
                try2StartScreenShot();
            }
        }
    }

    //截图
    private void try2StartScreenShot() {
        if (screenShotHelper != null){
            screenShotHelper.startScreenShot();
        } else {
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK && data != null) {
                screenShotHelper = new ScreenShotHelper(MainActivity.this, resultCode, data, this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_OTHER_PERMISSION) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    Toast.makeText(this, "需要取得权限以读写文件", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    startActivity(intent);
                    break;
                }
            }
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocalReceiver != null){
            unregisterReceiver(mLocalReceiver);
        }
    }
}