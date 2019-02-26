package il.co.activeview.og_kiosk;

import android.Manifest;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.UUID;

public class KioskActivity extends AppCompatActivity {
    public static String ResumeTimeExtra = "ResumeTimeExtra";
    int PERMISSION_READ_STATE = 7;
    int counter = 0;
    public static String AreYouResumeAction="AreYouResumeAction";
    public static String IResumeAction="IResumeAction";

    int passCounter=0;
    UUID threadUid;
    BroadcastReceiver receiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_kiosk);

        checkPermissionM();
        AppInit.runServices(getApplicationContext());
    }
    private void checkPermissionM() {
        counter++;

        if (ContextCompat.checkSelfPermission(KioskActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (counter < 5) {
                ActivityCompat.requestPermissions(KioskActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
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

    public void onLogoClick(View view) {
        if(counter == 14){
            passCounter++;
            return;
        }
        if(counter == 10 && passCounter ==1){
            passCounter++;
            return;
        }
        if(counter == 6 && passCounter ==2){
            counter=0;
            ScreenControl.getInstance(KioskActivity.this).removeControl();
            return;
        }
        counter=20;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenControl.getInstance(KioskActivity.this).getControl();

//        receiver=new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                sendBroadcast(new Intent(), IResumeAction);
//            }
//        };
//        registerReceiver(receiver,new IntentFilter(AreYouResumeAction));

        final UUID uid = UUID.randomUUID();
        threadUid=uid;
        counter = 1;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (counter> 0 && counter < 5 && uid.equals(threadUid)){
                      counter++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (counter>= 5 && uid.equals(threadUid)){
                    startApplication();
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
       // unregisterReceiver(receiver);
        super.onPause();
    }

    private void startApplication() {
        Intent intent = new Intent("com.twidroid.SendTweet");
    }
}
