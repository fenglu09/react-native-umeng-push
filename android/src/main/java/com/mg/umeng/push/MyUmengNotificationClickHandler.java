package com.mg.umeng.push;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import java.util.List;
import java.util.Map;

/**
 * 通知点击处理
 */

public class MyUmengNotificationClickHandler extends UmengNotificationClickHandler {
    public static final String EVENT_RECEIVE_REMOTE_NOTIFICATION = "clickRemoteNotification";

    @Override
    public void launchApp(Context context, UMessage uMessage) {
        boolean isRun = NotificationUtil.isRunning(context, context.getPackageName());
        boolean isOnForeground = NotificationUtil.appOnForeground(context);
        if (isRun == false || isOnForeground == false) {
//            super.launchApp(context, uMessage);
            Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(LaunchIntent);
        }

        Map<String, String> map = uMessage.extra;
        WritableMap param = Arguments.createMap();

        for (String key : map.keySet()) {
            String value = map.get(key).toString();//
            param.putString(key, value);
        }

        RNUmengPushModule.sendEvent(EVENT_RECEIVE_REMOTE_NOTIFICATION, param);
    }


    //    private boolean isAppForeground(Context context, String packageName) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//        String currentPackageName = cn.getPackageName();
//        Log.d("isAppForeground", currentPackageName);
//        Log.d("isAppForeground", packageName);
//
//        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
//            return true;
//        }
//
//        return false;
//    }


}
