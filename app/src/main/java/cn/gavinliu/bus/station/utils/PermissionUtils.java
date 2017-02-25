package cn.gavinliu.bus.station.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by gavin on 2017/2/25.
 */

public class PermissionUtils {

    public static boolean checkPermission(Activity context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Toast.makeText(context, "当前无权限，请授权！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:cn.gavinliu.bus.station"));
                context.startActivityForResult(intent, 0);

                return false;
            }
        }
        return true;
    }
}
