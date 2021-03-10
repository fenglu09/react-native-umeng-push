package com.mg.umeng.push;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.util.Map;

import androidx.core.app.NotificationCompat;


/**
 * 推送消息返回监听
 */

public class MessageHandler extends UmengMessageHandler {
    public String TAG = "MessageHandler";
    public static final String EVENT_RECEIVE_NOTIFICATION_ARRIVAL = "arrivalNotification";


    public void arrivalRemoteNotification(Map<String, String> map) {
        try {
            WritableMap param = Arguments.createMap();
            for (String key : map.keySet()) {
                String value = map.get(key).toString();
                param.putString(key, value);
            }

            RNUmengPushModule.sendEvent(EVENT_RECEIVE_NOTIFICATION_ARRIVAL, param);
            return;
        } catch (Exception e) {
        }
    }

    @Override
    public Notification getNotification(Context context, UMessage msg) {
        try {
            BadgenumberUtils.setBadgenumber(context);

            Map<String, String> map = msg.extra;
            if (map != null) {
                arrivalRemoteNotification(map);//udesk推送消息监听发送


                Resources res = context.getResources();
                String packageName = context.getPackageName();
                int resId = res.getIdentifier("icon", "mipmap", packageName);
                Bitmap largeIconBitmap = BitmapFactory.decodeResource(res, resId);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle(map.get("title"))
                        .setContentText(map.get("content"))
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(resId)
                        .setAutoCancel(true)
                        .setLargeIcon(largeIconBitmap);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(context.getPackageName());
                }
                return builder.build();
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return null;
    }
}
