package il.co.activeview.og_kiosk.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ConnectTask extends AsyncTask<String, String, TcpClient> {
    private Context context;
    private String serverIp = "";
    private String Imei = "";
    public TcpClient mTcpClient;

    public ConnectTask(Context context, String serverIp, String imei) {
        super();
        this.serverIp = serverIp;
        this.Imei = imei;
        this.context = context;
    }

    @Override
    protected TcpClient doInBackground(String... message) {

        //we create a TCPClient object
        mTcpClient = new TcpClient(context, serverIp, Imei, new TcpClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        });
        mTcpClient.run();

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //response received from server
        Log.d("test", "response " + values[0]);
        //process server response here....
    }


}
