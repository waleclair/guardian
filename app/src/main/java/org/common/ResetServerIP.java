package org.common;

import android.net.wifi.WifiInfo;

import org.wleclair.myrobotclient.log.Log;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.TimerTask;

/**
 * Created by wleclair on 9/23/16.
 */
public class ResetServerIP extends TimerTask {
    Connected client=null;
    WifiInfo wifi=null;

    public ResetServerIP(Connected client,WifiInfo wifi){
        this.client = client;
        this.wifi=wifi;
    }

    public void run() {
        String ip = intToIP(wifi.getIpAddress());
        try {
            client.send("ip: "+ ip);
        } catch(Exception ex){
            Log.e("TimerTask exception sending IP",ex.getMessage());
        }
    }

    public String intToIP(int i) {
        return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) +
                "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF));
    }


}
