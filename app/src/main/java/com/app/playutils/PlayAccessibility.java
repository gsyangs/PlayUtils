package com.app.playutils;

import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.RequiresApi;

/**
 * @author:create by ys
 * 时间:2021/12/1 12
 * 邮箱 894417048@qq.com
 */
public class PlayAccessibility extends BaseAccessibilityService{


    private final String TAG = getClass().getName();

    public static PlayAccessibility mService;

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
//                try {
//                    Thread.sleep(20000);
//                    clickMiddleInRect(AntoApplication.getInstance().getPoints().get(0));
//                    Thread.sleep(20000);
//                    clickMiddleInRect(AntoApplication.getInstance().getPoints().get(1));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void clickMiddleInRect(Point point) {
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
}