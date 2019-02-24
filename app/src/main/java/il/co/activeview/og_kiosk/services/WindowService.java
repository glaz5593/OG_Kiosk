package il.co.activeview.og_kiosk.services;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

import il.co.activeview.og_kiosk.AppInit;
import il.co.activeview.og_kiosk.MainActivity;
import il.co.activeview.og_kiosk.R;

public class WindowService extends Service {


    public static String TAG = "WindowService";
    private final MainActivityLifecycleCallbacks mainActivityLifecycleCallbacks = new MainActivityLifecycleCallbacks(this);
    private final MainBroadcastReceiver mainBroadcastReceiver = new MainBroadcastReceiver(this);

    private ImageView overlayHomeView;
    private ImageView overlayView;
    private boolean overlayVisible;
    private boolean screenOn;
    private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(!AppInit.rumWindowsService){
            return;
        }
        Log.i(TAG, "Launched");
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);

        if (wm != null) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            int width = size.x;
            int height = size.y;
            this.overlayView = new ImageView(this);
            ImageView imageView = this.overlayView;
            if (imageView != null) {
                imageView.setBackgroundColor(getResources().getColor(R.color.yellow));
                imageView.setImageResource(R.drawable.home);
            }

            File imgFile = new File("");
            if (imgFile.exists()) {
                Bitmap image = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView imageView2 = this.overlayView;
                if (imageView2 != null) {
                    imageView2.setImageBitmap(image);
                }
                Log.i(TAG, "IMAGE FOUND");
            } else {
                Log.e(TAG, "Overlay image not found");
            }

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2, 2006, 524328, 3);
            layoutParams.gravity = 17;
            layoutParams.x = 0;
            layoutParams.y = 0;
            layoutParams.width = width;
            layoutParams.height = height;
            wm.addView(this.overlayView, layoutParams);
            this.overlayVisible = true;
            this.overlayHomeView = new ImageView(this);
            ImageView imageView3 = this.overlayHomeView;
            if (imageView3 != null) {
                imageView3.setBackgroundColor(getResources().getColor(R.color.green));
            }
            Bitmap bitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.home));
            ImageView imageView4 = this.overlayHomeView;
            if (imageView4 != null) {
                imageView4.setImageBitmap(bitmap);
            }
            wm.addView(this.overlayHomeView, layoutParams);
            imageView4 = this.overlayHomeView;
            if (imageView4 != null) {
                imageView4.setVisibility(View.INVISIBLE);
            }

            getApplication().registerActivityLifecycleCallbacks(mainActivityLifecycleCallbacks);
            IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_ON");
            filter.addAction("android.intent.action.SCREEN_OFF");
            registerReceiver(mainBroadcastReceiver, filter);
            //startForeground(99, createNotification());
            new Handler().postDelayed(new KioskServiceOnCreate(this), 1000);
            return;
        }


    }

    public static boolean isServiceRunning = false;
    public static WindowService self;
    ServerSocket welcomeSocket;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isServiceRunning)
            return Service.START_STICKY;

        self = this;
        isServiceRunning = true;


        try {
            welcomeSocket = new ServerSocket(1122);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (isServiceRunning) {
                    try {
                        Socket connectionSocket = welcomeSocket.accept();
                        test();
                        Log.i(TAG, "Service Running");
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();*/

        return Service.START_STICKY;
    }
/*

    public void test() {
        Log.i(TAG, "test");
        Intent intent = new Intent(self, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void asyncTest() {
        Log.i(TAG, "asyncTest");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    test();
                } catch (Exception e) {
                }

            }
        }).start();
    }*/

    final class KioskServiceOnCreate implements Runnable {
        final WindowService WindowService;

        KioskServiceOnCreate(WindowService service) {
            WindowService = service;
        }

        public final void run() {
            Log.i(TAG, "Init");
            WindowService.showOverlay();
            WindowService.bringToForeground();
        }
    }

    public final class MainActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        final WindowService WindowService;
        final String TAG = "WindowService";

        MainActivityLifecycleCallbacks(WindowService service) {
            WindowService = service;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.i(TAG, "App started");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.i(TAG, "App resume");
            WindowService.hideOverlay();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.i(TAG, "App Paused");
            WindowService.showOverlay();
            WindowService.bringToForeground();
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.i(TAG, "App stopped");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            Log.i(TAG, "App saved state");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "App destroyed");
        }
    }

    public final class MainBroadcastReceiver extends BroadcastReceiver {
        final WindowService WindowService;

        MainBroadcastReceiver(WindowService service) {
            WindowService = service;
        }

        public void onReceive(Context context, Intent intent) {
            Object obj = null;
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                Log.i(TAG, "screen off");
                WindowService.screenOn = false;
                WindowService.showOverlay();
            } else if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                Log.i(TAG, "screen on");
                WindowService.screenOn = true;
                WindowService.bringToForeground();
                WindowService.hideOverlay();
            }
        }
    }

    public final void showOverlay() {
        Log.i(TAG, "showOverlay");
        ImageView imageView = this.overlayView;
        if (imageView != null) {
            imageView.setVisibility(View.VISIBLE);
        }
        if (!this.overlayVisible) {
            // TODO
            //this.onOverlayShowCallback.invoke();
        }
        this.overlayVisible = true;
    }

    public final void hideOverlay() {
        Log.i(TAG, "hideOverlay");
        ImageView imageView = this.overlayView;
        if (imageView != null) {
            imageView.setVisibility(View.INVISIBLE);
        }
        this.overlayVisible = false;
    }

    public final void bringToForeground() {
        Log.i(TAG, "bringToForeground");
        Intent setAppForegroundIntent = new Intent(getBaseContext(), MainActivity.class);
        setAppForegroundIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        setAppForegroundIntent.setAction("android.intent.action.MAIN");
        setAppForegroundIntent.addCategory("android.intent.category.LAUNCHER");
        setAppForegroundIntent.putExtra("screenOn", true);
        startActivity(setAppForegroundIntent);
    }

}

