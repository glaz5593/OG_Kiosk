package il.co.activeview.og_kiosk.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by moshe on 20/02/2019.
 */

public class ConnectTask extends AsyncTask<String, String, TcpClient> {
    private Context context;
    private String serverIp = "";
    public TcpClient mTcpClient;

    public ConnectTask(Context context, String serverIp) {
        super();
        this.serverIp = serverIp;
        this.context = context;
    }

    @Override
    protected TcpClient doInBackground(String... message) {
        mTcpClient = new TcpClient(context, serverIp, new TcpClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                 publishProgress(message);
            }
        });
        mTcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.i("test", "response " + values[0]);
     }


}
