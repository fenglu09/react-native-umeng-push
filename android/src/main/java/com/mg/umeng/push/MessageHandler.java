package com.mg.umeng.push;

import android.app.Notification;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;

import java.util.Map;


/**
 * 推送消息返回监听
 */

public class MessageHandler extends UmengMessageHandler {
    public String TAG = "MessageHandler";

    @Override
    public Notification getNotification(Context context, UMessage msg) {
        try {
            Map<String, String> map = msg.extra;
            if (map != null) {

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
