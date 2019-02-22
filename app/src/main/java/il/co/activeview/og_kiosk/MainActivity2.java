package il.co.activeview.og_kiosk;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public class MainActivity2 extends AppCompatActivity {
//
//    ConnectTask connectTask;
//    String Imei;
//    Intent serviceIntent;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//
//        Imei = "No Permission";
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//            Imei = telephonyManager.getImei();
//        }
//
//        serviceIntent = new Intent(this, MyIntentService.class);
//        serviceIntent.putExtra("Imei", Imei);
//        startService(serviceIntent);
//
//        Intent intent = new Intent("glazman.Disconnect");
//        sendBroadcast(intent);
//
//
//        final EditText txtIMEI = findViewById(R.id.txtIMEI);
//      txtIMEI.setText(Imei);
//
//        Button button = (Button) findViewById(R.id.btnIMEI);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                sendLocalBroadcast(txtIMEI.getText().toString());
//            }
//        });
//
//        final EditText txtIP = findViewById(R.id.txtIP);
//        Button button1 = (Button) findViewById(R.id.btnIP);
//        button1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if(connectTask.mTcpClient != null && connectTask.mTcpClient.isAlive){
//                    connectTask.mTcpClient.sendMessage("Hello From Activity");
//                }
//            }
//        });
//
//        if (dataUpdateReceiver == null)
//            dataUpdateReceiver = new DataUpdateReceiver();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("Connect");
//        registerReceiver(dataUpdateReceiver, intentFilter);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(dataUpdateReceiver);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("Connect");
//        registerReceiver(dataUpdateReceiver, intentFilter);
//    }
//
//    private DataUpdateReceiver dataUpdateReceiver;
//    private class DataUpdateReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals("Connect")) {
//                Toast.makeText(context, "Connect", Toast.LENGTH_SHORT).show();
//                String serverIp = intent.getExtras().getString("serverIp");
//                connectTask =  new ConnectTask(context, serverIp, Imei);
//                connectTask.execute("");
//
//                new Thread(new Runnable() {
//                    public void run() {
//                        stopService(serviceIntent);
//                        boolean keepAlive = true;
//                        while(keepAlive) {
//                            if (connectTask.mTcpClient == null || !connectTask.mTcpClient.isAlive) {
//                                serviceIntent = new Intent(getApplicationContext(), MyIntentService.class);
//                                serviceIntent.putExtra("Imei", Imei);
//                                startService(serviceIntent);
//                                keepAlive = false;
//                            }
//                        }
//                    }
//                }).start();
//            }
//        }
//    }
//
//
//    private void sendLocalBroadcast(String messageStr) {
//        // Hack Prevent crash (sending should be done using an async task)
//        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        byte[] sendData = messageStr.getBytes();
//        try {
//            DatagramSocket sendSocket = new DatagramSocket(null);
//            sendSocket.setReuseAddress(true);
//            sendSocket.bind(new InetSocketAddress(9876));
//            sendSocket.setBroadcast(true);
//
//            //Broadcast to all IP addresses on subnet
//            try {
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 9876);
//                sendSocket.send(sendPacket);
//                System.out.println(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
//            } catch (Exception e) {
//                Log.e("sendBroadcast", "IOException: " + e.getMessage());
//            }
//        } catch (IOException e) {
//            Log.e("sendBroadcast", "IOException: " + e.getMessage());
//        }
//    }

}
