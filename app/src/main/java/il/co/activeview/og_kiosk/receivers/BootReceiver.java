package il.co.activeview.og_kiosk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by moshe on 20/02/2019.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Settings.canDrawOverlays(context)) {
            //       context.startService(new Intent(context, WindowService.class));
        }
        //    context.startService(new Intent(context, MyService.class));
    }
}
