package org.steelsquid.ssc32;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.List;

import org.bluetooth.ConnectionManager;
import org.wleclair.myrobotclient.log.Log;


/**
 * Communication to SSC-32 servo controller<br>
 * - Handles 32 servos.<br>
 * - Get available com-ports.<br>
 * - Move a servo or group of servos to position as fast as possible.<br>
 * - Move a servo or group of servos to position by selecting speed.<br>
 * - Move a servo or group of servos to position in a certain time.<br>
 * - Get position of a servo or group of servos.<br>
 * - Wait for last move to complete.<br>
 * <br>
 * See Readme.txt and org.steelsquid.ssc32.ExecTest for example of usage.<br>
 * <br>
 * Organization: Steelsquid<br>
 * Project: SSC32Controller<br>
 * Licensing: LGPL (GNU Lesser General Public License)<br>
 * Author: Andreas Nilsson<br>
 * Contact: steelsquid@gmail.com<br>
 * Homepage: http://steelsquid.org/SSC32Controller<br>
 * <br>
 * Revision history: <br>
 * - 2010-09-24  Created <br>
 */
public final class SSC32Controller { //NOPMD

    /**
     * sleep interval between poll query to ssc-32 (ms).
     */
    private static final int SLEEP = 10;

    /**
     * The baud rate.
     */
    private static final int BAUD_RATE = 115200;

    /** The databits. */
//    private static final int DATABITS = SerialPort.DATABITS_8;

    /** The stopbits. */
//    private static final int STOPBITS = SerialPort.STOPBITS_1;

    /** The baud rate. */
//    private static final int PARITY = SerialPort.PARITY_NONE;

    /**
     * The comport connection.
     */
//    private final transient SerialPort serialPort;

    private InputStream in;

    private BluetoothDevice device;

    final private ConnectionManager connectionM;

    private BluetoothAdapter bluetoothAdapter;


    public SSC32Controller(final BluetoothDevice _device, BluetoothAdapter adapter) throws IOException {
        try {
            device = _device;
            connectionM = new ConnectionManager();
            connectionM.connect(_device, adapter);
        } catch (Exception e) {
            this.disconnect();
            throw e;
        }
    }


    /**
     * Disconnect from the port.
     */
    public void disconnect() {
        if (connectionM.getState() != connectionM.STATE_NONE)
            connectionM.stop();
    }


    /**
     * Return a list with all free com ports
     *
     * @return Name of com-port
     */
    public List<String> getBluetoothList() {
        final List<String> list = new ArrayList<String>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            //           Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            return null;
        }


