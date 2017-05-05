package org.wleclair.myrobotclient.activity;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.common.ResetServerIP;
import org.wleclair.myrobotclient.R;
import org.wleclair.myrobotclient.bot.Robot;
import org.wleclair.myrobotclient.comm.ConnectedClient;
import org.wleclair.myrobotclient.comm.ConnectedServer;
import org.wleclair.myrobotclient.log.Log;
import org.wleclair.myrobotclient.log.LogView;
import org.wleclair.myrobotclient.log.LogWrapper;
import org.wleclair.myrobotclient.log.MessageOnlyLogFilter;

import java.io.IOException;
import java.util.Set;
import java.util.Timer;

public class MainActivity extends Activity {

    String serverIP = "192.168.1.5";
    Timer myTimer;
    int port = 9089;
    long duration=120;
    ConnectedClient client = new ConnectedClient(serverIP);
    Handler fromServer = new Handler(client);
    Robot rob=null;
    Handler robotHandler = null;
    ConnectedServer server = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    BluetoothDevice hexabot=null;

    private static final String TAG = "MainActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        LinearLayout lassie = (LinearLayout) findViewById(R.id.myLinearView);
//        EditText eddie = (EditText)
        try {
            rob = new Robot(client);
        } catch(IOException io) {
            Toast.makeText(this, io.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
        robotHandler = rob.robotHandler;
        TextView loggy = (TextView) findViewById(R.id.textView);
        loggy.setMovementMethod(new ScrollingMovementMethod());
        LogView logger = new LogView(this, loggy);
        rob.setLogView(logger);
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        msgFilter.setNext(logger);
        WifiManager manag = (WifiManager) getSystemService(getBaseContext().WIFI_SERVICE);
        WifiInfo winfo = manag.getConnectionInfo();
        myTimer = new Timer();
        ResetServerIP ipResetTimer = new ResetServerIP(client,winfo);
        myTimer.schedule(ipResetTimer,1000l,300000l);
        server = new ConnectedServer(rob,client);
        server.setIPAddress(intToIP(winfo.getIpAddress()));
        Log.i(TAG, "Ready");
        AsyncTaskRunner runner = new AsyncTaskRunner(client);
        runner.execute();
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    public String intToIP(int i) {
        return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) +
                "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF));
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        ProgressDialog progressDialog;
        ConnectedClient client;

        public AsyncTaskRunner(ConnectedClient client){
            this.client = client;
        }

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {
                rob.start();
                server.start();
                int time = 100;
                for (int i = 0; i < 25; i++) {
                    Log.d(TAG, "message number " + i);
                    Thread.sleep(time);
                }
//                while (true) {
//                    try {
//                        client.send("Starting up");
//                        break;
//                    } catch (Exception ex) {
//                        Log.wtf(TAG, ex.toString());
//                    }
//                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
        }


        @Override
        protected void onPreExecute() {
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
