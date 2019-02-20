package il.co.activeview.og_kiosk.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import il.co.activeview.og_kiosk.Json;
import il.co.activeview.og_kiosk.objects.Battery;
import il.co.activeview.og_kiosk.objects.Device;
import il.co.activeview.og_kiosk.receivers.ScreenReceiver;
import il.co.activeview.og_kiosk.request.Request;
import il.co.activeview.og_kiosk.request.RequestBrodcastManager;
import il.co.activeview.og_kiosk.request.RequestHash;
import il.co.activeview.og_kiosk.request.RequestPackage;


public class MainService extends Service {
    private NotificationManager mNM;
    private int NOTIFICATION = 12344;
    String TAG = "MainService";
    PhoneStateReceiver phoneStateListener;

    public class MyServiceBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runThread();
        listenToBaggeryBrodcast();
        listenToScreenBrodcast();
        listenToGsmSignal();
        listenToRequestRequired();
        startForeground(NOTIFICATION, getNotification());
    }

    private void listenToRequestRequired() {
        IntentFilter intentFilter=new IntentFilter() ;
        intentFilter.addAction(RequestBrodcastManager.Action_GET_REQUEST_List);
        intentFilter.addAction(RequestBrodcastManager.Action_ADD_REQUEST);
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                switch (action){
                    case RequestBrodcastManager.Action_ADD_REQUEST: {
                        String sR = intent.getStringExtra(RequestBrodcastManager.EXTRA_REQUEST);
                        Request r = Json.toObject(sR,Request.class);

                        if (r != null) {
                            RequestBrodcastManager.getInstance().hash.addRequest(r);
                        }
                        break;
                    }
                    case RequestBrodcastManager.Action_GET_REQUEST_List:{
                        int targetId=intent.getIntExtra(RequestBrodcastManager.EXTRA_TARGET_ID,0);
                        sendRequestPackage(targetId);
                        break;
                    }
                }
            }
        }, new IntentFilter(RequestBrodcastManager.Action_GET_REQUEST_List));
    }
    private void sendRequestPackage(int targetId) {
        RequestPackage pack=RequestBrodcastManager.getInstance().hash.getRequestPackage(targetId);
        if(pack != null && pack.requests.size() > 0){
            sendRequestPackage(pack);
        }
    }
    private void sendRequestPackage(RequestPackage pack) {
        String p = Json.toString(pack);
        Intent intent = new Intent(RequestBrodcastManager.Action_REQUEST_PACK);
        intent.putExtra(RequestBrodcastManager.EXTRA_REQUEST_PACK, p);
        sendBroadcast(intent);
    }

    private void listenToGsmSignal() {
        phoneStateListener = new PhoneStateReceiver();
        TelephonyManager mTelephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    private void listenToScreenBrodcast() {
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenReceiver(), screenStateFilter);

    }

    private void listenToBaggeryBrodcast() {
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Device.battery = new Battery(intent);
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

    }

    public class PhoneStateReceiver extends PhoneStateListener{
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            try {
                Device.SignalGsmValue  = signalStrength.getGsmSignalStrength();
                Log.i(TAG, "onSignalStrengthsChanged gsm: " +   Device.SignalGsmValue);
            } catch (Exception ex) {
                Log.e(TAG, "onSignalStrengthsChanged", ex);
            }
        }
    }
    private void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        super.onDestroy();
    }

    private final IBinder mBinder = new MyServiceBinder();

    private Notification getNotification() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        CharSequence text = "";

        Notification notification = new Notification.Builder(this)
                .setTicker(text)
                .setWhen(System.currentTimeMillis())
                .setContentTitle("")
                .setContentText(text)
                .build();

        return notification;
    }
}