        return list;
    }


    /**
     * Move a servo to position as fast as possible.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo The servo to move (0 to 31)
     */
    public void move(final int servo, final int position) throws IOException {
        synchronized (connectionM) {
            connectionM.write('#');
            if (servo < 10) {
                connectionM.write(Character.forDigit(servo, 10));
            } else {
                connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                connectionM.write(Character.forDigit(servo % 10, 10));
            }
            connectionM.write('P');
            if (position < 10) {
                connectionM.write(Character.forDigit(position, 10));
            } else if (position < 100) {
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            } else if (position < 1000) {
                connectionM.write(Character.forDigit(position / 100 % 10, 10));
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            } else {
                connectionM.write(Character.forDigit(position / 1000 % 10, 10));
                connectionM.write(Character.forDigit(position / 100 % 10, 10));
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }


    /**
     * Move a servo to position in a certain time.<br>
     * //     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo    The servo to move (0 to 31)
     * @param position The position (500 to 2500)
     * @param time     Time in mS for entire move (max 65535)
     * @throws IOException On error
     */
    public void moveTime(final int servo, final int position, final int time) throws IOException { //NOPMD
        synchronized (connectionM) {
            connectionM.write('#');
            if (servo < 10) {
                connectionM.write(Character.forDigit(servo, 10));
            } else {
                connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                connectionM.write(Character.forDigit(servo % 10, 10));
            }
            connectionM.write('P');
            if (position < 10) {
                connectionM.write(Character.forDigit(position, 10));
            } else if (position < 100) {
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            } else if (position < 1000) {
                connectionM.write(Character.forDigit(position / 100 % 10, 10));
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            } else {
                connectionM.write(Character.forDigit(position / 1000 % 10, 10));
                connectionM.write(Character.forDigit(position / 100 % 10, 10));
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            }
            connectionM.write('T');
            if (time < 10) {
                connectionM.write(Character.forDigit(time, 10));
            } else if (time < 100) {
                connectionM.write(Character.forDigit(time / 10 % 10, 10));
                connectionM.write(Character.forDigit(time % 10, 10));
            } else if (time < 1000) {
                connectionM.write(Character.forDigit(time / 100 % 10, 10));
                connectionM.write(Character.forDigit(time / 10 % 10, 10));
                connectionM.write(Character.forDigit(time % 10, 10));
            } else if (time < 10000) {
                connectionM.write(Character.forDigit(time / 1000 % 10, 10));
                connectionM.write(Character.forDigit(time / 100 % 10, 10));
                connectionM.write(Character.forDigit(time / 10 % 10, 10));
                connectionM.write(Character.forDigit(time % 10, 10));
            } else {
                connectionM.write(Character.forDigit(time / 10000 % 10, 10));
                connectionM.write(Character.forDigit(time / 1000 % 10, 10));
                connectionM.write(Character.forDigit(time / 100 % 10, 10));
                connectionM.write(Character.forDigit(time / 10 % 10, 10));
                connectionM.write(Character.forDigit(time % 10, 10));
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }


    /**
     * Move a servo to position in a certain speed.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo    The servo to move (0 to 31)
     * @param position The position (500 to 2500)
     * @param speed    Speed in uS per second until it reaches its commanded destination.<br>
     *                 (100uS per second means the servo will take 10 seconds to move 90 degrees)
     * @throws IOException On error
     */
    public void moveSpeed(final int servo, final int position, final int speed) throws IOException { //NOPMD
        synchronized (connectionM) {
            connectionM.write('#');
            if (servo < 10) {
                connectionM.write(Character.forDigit(servo, 10));
            } else {
                connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                connectionM.write(Character.forDigit(servo % 10, 10));
            }
            connectionM.write('P');
            if (position < 10) {
                connectionM.write(Character.forDigit(position, 10));
            } else if (position < 100) {
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            } else if (position < 1000) {
                connectionM.write(Character.forDigit(position / 100 % 10, 10));
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            } else {
                connectionM.write(Character.forDigit(position / 1000 % 10, 10));
                connectionM.write(Character.forDigit(position / 100 % 10, 10));
                connectionM.write(Character.forDigit(position / 10 % 10, 10));
                connectionM.write(Character.forDigit(position % 10, 10));
            }
            connectionM.write('S');
            if (speed < 10) {
                connectionM.write(Character.forDigit(speed, 10));
            } else if (speed < 100) {
                connectionM.write(Character.forDigit(speed / 10 % 10, 10));
                connectionM.write(Character.forDigit(speed % 10, 10));
            } else if (speed < 1000) {
                connectionM.write(Character.forDigit(speed / 100 % 10, 10));
                connectionM.write(Character.forDigit(speed / 10 % 10, 10));
                connectionM.write(Character.forDigit(speed % 10, 10));
            } else if (speed < 10000) {
                connectionM.write(Character.forDigit(speed / 1000 % 10, 10));
                connectionM.write(Character.forDigit(speed / 100 % 10, 10));
                connectionM.write(Character.forDigit(speed / 10 % 10, 10));
                connectionM.write(Character.forDigit(speed % 10, 10));
            } else {
                connectionM.write(Character.forDigit(speed / 10000 % 10, 10));
                connectionM.write(Character.forDigit(speed / 1000 % 10, 10));
                connectionM.write(Character.forDigit(speed / 100 % 10, 10));
                connectionM.write(Character.forDigit(speed / 10 % 10, 10));
                connectionM.write(Character.forDigit(speed % 10, 10));
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }


    /**
     * Get position of a server.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo Get position from this servo (0 to 31)
     * @return The position (500 to 2500)
     * @throws IOException On error
     */
    public int getPosition(final int servo) throws IOException {
        synchronized (connectionM) {
            connectionM.write('Q');
            connectionM.write('P');
            if (servo < 10) {
                connectionM.write(Character.forDigit(servo, 10));
            } else {
                connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                connectionM.write(Character.forDigit(servo % 10, 10));
            }
            connectionM.write('\r');
            connectionM.flush();

            return in.read() * 10;
        }
    }

    private void writeValue(final int value) throws IOException {
        if (value < 10) {
            connectionM.write(Character.forDigit(value, 10));
        } else if (value < 100) {
            connectionM.write(Character.forDigit(value / 10 % 10, 10));
            connectionM.write(Character.forDigit(value % 10, 10));
        } else if (value < 1000) {
            connectionM.write(Character.forDigit(value / 100 % 10, 10));
            connectionM.write(Character.forDigit(value / 10 % 10, 10));
            connectionM.write(Character.forDigit(value % 10, 10));
        } else if (value < 10000) {
            connectionM.write(Character.forDigit(value / 1000 % 10, 10));
            connectionM.write(Character.forDigit(value / 100 % 10, 10));
            connectionM.write(Character.forDigit(value / 10 % 10, 10));
            connectionM.write(Character.forDigit(value % 10, 10));
        } else {
            connectionM.write(Character.forDigit(value / 10000 % 10, 10));
            connectionM.write(Character.forDigit(value / 1000 % 10, 10));
            connectionM.write(Character.forDigit(value / 100 % 10, 10));
            connectionM.write(Character.forDigit(value / 10 % 10, 10));
            connectionM.write(Character.forDigit(value % 10, 10));
        }
    }


    /**
     * Sending command to multiple servers at once.<br>
     *
     * @param group Servo group to send command to.
     * @throws IOException On error
     */
    public void move(final ServoGroup group) throws IOException, InterruptedException { //NOPMD
        StringBuilder build = new StringBuilder();
        if (group.useGroupMoveCommand()) {
            synchronized (connectionM) {
                for (int i = 0; i < group.getGroupSize(); i++) { // was 32 but we are not moving all of them but only a sub\
                    final int servo = group.getServos()[i];
                    final int position = group.getPositions()[i];
                    if (position != -1) {
                        connectionM.write('#');
                        build.append('#');
                        writeValue(servo);
                        build.append(servo);
                        connectionM.write('P');
                        build.append('P');
                        writeValue(position);
                        build.append(position);
                    }
                }
            }
            if (group.getSpeeds()[0] != -1) {
                connectionM.write('S');
                build.append('S');
                writeValue(group.getSpeeds()[0]);
                build.append(group.getSpeeds()[0]);
            }
            if (group.getTimes()[0] != -1) { //NOPMD
                connectionM.write('T');
                build.append('T');
                writeValue(group.getTimes()[0]);
                build.append(group.getTimes()[0]);
            }
            Log.d(this.getClass().getName(), " flushing - " + build.toString());
            connectionM.write('\r');

            connectionM.flush();

        } else {
            for (int i = 0; i < 32; i++) {
                final int severoi = group.getServos()[i];
                final int position = group.getPositions()[i];
                final int speed = group.getSpeeds()[i]; //NOPMD
                final int time = group.getTimes()[i];
                if (position != -1) {
                    if (speed != -1) { //NOPMD
                        this.moveSpeed(severoi, position, speed);
                    } else if (time != -1) { //NOPMD
                        this.moveTime(severoi, position, time);
                    } else {
                        this.move(severoi, position);
                    }
                } else
                    break;
                // let move complete
//                    Thread.sleep(1500);
//                    String titi = queryPosition(severoi);
//                    Log.d(this.getClass().getName()," query response in group - " + titi);
            }
        }
 /*           for (int i = 0; i < 32; i++) {

                final int severoi = group.getServos()[i];
                final int position = group.getPositions()[i];
                if (position != -1) {
                    connectionM.write('#');
                    build.append('#');
                    writeValue(severoi);
                    build.append(severoi);
                    connectionM.write('P');
                    build.append('P');
                    writeValue(position);
                    build.append(position);
                    connectionM.write((char)27);
                    connectionM.flush();

                } else
                    break;
*/

        // let move complete
        Thread.sleep(1500);
//                    String titi = queryPosition(severoi);
//                    Log.d(this.getClass().getName()," query response in group - " + titi);
//            }

    }


    /**
     * position of multiple servers at once.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param group Servo group to send command to.
     * @throws IOException On error
     */
    public void queryPositions(final ServoGroup group) throws IOException {
        synchronized (connectionM) {
            //Create the query
            for (int i = 0; i < group.getGroupSize(); i++) {
                final int servo = group.getServos()[i];
//                    if (position == -2) {
                connectionM.write('Q');
                connectionM.write('P');
                if (i < 10) {
                    connectionM.write(Character.forDigit(servo, 10));
                } else {
                    connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                    connectionM.write(Character.forDigit(servo % 10, 10));
                }
//                    }
            }
            connectionM.write('\r');
            connectionM.flush();

            //Read answers
            for (int i = 0; i < group.getGroupSize(); i++) {
                String toto = connectionM.read();
                if (!toto.isEmpty())
                    group.getPositions()[i] = Integer.getInteger(toto) * 10;
            }
        }
    }

    /**
     * position of multiple servers at once.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @throws SSC32Exception On error
     */
    public String queryPosition(final int servo) throws IOException { //NOPMD
        synchronized (connectionM) {
            //Create the query
            connectionM.write('Q');
            connectionM.write('P');
            if (servo < 10) {
                connectionM.write(Character.forDigit(servo, 10));
            } else {
                connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                connectionM.write(Character.forDigit(servo % 10, 10));
            }
            connectionM.write('\r');
            connectionM.flush();
            return connectionM.read();
        }
    }

    /**
     * Check if last move is complete.
     *
     * @return Is last move complete
     * @throws SSC32Exception On error
     */
    public boolean isLastMoveComplete() {
        return true;
 /*       try {
            synchronized (connectionM) {
                connectionM.write('Q');
                connectionM.write('\r');
                connectionM.flush();
                final char response = (char) connectionM.read().charAt(0);
                return response == '.' || response=='[';
            }
        }
        catch (Exception e) {
        }
        return false;
  */
    }


    /**
     * Wait for last move to complete.
     *
     * @throws SSC32Exception On error
     */
    public void waitForLastMoveToComplete() throws IOException {
        boolean lastOk = false;
        while (!lastOk) {
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException ie) { //NOPMD
            }
            synchronized (connectionM) {
                connectionM.write('Q');
                connectionM.write('\r');
                connectionM.flush();
                String response = connectionM.read();
                lastOk = response.charAt(0) == '.';
            }
        }
    }


    /**
     * This command allows the servos centered (1500uS) position to be aligned perfectly. <br>
     * The servo channel will be offset by the amount indicated in offset value.  <br>
     * This represents approximately 15degrees of range. It's important to build the mechanical  <br>
     * assembly as close as possible to the desired centered position before applying the  <br>
     * servo offset. This makes it easy to setup servos that do not have mechanical alignment.  <br>
     * The Position Offset command should be issued only once in your program. When the SSC-32 is  <br>
     * turned off it will forget the Position Offsets.
     *
     * @param servos  Array of servos Ex: 0, 1, 4, 6   (will map to equivalent index in the offsets array)
     * @param offsets All offsets in a array (100 to -100 in uSeconds)
     * @throws IOException On error
     */
    public void setSoftwarePositionOffset(final int[] servos, final int[] offsets) throws IOException {
        synchronized (connectionM) {
            for (int i = 0; i < servos.length; i++) {
                final int servo = servos[i];
                int offset = offsets[i];
                connectionM.write('#');
                if (servo < 10) {
                    connectionM.write(Character.forDigit(servo, 10));
                } else {
                    connectionM.write(Character.forDigit(servo / 10 % 10, 10));
                    connectionM.write(Character.forDigit(servo % 10, 10));
                }
                connectionM.write('P');
                connectionM.write('O');
                if (offset < 0) {
                    connectionM.write('-');
                    offset = offset * -1;
                }
                if (offset < 10) {
                    connectionM.write(Character.forDigit(offset, 10));
                } else if (offset < 100) {
                    connectionM.write(Character.forDigit(offset / 10 % 10, 10));
                    connectionM.write(Character.forDigit(offset % 10, 10));
                } else {
                    connectionM.write(Character.forDigit(offset / 100 % 10, 10));
                    connectionM.write(Character.forDigit(offset / 10 % 10, 10));
                    connectionM.write(Character.forDigit(offset % 10, 10));
                }
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }


    /**
     * The channel will go to the level indicated within 20mS.<br>
     * The outputs can sink or source up to 25mA per pin, but<br>
     * a max of 70mA per bank must be observed.<bt>
     * High (+5v)  Low (0v)
     *
     * @param channelNumber Channel number in decimal, 0-31
     * @param level         Logic level for the channel, true=Hight false=Low
     * @throws IOException On error
     */
    public void setOutputLevel(final int channelNumber, final boolean level) throws IOException {
        synchronized (connectionM) {
            connectionM.write('#');
            if (channelNumber < 10) {
                connectionM.write(Character.forDigit(channelNumber, 10));
            } else {
                connectionM.write(Character.forDigit(channelNumber / 10 % 10, 10));
                connectionM.write(Character.forDigit(channelNumber % 10, 10));
            }

            if (level) {
                connectionM.write('H');
            } else {
                connectionM.write('L');
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }


    /**
     * Set level to mutiple channels.<br>
     * The channel will go to the level indicated within 20mS.<br>
     * The outputs can sink or source up to 25mA per pin, but<br>
     * a max of 70mA per bank must be observed.
     * High (+5v)  Low (0v)
     *
     * @param levels A int array 32 long (-1=do nothing, 0=low, 1=hight)
     * @throws IOException On error
     */
    public void setOutputLevels(final int[] levels) throws IOException {
        synchronized (connectionM) {
            for (int i = 0; i < levels.length; i++) {
                final int level = levels[i];
                if (level == 0) {
                    connectionM.write('#');
                    if (i < 10) {
                        connectionM.write(Character.forDigit(i, 10));
                    } else {
                        connectionM.write(Character.forDigit(i / 10 % 10, 10));
                        connectionM.write(Character.forDigit(i % 10, 10));
                    }
                    connectionM.write('L');
                } else if (level == 1) {
                    connectionM.write('#');
                    if (i < 10) {
                        connectionM.write(Character.forDigit(i, 10));
                    } else {
                        connectionM.write(Character.forDigit(i / 10 % 10, 10));
                        connectionM.write(Character.forDigit(i % 10, 10));
                    }
                    connectionM.write('H');
                }
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }


    /**
     * This command allows 8 bits of binary data to be written at once. <br>
     * All pins of the bank are updated simultaneously. The banks will <br>
     * be updated within 20mS of receiving the carriage return.
     *
     * @param bank  (0 = Pins 0-7, 1 = Pins 8-15, 2 = Pins 16-23, 3 = Pins 24-31)
     * @param value Decimal value to output to the selected bank (0-255), Bit 0 = LSB of bank
     * @throws IOException On error
     */
    public void setOutputByte(final int bank, final int value) throws IOException {
        synchronized (connectionM) {
            connectionM.write('#');
            connectionM.write(Character.forDigit(bank, 10));
            connectionM.write(':');
            if (value < 10) {
                connectionM.write(Character.forDigit(value, 10));
            } else if (value < 100) {
                connectionM.write(Character.forDigit(value / 10 % 10, 10));
                connectionM.write(Character.forDigit(value % 10, 10));
            } else {
                connectionM.write(Character.forDigit(value / 100 % 10, 10));
                connectionM.write(Character.forDigit(value / 10 % 10, 10));
                connectionM.write(Character.forDigit(value % 10, 10));
            }
            connectionM.write('\r');
            connectionM.flush();
        }
    }
}
