package il.co.activeview.og_kiosk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import il.co.activeview.og_kiosk.objects.Device;

/**
 * Created by moshe on 20/02/2019.
 */

public class ScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Device.ScreenOn=false;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Device.ScreenOn=true;
        }else{
            Device.ScreenOn=null;
        }
    }
}
