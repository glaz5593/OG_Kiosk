package il.co.activeview.og_kiosk;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import il.co.activeview.og_kiosk.objects.Device;
import il.co.activeview.og_kiosk.receivers.ScreenReceiver;
import il.co.activeview.og_kiosk.services.MainService;
import il.co.activeview.og_kiosk.services.WindowService;

/**
 * Created by moshe on 20/02/2019.
 */

public class MainActivity extends Activity {
    public static String ResumeTimeExtra = "ResumeTimeExtra";
    int PERMISSION_READ_STATE = 7;
    int counter = 0;
   public static String AreYouResumeAction="AreYouResumeAction";
   public static String IResumeAction="IResumeAction";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        checkPermissionM();
        AppInit.runServices(getApplicationContext());
    }

    private void checkPermissionM() {
        counter++;

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (counter < 5) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
            }

            return;
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        checkPermissionM();
        AppInit.runServices(getApplicationContext());
    }

    BroadcastReceiver receiver;
    @Override
    protected void onResume() {
        super.onResume();
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendBroadcast(new Intent(), IResumeAction);
            }
        };
        registerReceiver(receiver,new IntentFilter(AreYouResumeAction));
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }
}
