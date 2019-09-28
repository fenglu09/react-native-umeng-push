
package com.mg.umeng.push;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;


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
        if (clientId == null) {
            ininPushAgent(getReactApplicationContext(), pushInfo);
        }
    }

    /**
     * 清除Notifications
     *
     * @return Cid值
     */

    @ReactMethod
    public void clearAllNotifications() {
//        NotificationManager manager = (NotificationManager) getReactApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancelAll();
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

    public static void ininPushAgent(Context context, String pushInfo) {
        // 保存pushInfo
        RNUmengPushModule.pushInfo = pushInfo;
        PushAgent mPushAgent = PushAgent.getInstance(context);
        //监听消息返回
        mPushAgent.setMessageHandler(new MessageHandler());
        //通知点击事件
        mPushAgent.setNotificationClickHandler(new MyUmengNotificationClickHandler());
        mPushAgent.setDisplayNotificationNumber(10);

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
        callback.invoke(MipushTestActivity.param);
        MipushTestActivity.param = null;
    }

    public static void sendEvent(String eventName, @Nullable WritableMap params) {
        try {
            mRAC.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } catch (Exception e) {

        }

    }


}