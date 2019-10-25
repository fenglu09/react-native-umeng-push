package com.mg.umeng.push;

import android.content.Context;
import android.content.Intent;

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
//        super.launchApp(context, uMessage);
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(LaunchIntent);

        Map<String, String> map = uMessage.extra;
        WritableMap param = Arguments.createMap();

        for (String key : map.keySet()) {
            String value = map.get(key).toString();
            param.putString(key, value);
        }

        RNUmengPushModule.sendEvent(EVENT_RECEIVE_REMOTE_NOTIFICATION, param);
    }
}
