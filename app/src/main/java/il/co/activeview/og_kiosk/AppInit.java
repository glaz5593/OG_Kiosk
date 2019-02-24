package il.co.activeview.og_kiosk;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import il.co.activeview.og_kiosk.services.MainService;
import il.co.activeview.og_kiosk.services.ServerService;
import il.co.activeview.og_kiosk.services.WindowService;

/**
 * Created by moshe on 20/02/2019.
 */

public class AppInit extends Application {
    public static int serverListenPortNumber=9876;
    public static int serverSendPortNumber=9870;
public static boolean rumWindowsService;
    public static String ServerAskParentWord = "IPIPIP";

    @Override
    public void onCreate() {

        runServices(getApplicationContext());
        super.onCreate();
    }

    public static void runServices(Context context) {
        if(!isMyServiceRunning(context, WindowService.class)){
            context.startService(new Intent(context, WindowService.class));
        }

        if(!isMyServiceRunning(context, WindowService.class)){
            context.startService(new Intent(context, MainService.class));
        }
    }

    private static boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
