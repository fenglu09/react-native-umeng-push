package com.mg.umeng.push;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class BadgenumberUtils {
    public static int Total = 0;
    public static String className = "com.mglink.mgcircle.MainActivity";

    public static void setBadgenumber(Context context) {
        Total = 1;
        _setBadgenumber(context, Total);
        return;
    }

    public static void clearAll(Context context) {
        Total = 0;
        _setBadgenumber(context, Total);
        return;
    }


    public static void _setBadgenumber(Context context, int num) {
        //华为
        if (RomUtil.isEmui()) {
            setEmuiBadgenumber(context, num);
            return;
        }

        //vivo
        if (RomUtil.isVivo()) {
            setVivoBadgenumber(context, num);
            return;
        }

    }


    public static void setVivoBadgenumber(Context context, int num) {
        try {

            Intent intent = new Intent();
            intent.setAction("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
            intent.putExtra("packageName", context.getPackageName());
            intent.putExtra("className", className);
            intent.putExtra("notificationNum", num);
            intent.addFlags(0x01000000);

            context.sendBroadcast(intent);
        } catch (Exception e) {
        }
    }


    public static void setEmuiBadgenumber(Context context, int num) {
        try {
            Bundle extra = new Bundle();
            extra.putString("package", context.getPackageName());
            extra.putString("class", className);
            extra.putInt("badgenumber", num);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception e) {

        }
    }


}
