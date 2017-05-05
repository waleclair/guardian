package org.wleclair.myrobotclient.comm;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.common.Connected;
import org.wleclair.myrobotclient.log.Log;
import org.wleclair.myrobotclient.log.LogView;

/**
 * Created by wleclair on 4/7/16.
 */

public class ConnectedClient extends Connected implements Handler.Callback {
    private String TAG = "ConnectedClient";
    private Socket socket = null;
    private PrintWriter toServer = null;
    private int counter = 0;
    public static final String STRING_RESPONSE="DidIt";
    public static final String STRING_RESPONSE_LIST="DidIt_LIST";


    public ConnectedClient(String server, int port) {
        setPort(port);
        setHostess(server);
        socket = new Socket();
    }
     public ConnectedClient(String server) {
        setHostess(server);
        socket = new Socket();
    }

    public void connect() throws IOException {
        if (socket.isConnected())
            return;
        setHostEndPoint();
        socket.connect(getHostEndPoint());
        socket.setSoTimeout(2000);
        toServer = new PrintWriter(socket.getOutputStream(), true);
    }


    @Override
    public void send(String payload) throws IOException {
        socket = new Socket();
        connect();
        toServer.println(Integer.toString(counter) + ": " + payload);
        counter++;
        socket.close();
    }

    @Override
    public boolean handleMessage(Message msg) {
        try {
            Bundle buns = msg.getData();
            String toSend = buns.getString(STRING_RESPONSE);
            Log.d(TAG,"sending message " + toSend);
            send(toSend);
        } catch (Exception ex) {
            Log.wtf(TAG,ex.toString());
            return false;
        }
        return true;
    }

}
