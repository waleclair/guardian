package org.wleclair.myrobotclient.comm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.wleclair.myrobotclient.bot.Robot;
import org.wleclair.myrobotclient.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wleclair on 4/7/16.
 */
public class ConnectedServer  extends Thread {
    Robot robot;
    private int port = 9089;
    ServerSocket sock=null;
    String TAG="ConnectedServer";
    ConnectedClient client;
    InetAddress addr=null;

    public ConnectedServer(int port,Robot robby,ConnectedClient client) throws IOException {
        this.port = port;
        robot = robby;
        this.client=client;
    }

    public ConnectedServer(Robot robby,ConnectedClient client) {
        this.client=client;
        robot=robby;
    }

    public void setIPAddress(String ip){
        try {
            addr = InetAddress.getByName(ip);
        } catch (Exception ex){
            //do nothing we are dead anyway
        }
    }

    @Override
    public void run() {
            while(true) {
                try {
                    if (sock==null) {
                        sock = new ServerSocket(port, 0, addr);
                    }
                   Socket socket = sock.accept();
                    socket.setSoTimeout(10000);
                    BufferedReader fromClient =
                            new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String line = fromClient.readLine();
                   if (line != null) {
                        Bundle buns = new Bundle();
                        buns.putString("DoIt", line);
                        Message msg = Message.obtain();
                        msg.setData(buns);
                        socket.close();
                        Log.d(TAG," received from server" + line);
                        robot.robotHandler.handleMessage(msg);
                    }
                } catch (Exception ex) {
                    Log.wtf(TAG,"server does not start ", ex);
                    sock=null;
                    try {
                        Thread.sleep(10000);
                    } catch(Exception x){
                        Log.wtf(TAG," error on sleep" + x.getMessage());
                    }
                }
            }
    }
}
