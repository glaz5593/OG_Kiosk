package il.co.activeview.og_kiosk.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import il.co.activeview.og_kiosk.Json;
import il.co.activeview.og_kiosk.UUID_List;
import il.co.activeview.og_kiosk.objects.Device;
import il.co.activeview.og_kiosk.request.Request;
import il.co.activeview.og_kiosk.request.RequestBrodcastManager;
import il.co.activeview.og_kiosk.request.RequestPackage;

/**
 * Created by moshe on 20/02/2019.
 */

public class TcpClient {

    public static final String TAG = TcpClient.class.getSimpleName();
    public static String SERVER_IP = ""; //server IP address
    public static final int SERVER_PORT = 11001;

    public boolean isAlive = true;

    private String mServerMessage;
    private boolean mRun = false;
    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;

    private Context context;

    public TcpClient(Context context, String serverIp) {
        SERVER_IP = serverIp;
        this.context = context;
    }

    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.i(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);

            Log.i("TCP Client", "C: Connecting...");

            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {

                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                new Thread(new Runnable() {
                    public void run() {
                        while (isAlive) {
                            try {
                                Device device = Device.getInstance(context);
                                sendMessage(Json.toString(device));
                                Thread.sleep(5000);

                                RequestPackage requestPackage = RequestBrodcastManager.getInstance().hash.getRequestPackage(0);
                                if (requestPackage != null && requestPackage.requests.size() > 0) {
                                    UUID_List list = new UUID_List();
                                    for (Request r : requestPackage.requests) {
                                        sendMessage(Json.toString(r));
                                        list.add(r.uid);
                                        Thread.sleep(500);
                                    }
                                    RequestBrodcastManager.getInstance().AddRequestConfirm(context, list);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

                while (mRun) {

                    mServerMessage = mBufferIn.readLine();

                    if (mServerMessage != null) {
                        Request r = Json.toObject(mServerMessage, Request.class);
                        RequestBrodcastManager.getInstance().AddRequest(context, r);
                    }
                }

                Log.i("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {
                Intent intent = new Intent("Disconnect");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } finally {
                socket.close();
            }

        } catch (Exception e) {
            Log.e("TCP", "C: Error", e);
        }
        isAlive = false;
    }

    public interface OnMessageReceived {
        void messageReceived(String message);
    }
}