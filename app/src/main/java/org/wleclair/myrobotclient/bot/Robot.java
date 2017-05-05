package org.wleclair.myrobotclient.bot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import org.steelsquid.ssc32.SSC32Controller;
import org.steelsquid.ssc32.SSC32Exception;
import org.steelsquid.ssc32.ServoGroup;
import org.wleclair.myrobotclient.comm.ConnectedClient;
import org.wleclair.myrobotclient.log.Log;
import org.wleclair.myrobotclient.log.LogView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by wleclair on 4/7/16.
 */
public class Robot extends Thread {
    ConnectedClient client;
    String TAG="Robot";
    BluetoothDevice hexabot=null;
    BluetoothAdapter adapter;
    SSC32Controller controller=null;
    LogView logger;
    private static final String MOVE ="M";
    private static final String MOVE_SPEED = "S";
    private static final String MOVE_TIME = "T";
    private static final String GROUP_MOVE = "G";
    private static final String GROUP_MOVE_SPEED = "H";
    private static final String GROUP_MOVE_ALL = "I";
    private static final String POSITION_OFFSET = "P";
    private static final String QUERY_POSTION = "Q";
    private static final String SEND_LOG="L";
    private static final String SET_OFFSET="P";

    public Handler robotHandler;

    public Robot(ConnectedClient me) throws IOException {
        client=me;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            throw new IOException("Cannot connect to Bluetooth");
        }

    }

    public void setHexabot(BluetoothDevice hexa,BluetoothAdapter adapt){
        hexabot=hexa;
        adapter = adapt;
    }

    public void connectToBot() {
        int retry = 0;
        while(hexabot==null) {
            try {
                adapter.startDiscovery();
                Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("HMSoft")) {
                        hexabot = device;
                    }
                }
                if (hexabot == null) {
                    Bundle buns = new Bundle();
                    buns.putString("DidIt", "Error 1: Unable to contact hexabot 'HMSoft, is it on");
                    Message newMessage = new Message();
                    newMessage.setData(buns);
                    client.handleMessage(newMessage);
                    Log.i(TAG, "Error: Unable to contact hexabot 'HMSoft, is it on");
                    Thread.sleep(5000);
                }
            } catch(InterruptedException ex){
                Bundle buns = new Bundle();
                buns.putString("DidIt", "Error 2: Unable to contact hexabot 'HMSoft, is it on");
                Message newMessage = new Message();
                newMessage.setData(buns);
                client.handleMessage(newMessage);
                Log.i(TAG, "Error: Unable to contact hexabot 'HMSoft, is it on");
                if (retry > 200) {
                    System.exit(100);
                }
            }
        }
    }

    public void setLogView(LogView logv){
        logger = logv;
    }

    public void run(){
            try {
                Looper.prepare();
                    robotHandler = new Handler() {
                    public void handleMessage(Message msg) {
                            hMessage(msg);
                    }
                };
                connectToBot();
                Log.d(TAG, "Robot sleeps");
//                client.send("Start");
                controller = new SSC32Controller(hexabot, adapter);
                Looper.loop();
            } catch(Exception ex){
                Bundle buns = new Bundle();
                buns.putString("DidIt","Error 0: Life is meaningless at this point" + ex.getMessage());
                Message newMessage = new Message();
                newMessage.setData(buns);
                client.handleMessage(newMessage);
                Log.wtf(TAG,ex.getMessage());
            }
    }

    public boolean hMessage(Message msg) {
        Bundle buns=new Bundle();
        Message newMessage = new Message();
        Bundle bans = msg.getData();
        String res = bans.getString("DoIt");
        Log.d(TAG,"message received from server: " + res);
        String[] data = res.split(",");
        boolean noError=true;
        int retry=0;
        while (noError) {
            try {
                processMessage(data);
                noError=false;
            } catch (IOException | NullPointerException sex) {
                Log.e(TAG, "Bot Disconnected:  " + sex.getMessage());
                buns.putString(ConnectedClient.STRING_RESPONSE, "Error 1: Trying to reconnect " + sex.getMessage());
                newMessage.setData(buns);
                client.handleMessage(newMessage);
                // reconnect to bot
                connectToBot();
            } catch (InterruptedException ex) {
                Log.e(TAG, "Error 2:  Bot Interrupted while sending command :  " + ex.getMessage());
                buns.putString(ConnectedClient.STRING_RESPONSE, "Error 2:  Bot Interrupted while sending command " + ex.getMessage());
            } catch (Exception sex) {
                Log.e(TAG, "Problem sending information to bot:  " + sex.getMessage());
                buns.putString(ConnectedClient.STRING_RESPONSE, "Error 0:  Unknown exception while sending command " + sex.getMessage());
            }
        }
        return true;
    }


    private boolean processMessage(String[] data) throws Exception {
        Bundle buns=new Bundle();
        Message newMessage = new Message();
        String strut = "Received the following message";
            switch (data[0]) {
                case MOVE:
                    controller.move(Integer.parseInt(data[1]), Integer.parseInt(data[2]));
                    buns.putString(ConnectedClient.STRING_RESPONSE, "OK");
                    break;
                case MOVE_SPEED:
                    controller.moveSpeed(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                    buns.putString(ConnectedClient.STRING_RESPONSE, "OK");
                    break;
                case MOVE_TIME:
                    controller.moveTime(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
                    buns.putString(ConnectedClient.STRING_RESPONSE, "OK");
                    break;
                case GROUP_MOVE:
                    controller.move(createServoGroup(data));
                    buns.putString(ConnectedClient.STRING_RESPONSE, "OK");
                    break;
                case POSITION_OFFSET: {
                    int k = 0, l = Integer.parseInt(data[1]);
                    int[] servos = new int[l];
                    int[] offsets = new int[l];
                    for (int j = 2; j < l; j++) {
                        servos[k] = Integer.parseInt(data[j]);
                        offsets[k++] = Integer.parseInt(data[j + l]);
                    }
                    controller.setSoftwarePositionOffset(servos, offsets);
                    buns.putString(ConnectedClient.STRING_RESPONSE, "OK");
                }
                break;
                case QUERY_POSTION: {
                    int k = 0, l = Integer.parseInt(data[1]);
                    int[] servos = new int[l];
                    for (int j = 2; j < l; j++) {
                        servos[k++] = Integer.parseInt(data[j]);
                    }
                    ServoGroup gp = new ServoGroup(servos);
                    controller.queryPositions(gp);
                    buns.putString(ConnectedClient.STRING_RESPONSE, gp.getPositionString());
                }
                break;
                case SEND_LOG:
                    ArrayList<String> result = logger.getBuffer();
                    Iterator iter = result.iterator();
                    StringBuilder mavis = new StringBuilder();
                    while (iter.hasNext())
                        mavis.append(iter.next() + ";");
                    buns.putString(ConnectedClient.STRING_RESPONSE, mavis.toString());
            }
            int i = 1;
            while (!controller.isLastMoveComplete()) {
                if (i > 30)
                    break;
                this.sleep(100);
                i++;
            }
        if (buns != null) {
            newMessage.setData(buns);
            client.handleMessage(newMessage);
        }
        //does nothing for test respond
        return true;
    }

    private ServoGroup createServoGroup(String[] data) {
        int i = 2, j = Integer.parseInt(data[1]);
        int k = 0;
        int[] position = new int[j];
        int[] servo = new int[j];
        int[] times = new int[j];
        int[] speeds = new int[j];
        ServoGroup sg = new ServoGroup();
        int defaultSpeed=100,defaultTime=500;

        while (k < j) {
            servo[k] = Integer.parseInt(data[i]);
            ++k;
            ++i;
        }
        k = 0;
        while (k < j) {
            position[k] = Integer.parseInt(data[i]);
            ++k;
            ++i;
        }
        k = 0;
        if (data[i].compareTo("N") != 0) {
            while (k < j) {
                speeds[k] = Integer.parseInt(data[i]);
                ++k;
                ++i;
            }
            k=0;
            while (k < j) {
                times[k] = Integer.parseInt(data[i]);
                ++k;
                ++i;
            }
            sg.setGroup(servo,position,times,speeds);
        } else {
            sg.setGroup(servo,position);
        }
        return sg;
    }
}
