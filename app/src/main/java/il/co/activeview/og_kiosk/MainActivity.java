package il.co.activeview.og_kiosk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import il.co.activeview.og_kiosk.objects.Device;
import il.co.activeview.og_kiosk.services.MainService;

public class MainActivity extends Activity {
int PERMISSION_READ_STATE=7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        startService(new Intent(getApplicationContext(), MainService.class));
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
        return;
        }
    }

    public void onClick(View view) {
        LogDevice();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogDevice();
            }
        }).start();
    }

    private void LogDevice() {
        Device d = Device.getCurrent(getApplicationContext());
        Log.i("DeviceLog","isScreenOn: "+d.screenOn);
        Log.i("DeviceLog","batteryMode: "+d.batteryMode);
        Log.i("DeviceLog","isCharging: "+d.isCharging);
        Log.i("DeviceLog","volume: "+d.volume);
        Log.i("DeviceLog","imei: "+d.imei);
        Log.i("DeviceLog","screenOn: "+d.screenOn);
        Log.i("DeviceLog","connection: "+d.signalGsmValue);
    }
}
