package org.common;

import org.wleclair.myrobotclient.log.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by wleclair on 9/23/16.
 */
public class Connected {
    private String ip=null;
    private InetSocketAddress hostEndPoint;
    private int port = 9089;
    private String hostess = "localhost";
    private boolean connectionEstablished=false;

    public void setIPAddress(String ip)
        {
            this.ip=ip;
        }
    public String getIPAddress(){
        return ip;
    }

    public void send(String t) throws IOException {
        //must be implemented by subclass
    }


        public void setIp(String ip) {
            this.ip = ip;
        }public void setHostEndPoint(InetSocketAddress hostEndPoint) {
            this.hostEndPoint = hostEndPoint;
        }public void setConnectionEstablished(boolean connectionEstablished) {
            this.connectionEstablished = connectionEstablished;
        }public String getIp() {
            return ip;
        }public int getPort() {
            return port;
        }public String getHostess() {
            return hostess;
        }public boolean isConnectionEstablished() {
        return connectionEstablished;
    }
    public void testConnection(){
    }

    public void setPort(int port){
        this.port=port;
    }

    public void setHostess(String host){
        hostess=host;
    }

    public void setHostEndPoint(){
        if (hostEndPoint != null) return;
        try {
            InetAddress host = InetAddress.getByName(hostess);
            hostEndPoint = new InetSocketAddress(host, port);
        } catch (Exception ex){
            Log.e("Connected",ex.getMessage());
        }
    }

    public InetSocketAddress getHostEndPoint(){
        return hostEndPoint;
    }

}
