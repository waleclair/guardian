package org.steelsquid.ssc32;


/**
 * This is a class representing a servo group.<br>
 * Use this to send command to multiple servos.<br>
 * <br>
 * Organization: Steelsquid<br>
 * Project: SSC32Controller<br>
 * Licensing: LGPL (GNU Lesser General Public License)<br>
 * Author: Andreas Nilsson<br>
 * Contact: steelsquid@gmail.com<br>
 * Homepage: http://steelsquid.org/SSC32Controller<br>
 * <br>
 * Revision history: <br>
 *  - 2010-01-02  Start to use revision history <br>
 */
public final class ServoGroup {

    /** The positions for all servos (500 to 2500). */
    private final transient int[] servos = new int[32];

    /** The positions for all servos (500 to 2500). */
    private final transient int[] positions = new int[32];

    /** The speed for all servos. */
    private final transient int[] speeds = new int[32];

    /** The time for all servos. Time in mS for entire move (max 65535). */
    private transient int[] times = new int[32];

    /** useGroupMoveCommand. */
    private transient boolean useGroupMoveCommand; //NOPMD

    int groupSize;

    public ServoGroup(){
        useGroupMoveCommand = true;
        for(int i=0;i<servos.length;i++){
            servos[i]=-1;
            positions[i]=-1;
            speeds[i]=-1;
            times[i]=-1;
        }

    }

    public ServoGroup(int[] servo){
        int i;
        for(i=0;i<servo.length;i++){
            servos[i]=servo[i];
        }
        groupSize=i;
    }

    public String getPositionString(){
        String tp="P";
        for(int i=0;i<positions.length;i++){
            tp = ","+positions[i];
        }
        return tp;
    }
    public void setGroup(int[] servo,int[] pos,int[] tim,int[] spd){
        int i;
        for(i=0;i<servo.length;i++){
            servos[i]=servo[i];
            positions[i]=pos[i];
            times[i]=tim[i];
            speeds[i]=spd[i];
        }
        groupSize=i;
        useGroupMoveCommand=true;
    }

    public void setGroup(int[] servo,int[] pos){
        int i;
        for(i=0;i<servo.length;i++){
            servos[i]=servo[i];
            positions[i]=pos[i];
            times[i]=-1;
            speeds[i]=-1;
        }
        groupSize=i;
    }
    public int getGroupSize(){
        return groupSize;
    }

    /**
     * Create the servo group.
     *
     * @param useGroupMoveCommandd true = Send move to mutiple servos in one command (All servis will start and stop moving at the same time.)<br>
     *                                    If one servo has farther to travel than another then it will move faster.<br>
     *                            false = Sending move to servers in different commands (Server can move in own speed or time).<br>
     */
    public void ServoGroup(final boolean useGroupMoveCommandd) { //NOPMD
        reset(useGroupMoveCommandd);
    }


    /**
     * Reset all the values in this servo group.
     *
     * @param useGroupMoveCommandd true = Send move to mutiple servos in one command (All servis will start and stop moving at the same time.)<br>
     *                                    If one servo has farther to travel than another then it will move faster.<br>
     *                            false = Sending move to servers in different commands (Server can move in own speed or time).<br>
     */
    public void reset(final boolean useGroupMoveCommandd) { //NOPMD
        for (int i = 0; i < 32; i++) {
            positions[i] = -1;
            speeds[i] = -1;
            times[i] = -1;
        }
        useGroupMoveCommand = useGroupMoveCommandd;
    }


    /**
     * Set the move of servo to position as fast as possible.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo The servo to move (0 to 31)
     * @param position The position (500 to 2500)
     */
    public void setMove(final int servo, final int position) {
        positions[servo] = position;
        speeds[servo] = -1;
        times[servo] = -1;
    }


    /**
     * Set the move of servo to position in a certain time.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo The servo to move (0 to 31)
     * @param position The position (500 to 2500)
     * @param time Time in mS for entire move (max 65535)
     */
    public void setMoveTime(final int servo, final int position, final int time) {
        positions[servo] = position;
        speeds[servo] = -1;
        times[servo] = time;
    }


    /**
     * Set the move of servo to position in a certain speed.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo The servo to move (0 to 31)
     * @param position The position (500 to 2500)
     * @param speed Speed in uS per second until it reaches its commanded destination.<br>
     *        (100uS per second means the servo will take 10 seconds to move 90 degrees)
     */
    public void setMoveSpeed(final int servo, final int position, final int speed) {
        positions[servo] = position;
        speeds[servo] = speed;
        times[servo] = -1;
    }


    /**
     * Set the servos you want to get position from.<br>
     * Then use getPosition(...) after query executed.<br>
     * The position can vary, can be 400 to 2400 (try the values of your servo)
     *
     * @param servo  Get position from this servo (0 to 31)
     */
    public void queryPosition(final int servo) {
        positions[servo] = -2; //-2 = Get position from this servo
    }


    /**
     * After queryPosition(...) use this to get the position.
     *
     * @param servo The servo (0 to 31)
     * @return The position (500 to 2500) (-1 or -2 = no answer)
     */
    public int getPosition(final int servo) {
        return positions[servo];
    }


    /**
     * Get the position array.
     *
     * @return Array containing the positions.
     */
    protected int[] getPositions() {
        return positions; //NOPMD
    }


    /**
     * Get the servos array.
     *
     * @return Array containing the servos.
     */
    protected int[] getServos() {
        return servos; //NOPMD
    }

    /**
     * Get the speeds array.
     *
     * @return Array containing the speeds.
     */
    protected int[] getSpeeds() {
        return speeds; //NOPMD
    }


    /**
     * Get the times array.
     *
     * @return Array containing the times.
     */
    protected int[] getTimes() {
        return times; //NOPMD
    }


    /**
     * true = Send move to mutiple servos in one command (All servis will start and stop moving at the same time.)<br>
     *        If one servo has farther to travel than another then it will move faster.<br>
     * false = Sending move to servos in different commands (Servos can move in own speed or time).<br>
     *
     * @return true/false.
     */
    protected boolean useGroupMoveCommand() {
        return useGroupMoveCommand;
    }
}
