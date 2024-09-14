package com.rcloud.netflix;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class StoragePermissionUtil {

    //new
    private static String[] getStoragePermissionList() {
        String[] PERMISSIONS_REQUIRED;

        if (Build.VERSION.SDK_INT >= 33) {
            PERMISSIONS_REQUIRED = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
            };
        } else {
            PERMISSIONS_REQUIRED = new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
        return PERMISSIONS_REQUIRED;
    }

    public static boolean isAllStoragePermissionsGranted(Context context) {
        for (String permission : getStoragePermissionList()) {
            // System.out.println("Rajan_permission"+ permission+ ContextCompat.checkSelfPermission(context, permission));
            if (ContextCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestAllStoragePermissions(Activity activity, int requestCode) {
        final ArrayList<String> arrayList = new ArrayList<>();
        for (String permission : getStoragePermissionList()) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PERMISSION_GRANTED) {
                arrayList.add(permission);
            }
        }

        //for converting arraylist to string array
        String[] strArr = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            strArr[i] = (String) arrayList.get(i);
        }
        if (strArr.length > 0) {
            ActivityCompat.requestPermissions(activity, strArr, requestCode);
        }
    }
}
