package il.co.activeview.og_kiosk.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import il.co.activeview.og_kiosk.AppInit;

/**
 * Created by moshe on 20/02/2019.
 */

public class ServerService extends IntentService {

    private boolean keepBroadcast = true;
    private boolean listen = true;
    Handler HN = new Handler();

    public ServerService() {
        super("ServerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HN.post(new DisplayToast("Start The Service!"));
        RunUdpConnection();
    }

    private void RunUdpConnection() {
        try {
            if (listen) {
                new Thread(new Runnable() {
                    public void run() {
                        startUDPListener();
                    }
                }).start();
            }

            // try broadcast
            while (keepBroadcast) {
                Log.i(getClass().getName(),  "Try to BROADCAST!");
                sendBroadcast(AppInit.ServerAskParentWord);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            Log.i(getClass().getName(),  "Error: " + e.getMessage());
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startUDPListener() {
        try {
            int port = 11000;

            DatagramSocket dsocket = new DatagramSocket(null);
            dsocket.setReuseAddress(true);
            dsocket.bind(new InetSocketAddress(port));
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            String serverIp = "";

            while (listen) {
                dsocket.receive(packet);
                serverIp = new String(buffer, 0, packet.getLength());
                Log.i("UDP packet received", serverIp);
                Log.i(getClass().getName(),  "UDP packet received" + serverIp);
                //HN.post(new DisplayToast(serverIp));
                listen = false;
                keepBroadcast = false;
                packet.setLength(buffer.length);
            }

            dsocket.close();

            if(!listen) {
                Intent intent = new Intent("Connect");
                intent.putExtra("serverIp", serverIp);
                sendBroadcast(intent);
            }
        } catch (Exception e) {
            Log.i(getClass().getName(),  " >"+ e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void sendBroadcast(String messageStr) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        byte[] sendData = messageStr.getBytes();
        try {
            DatagramSocket sendSocket = new DatagramSocket(null);
            sendSocket.setReuseAddress(true);
            sendSocket.bind(new InetSocketAddress(9876));
            sendSocket.setBroadcast(true);

            //Broadcast to all IP addresses on subnet
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 9876);
                sendSocket.send(sendPacket);
                Log.i(getClass().getName(),  "Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
                Log.e("sendBroadcast", "IOException: " + e.getMessage());
            }
        } catch (IOException e) {
            Log.e("sendBroadcast", "IOException: " + e.getMessage());
        }
    }

    private class DisplayToast implements Runnable {

        String TM = "";

        public DisplayToast(String toast){
            TM = toast;
        }

        public void run(){
            Toast.makeText(getApplicationContext(), TM, Toast.LENGTH_SHORT).show();
        }
    }
}