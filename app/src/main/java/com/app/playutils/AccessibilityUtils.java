package com.app.playutils;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author:create by ys
 * 时间:2021/12/1 12
 * 邮箱 894417048@qq.com
 */
public class AccessibilityUtils {

    /**
     * 该辅助功能开关是否打开
     */
    public static boolean isAccessibilitySettingsOn(String serviceName, Context mContext) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if (accessibilityService.equalsIgnoreCase(serviceName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 跳转到系统设置页面开启辅助功能
     */
    public static boolean openAccessibility(Context context) {
        final String serviceName = context.getPackageName() + "/" + PlayAccessibility.class.getCanonicalName();
        if (!isAccessibilitySettingsOn(serviceName, context)) {
            Toast.makeText(context, "需要取得权限以使用辅助服务", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
            return false;
        }
        return true;
    }


} 