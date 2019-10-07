package com.mg.umeng.push;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.util.Map;

/**
 * 通知点击处理
 */

public class MyUmengNotificationClickHandler extends UmengNotificationClickHandler {
    public static final String EVENT_RECEIVE_REMOTE_NOTIFICATION = "clickRemoteNotification";

    @Override
    public void launchApp(Context context, UMessage uMessage) {
        if (!isAppForeground(context, context.getPackageName())) {
            super.launchApp(context, uMessage);
        }

        Map<String, String> map = uMessage.extra;
        WritableMap param = Arguments.createMap();

        for (String key : map.keySet()) {
            String value = map.get(key).toString();//
            param.putString(key, value);
        }

        RNUmengPushModule.sendEvent(EVENT_RECEIVE_REMOTE_NOTIFICATION, param);
    }


    private boolean isAppForeground(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        Log.d("isAppForeground", currentPackageName);
        Log.d("isAppForeground", packageName);

        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
            return true;
        }

        return false;
    }
}
