package il.co.activeview.og_kiosk.objects;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;

/**
 * Created by moshe on 20/02/2019.
 */

public class Device {
    public static Boolean ScreenOn=true;
    public static int SignalGsmValue;
    public int            batteryMode;
    public boolean        isCharging;
    public int            volume;
    public String         imei;
    public Boolean        screenOn;
    public int            signalGsmValue;

    public static Battery battery;

    public static Device getCurrent(Context context) {
        Device res = new Device();
        if (battery != null) {
            res.batteryMode = battery.percent;
            res.isCharging = battery.isCharging;
        }
        res.imei = getIMEI(context);
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        res.volume= am.getStreamVolume(AudioManager.STREAM_MUSIC);

        res.screenOn = ScreenOn;
        res.signalGsmValue = SignalGsmValue;
        return res;
    }

    public static String getIMEI(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }
}
