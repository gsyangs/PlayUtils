package com.app.playutils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OpenServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //启动截图服务
        Intent itemIntent = new Intent(context, PlayScreenService.class);
        context.startService(itemIntent);
    }
}
