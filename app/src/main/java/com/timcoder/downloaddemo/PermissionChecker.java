package com.timcoder.downloaddemo;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author Administrator
 * 2019/10/8 0008.
 */
public class PermissionChecker {

    public static boolean lackPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }

    public static void requestPermission(Context context, String perimission) {
//        ActivityCompat.requestPermissions();
    }

}
