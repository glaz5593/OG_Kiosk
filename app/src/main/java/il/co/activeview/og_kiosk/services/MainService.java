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
import android.os.StrictMode;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;

import il.co.activeview.og_kiosk.AppInit;
import il.co.activeview.og_kiosk.Json;
import il.co.activeview.og_kiosk.MainActivity;
import il.co.activeview.og_kiosk.ServerListener;
import il.co.activeview.og_kiosk.UUID_List;
import il.co.activeview.og_kiosk.objects.Battery;
import il.co.activeview.og_kiosk.objects.Device;
import il.co.activeview.og_kiosk.receivers.ScreenReceiver;
import il.co.activeview.og_kiosk.request.Request;
import il.co.activeview.og_kiosk.request.RequestBrodcastManager;
import il.co.activeview.og_kiosk.request.RequestHash;
import il.co.activeview.og_kiosk.request.RequestPackage;

/**
 * Created by moshe on 20/02/2019.
 */

public class MainService extends Service {
    private NotificationManager mNM;
    private int NOTIFICATION = 12344;
    String TAG = "MainService";
    PhoneStateReceiver phoneStateListener;
    private DataUpdateReceiver dataUpdateReceiver;
    ConnectTask connectTask;
    Intent serviceIntent;
    ServerListener serverListener;

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
        initServerService();
        //initServerListener();

        startForeground(NOTIFICATION, getNotification());
    }

    private void initServerListener() {
        ServerListener serverListener=new ServerListener(getApplicationContext(), AppInit.serverSendPortNumber);
        serverListener.start();
    }

    private void initServerService() {
        serviceIntent = new Intent(this, ServerService.class);
        startService(serviceIntent);

        dataUpdateReceiver = new DataUpdateReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Connect");
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("Connect")) {
                 String serverIp = intent.getExtras().getString("serverIp");
                connectTask =  new ConnectTask(context, serverIp);
                connectTask.execute("");

                new Thread(new Runnable() {
                    public void run() {
                        stopService(serviceIntent);
                        boolean keepAlive = true;
                        while(keepAlive) {
                            if (connectTask.mTcpClient == null || !connectTask.mTcpClient.isAlive) {
                                serviceIntent = new Intent(getApplicationContext(), ServerService.class);
                                startService(serviceIntent);
                                keepAlive = false;
                            }
                        }
                    }
                }).start();
            }
        }
    }


    private void listenToRequestRequired() {
        IntentFilter intentFilter=new IntentFilter() ;
        intentFilter.addAction(RequestBrodcastManager.Action_GET_REQUEST_List);
        intentFilter.addAction(RequestBrodcastManager.Action_CONFIRM_REQUEST);
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
                    case RequestBrodcastManager.Action_CONFIRM_REQUEST:{
                        String sList = intent.getStringExtra(RequestBrodcastManager.EXTRA_UUID_LIST);
                        UUID_List list = Json.toObject(sList,UUID_List.class);
                        RequestBrodcastManager.getInstance().hash.remove(list);
                        break;
                    }
                }
            }
        }, new IntentFilter(RequestBrodcastManager.Action_GET_REQUEST_List));
    }
    private void sendRequestPackage(int targetId) {
        RequestPackage pack=RequestBrodcastManager.getInstance().hash.getRequestPackage(targetId);
        if(pack != null && pack.requests.size() > 0){
            RequestBrodcastManager.getInstance().sendRequestPackage(getApplicationContext(), pack);
        }
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

    Date resumeDate;
    private void runThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Date d=new Date();
                    resumeDate=null;
                    Intent intent=new Intent() ;
                    intent.putExtra(MainActivity.ResumeTimeExtra,d.getTime());
                    sendBroadcast(new Intent(),MainActivity.AreYouResumeAction);


                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
int counter=0;
                    while (counter< 7 &&resumeDate==null){
                        try {
                            counter++;
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    if(!MainActivity.isActive) {
                        Intent setAppForegroundIntent = new Intent(getBaseContext(), MainActivity.class);
                        setAppForegroundIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        setAppForegroundIntent.setAction("android.intent.action.MAIN");
                        setAppForegroundIntent.addCategory("android.intent.category.LAUNCHER");
                        setAppForegroundIntent.putExtra("screenOn", true);
                        startActivity(setAppForegroundIntent);
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