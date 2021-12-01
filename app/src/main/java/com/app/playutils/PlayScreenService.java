package com.app.playutils;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

/**
 * @author:create by ys
 * 时间:2021/12/1 14
 * 邮箱 894417048@qq.com
 */
public class PlayScreenService extends IntentService {

    public PlayScreenService() {
        super("PlayScreenService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (AntoApplication.getInstance().isOpenOKactivity()){
                //发送截图广播
                sendBroadcast(new Intent(Constant.openScreen));
            }

            int anHour = 10 * 1000;
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent i = new Intent(this, OpenServiceReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
    }
}