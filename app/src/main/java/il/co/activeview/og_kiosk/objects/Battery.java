package il.co.activeview.og_kiosk.objects;

import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by משה on 20/02/2019.
 */

public class Battery {
    public  int percent;
    public  boolean isCharging;

    public Battery (){
        percent = 0;
        isCharging = false;
    }

    public Battery (Intent intent){
        try{
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;
            percent = (int)(batteryPct * 100);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            if (status != BatteryManager.BATTERY_STATUS_FULL) {
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
            }
        }catch (Exception ex){
            Log.e("Battery", "set percent in battery Error", ex);
        }
    }

    public boolean equalsCharging(Battery b) {
        return isCharging==b.isCharging;
    }

    public boolean equalsPercent(Battery b) {
        return percent ==b.percent;
    }

    public boolean equals(Battery b) {
        return equalsCharging(b) && equalsPercent(b);
    }
}
