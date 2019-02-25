package il.co.activeview.og_kiosk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

public class KioskActivity extends AppCompatActivity {
int counter=0;
    int passCounter=0;
    UUID threadUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);
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
            ScreenControl.getInstance(getApplicationContext()).removeControl();
            return;
        }
        counter=20;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScreenControl.getInstance(getApplicationContext()).getControl();

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

    private void startApplication() {
        Intent intent = new Intent("com.twidroid.SendTweet");
    }
}
