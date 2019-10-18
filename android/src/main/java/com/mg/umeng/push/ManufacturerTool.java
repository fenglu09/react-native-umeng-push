package com.mg.umeng.push;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;


public class ManufacturerTool {

    public static boolean isAppForeground(Context context, String packageName) {
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

    public static void initManufacturer(final Context context, String pushInfo) {
        try {
            JSONObject object = new JSONObject(pushInfo);

            //华为
            if (RomUtil.isEmui()) {
                HuaWeiRegister.register((Application) context);
                return;
            }
            //魅族
            if (RomUtil.isFlyme()) {
                String appId = object.optString("MeizuAppId");
                String appKey = object.optString("MeizuAppKey");
                MeizuRegister.register(context, appId, appKey);
                return;
            }
            //小米
            if (RomUtil.isMiui()) {
                String XiaoMiAppId = object.optString("XiaoMiAppId");
                String XiaoMiAppKey = object.optString("XiaoMiAppKey");
                MiPushRegistar.register(context, XiaoMiAppId, XiaoMiAppKey);
                return;
            }
            //vivo
            if (RomUtil.isVivo()) {
                VivoRegister.register(context);
                return;
            }
            //Oppo
            if (RomUtil.isOppo()) {
                String key = object.optString("OppoAppKey");
                String secret = object.optString("OppoAppSecret");
                OppoRegister.register(context, key, secret);
                return;
            }
        } catch (Exception e) {
        }

    }
}
