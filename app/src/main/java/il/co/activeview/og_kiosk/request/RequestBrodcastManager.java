package il.co.activeview.og_kiosk.request;

import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.UUID;

import il.co.activeview.og_kiosk.AppInit;
import il.co.activeview.og_kiosk.Json;
import il.co.activeview.og_kiosk.UUID_List;

/**
 * Created by moshe on 17/02/2019.
 */

public class RequestBrodcastManager {

    public final static String EXTRA_REQUEST = "Request";
    public final static String EXTRA_REQUEST_PACK = "REQUEST_PACK";
    public final static String EXTRA_TARGET_ID = "TargetId";
    public final static String EXTRA_UUID_LIST= "UUID_LIST";

    public final static String Action_ADD_REQUEST = "il.co.activeview.serverservice.ADD_REQUEST";
    public final static String Action_CONFIRM_REQUEST = "il.co.activeview.serverservice.CONFIRM_REQUEST";
    public final static String Action_GET_REQUEST_List = "il.co.activeview.serverservice.GET_REQUEST_List";
    public final static String Action_REQUEST_PACK = "il.co.activeview.serverservice.Action_REQUEST_PACK";

    static RequestBrodcastManager mInstance;
    public RequestHash hash;

    public static RequestBrodcastManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestBrodcastManager();
        }

        return mInstance;
    }

    private RequestBrodcastManager() {
        hash = new RequestHash();
    }

    public void AskRequestList(Context context, int targetId) {
        Intent i = new Intent(Action_GET_REQUEST_List);
        i.putExtra(EXTRA_TARGET_ID, targetId);
        context.sendBroadcast(i);
     }

    public void AddRequest(Context context, Request request) {
        Intent i = new Intent(Action_ADD_REQUEST);
        i.putExtra(EXTRA_REQUEST, Json.toString(request));
        context.sendBroadcast(i);
     }

    public void AddRequestConfirm (Context context, UUID_List uids) {
        Intent i = new Intent(Action_CONFIRM_REQUEST);
        i.putExtra(EXTRA_UUID_LIST, Json.toString(uids));
        context.sendBroadcast(i);
    }

    public void sendRequestPackage(Context context,RequestPackage pack) {
        String p = Json.toString(pack);
        Intent intent = new Intent(RequestBrodcastManager.Action_REQUEST_PACK);
        intent.putExtra(RequestBrodcastManager.EXTRA_REQUEST_PACK, p);
        context.sendBroadcast(intent);
    }


    private void sendBroadcastToServer(String messageStr) {
        // Hack Prevent crash (sending should be done using an async task)
        StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        byte[] sendData = messageStr.getBytes();
        try {
            DatagramSocket sendSocket = new DatagramSocket(null);
            sendSocket.setReuseAddress(true);
            sendSocket.bind(new InetSocketAddress(AppInit.serverListenPortNumber));
            sendSocket.setBroadcast(true);

            //Broadcast to all IP addresses on subnet
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), AppInit.serverListenPortNumber);
                sendSocket.send(sendPacket);
                Log.i(getClass().getName(),  "Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
                Log.e("sendBroadcast", "IOException: " + e.getMessage());
            }
        } catch (IOException e) {
            Log.e("sendBroadcast", "IOException: " + e.getMessage());
        }
    }

}
