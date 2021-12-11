package com.app.playutils;

import android.accessibilityservice.GestureDescription;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @author:create by ys
 * 时间:2021/12/1 12
 * 邮箱 894417048@qq.com
 */
public class PlayAccessibility extends BaseAccessibilityService{

    private final String TAG = getClass().getName();

    public static PlayAccessibility mService;
    private LocalReceiver mLocalReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private DotPoint point;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.dotClike);

        mLocalReceiver = new LocalReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mLocalReceiver, intentFilter);

    }

    //初始化
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mService = this;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        super.onAccessibilityEvent(event);
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            String packageName = event.getPackageName().toString();
            String className = event.getClassName().toString();
            if (Constant.packageName.equals(packageName) && Constant.activitypName.equals(className)){
                Log.d(TAG, "当前活动：" + packageName + "  " + className);
                AntoApplication.getInstance().setOpenOKactivity(true);
            } else {
                AntoApplication.getInstance().setOpenOKactivity(false);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void clickMiddleInRect(DotPoint point) {
        if (point == null){
            return;
        }

        System.out.println("相识度：" + point.getMaxVal() + "第" + point.getIndex() + " 步点击坐标：x：" + (point.getMaxx() + point.getMinX()) / 2
                + " y：" + (point.getMaxy() + point.getMiny()) / 2 + "\n");
        Path path = new Path();
        path.moveTo(((point.getMaxx() + point.getMinX())/2), ((point.getMaxy() + point.getMiny())/2));
        GestureDescription.Builder builder = new GestureDescription.Builder();
        try {
            GestureDescription gestureDescription = builder.addStroke(new GestureDescription.StrokeDescription(path, 450, 50)).build();
            dispatchGesture(gestureDescription, new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                }
            }, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {
        mService = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mService = null;
    }


    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == Constant.dotClike) {
                point = (DotPoint) intent.getSerializableExtra("data");
                mHandler.sendEmptyMessage(0);
            }
        }
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean handleMessage(Message msg) {
            if (point != null){
                clickMiddleInRect(point);
            }
            return false;
        }
    });
}