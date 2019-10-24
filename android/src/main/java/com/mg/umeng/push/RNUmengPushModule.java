
package com.mg.umeng.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

import androidx.annotation.Nullable;


public class RNUmengPushModule extends ReactContextBaseJavaModule {

    public static String pushInfo = "";


    public final static String TAG = "RNUmengPushModule";
    public static String clientId = null;
    //    public final ReactApplicationContext reactContext;
    private static ReactApplicationContext mRAC;

    public RNUmengPushModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mRAC = reactContext;
    }

    @Override
    public String getName() {
        return "RNUmengPush";
    }


    /**
     * init
     *
     * @return Cid值
     */
    @ReactMethod
    public void initPush() {
        ininPushAgent(getReactApplicationContext(), pushInfo);
    }

    /**
     * 清除Notifications
     *
     * @return Cid值
     */

    @ReactMethod
    public void clearAllNotifications() {
        Log.d("clearAllNotifications", "-clearAllNotifications-");

        NotificationManager manager = (NotificationManager) getReactApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }


    @ReactMethod
    public void setApplicationIconBadgeNumber(int number) {
        ApplicationBadgeHelper.INSTANCE.setApplicationIconBadgeNumber(getReactApplicationContext(), number);
    }

    /**
     * 获取SDK的Cid
     *
     * @return Cid值
     */
    @ReactMethod
    public void clientId(Callback callback) {

        PushAgent mPushAgent = PushAgent.getInstance(getReactApplicationContext());
        mPushAgent.enable(null);
        callback.invoke(clientId);
    }

    /**
     * 开启通知渠道设置
     */
    @ReactMethod
    public void openNotificationSetting() {
        Context context = getReactApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            boolean check = NotificationUtil.isNotificationEnabled(context);
            if (check == false) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
                    //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
                    intent.putExtra("app_package", context.getPackageName());
                    intent.putExtra("app_uid", context.getApplicationInfo().uid);
                    context.startActivity(intent);
                } catch (Exception e) {

                    Intent intent = new Intent();
                    //下面这种方案是直接跳转到当前应用的设置界面。
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                    intent.setData(uri);
                    context.startActivity(intent);
                }
                return;

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            String id = context.getPackageName();
            NotificationChannel channel = mManager.getNotificationChannel(id);

            Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
            context.startActivity(intent);
        }
    }

    /**
     * 通知渠道是否开启
     *
     * @param callback
     */

    @ReactMethod
    public void checkNotification(Callback callback) {
        boolean needOpen = false;
        Context context = getReactApplicationContext();

        // areNotificationsEnabled方法的有效性官方只最低支持到API 19，低于19的仍可调用此方法不过只会返回true，即默认为用户已经开启了通知。
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                boolean check = NotificationUtil.isNotificationEnabled(context);
                if (check == false) {
                    callback.invoke(true);
                    return;
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager mManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                String id = context.getPackageName();
                NotificationChannel channel = mManager.getNotificationChannel(id);
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    needOpen = true;
                }
            }

            callback.invoke(needOpen);
        } catch (Exception e) {

        }

    }

    public static void ininPushAgent(final Context context, String pushInfo) {
        // 保存pushInfo
        RNUmengPushModule.pushInfo = pushInfo;
        PushAgent mPushAgent = PushAgent.getInstance(context);
        //监听消息返回
        mPushAgent.setMessageHandler(new MessageHandler());
        //通知点击事件
//        mPushAgent.setNotificationChannelName(PushInfo.ChannelName);
        mPushAgent.setNotificationClickHandler(new MyUmengNotificationClickHandler());
        mPushAgent.setDisplayNotificationNumber(8);

        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.d(TAG, deviceToken);
                clientId = deviceToken;
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.d(TAG, s);
            }
        });

        // 初始化厂商推送
        ManufacturerTool.initManufacturer(context, pushInfo);
    }


    @ReactMethod
    public void destroy() {
        PushAgent mPushAgent = PushAgent.getInstance(getReactApplicationContext());
        mPushAgent.disable(null);
    }

    @ReactMethod
    public void ManufacturerCallback(Callback callback) {
        if (UmengPushActivity.param == null) {
            callback.invoke(false);
            return;
        }
        callback.invoke(UmengPushActivity.param);
        UmengPushActivity.param = null;
    }

    public static void sendEvent(String eventName, @Nullable WritableMap params) {
        try {
            mRAC.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } catch (Exception e) {

        }

    }


}