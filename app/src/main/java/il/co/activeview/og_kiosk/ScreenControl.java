package il.co.activeview.og_kiosk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by משה on 26/02/2019.
 */

public class ScreenControl {
    OwnerLockManager ownerLockManager;
    boolean isLock;
    Context context;

    static ScreenControl mInstance;

    public static ScreenControl getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ScreenControl(context);
        }

        return mInstance;
    }

    public ScreenControl(Context context){
    this.context = context;
        ownerLockManager = new OwnerLockManager(context);
    }

    public   void getControl() {

        boolean initEnabled =  ownerLockManager.initLockAdmin();
        if(!initEnabled){
            isLock = false;
            Toast.makeText(context,"אין למכשיר זה הרשאות בעלים",Toast.LENGTH_LONG).show();
            return;
        }
        isLock = ownerLockManager.startLock();
        if(!isLock){
            Toast.makeText(context,"נעילת המכשיר נכשלה",Toast.LENGTH_LONG).show();
            return;
        }

        boolean isWriteSettingPermitted = checkSystemWritePermission();
        if(!isWriteSettingPermitted){
            Toast.makeText(context,"אנא אפשר לאפליקציה הרשאות כתיבה למכשיר",Toast.LENGTH_LONG).show();
            askSystemWritePermission(context);
            return;
        }
    }

    public void removeControl() {
        ownerLockManager.stopLock(context);
    }
    private void askSystemWritePermission(Context context){
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }
    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context);
        }
        return true;
    }
}
