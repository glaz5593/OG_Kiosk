package il.co.activeview.og_kiosk;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


import il.co.activeview.og_kiosk.request.Request;
import il.co.activeview.og_kiosk.request.RequestBrodcastManager;

/**
 * Created by moshe on 21/02/2019.
 */

public class ServerListener {
    Context context;
  public int port;

    private static final String TAG = "ServerListener";


    private Thread listenerThread;
    private ServerSocket serverSocket;

    public ServerListener(Context context,final int port ) {
        this.context = context;
        this.port = port;

        Log.i(TAG, "init port:" + port);
        listenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Listen port:" + port);
                Listen(port);
            }
        });
    }

    public void start() {
        listenerThread.start();
    }

    public void stop() {
        Log.i(TAG, "stop port:" + port);
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        serverSocket = null;
        listenerThread = null;
    }

    private void Listen(int serverPort) {
        while (true) {
            try {
                if (serverSocket == null) {
                    Log.i(TAG, "set socket port:" + port);
                    serverSocket = new ServerSocket();
                    serverSocket.setReuseAddress(true);
                    serverSocket.bind(new InetSocketAddress(serverPort));
                }
                break;
            } catch (Exception e) {
                Log.e(TAG, "error set socket port:" + port, e);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

        while (serverSocket != null && !serverSocket.isClosed()) {
            try {
                Log.i(TAG, "accept port:" + port);
                Socket client = serverSocket.accept();  // listen for incoming clients
                HandleSocket(client);
            } catch (Exception e) {
                Log.e(TAG, "error accept " + "" + port, e);
            }
        }
    }

    private void HandleSocket(Socket client) {
        BufferedInputStream bis = null;
        try {
            Log.i(TAG, "HandleSocket port:" + port);
            bis = new BufferedInputStream(client.getInputStream());
            String msg = "";
            byte[] contents = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = bis.read(contents)) != -1) {
                msg += new String(contents, 0, bytesRead);
            }
            HandleMessage(msg);
        } catch (Exception e) {
            Log.e(TAG, "error HandleSocket" + "" + port, e);
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                Log.e(TAG, "error HandleSocket in finally" + "" + port, e);
            }
        }
    }

    private void HandleMessage(String msg) {
        Log.i(TAG, "HandleMessage port:" + port + " message:" + msg);
        try {
                Request request = Json.toObject(msg, Request.class);
                HandleRequest(request);
        } catch (Exception e) {
            Log.e(TAG, "error HandleMessage" + "" + port + "\nmessage:" + msg, e);
        }
    }

    private void HandleRequest(Request request) {
        RequestBrodcastManager.getInstance().AddRequest(context, request);
    }
}