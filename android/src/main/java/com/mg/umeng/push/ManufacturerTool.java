package com.mg.umeng.push;

import android.app.Application;
import android.content.Context;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.json.JSONObject;


public class ManufacturerTool {
    public static void initManufacturer(Context context, String pushInfo) {
        try {
            JSONObject object = new JSONObject(pushInfo);
            String brand = android.os.Build.BRAND;
            switch (brand) {
                case "vivo":
                    VivoRegister.register(context);
                    break;
                case "OPPO":
                    String key = object.optString("OppoAppKey");
                    String secret = object.optString("OppoAppSecret");
                    OppoRegister.register(context, key, secret);
                    break;

                case "HUAWEI":
                    HuaWeiRegister.register((Application) context);
                    break;

                case "Meizu":
                    String appId = object.optString("MeizuAppId");
                    String appKey = object.optString("MeizuAppKey");
                    MeizuRegister.register(context, appId, appKey);
                    break;
                case "Xiaomi":
                    String XiaoMiAppId = object.optString("XiaoMiAppId");
                    String XiaoMiAppKey = object.optString("XiaoMiAppKey");
                    MiPushRegistar.register(context, XiaoMiAppId, XiaoMiAppKey);
                    break;

            }
        } catch (Exception e) {
        }

    }
}
