package org.steelsquid.ssc32;


/**
 * If error on comunication with the SSC32 servo controller.<br>
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
public final class SSC32Exception extends Exception {

    /** serialVersionUID. */
    private static final long serialVersionUID = 6666666661L;


    /**
     * Constructor.
     */
    public SSC32Exception() {
        super();
    }


    /**
     * Constructor.
     *
     * @param message Message
     */
    public SSC32Exception(final String message) {
        super(message);
    }


    /**
     * Constructor.
     *
     * @param cause Cause of exception
     */
    public SSC32Exception(final Throwable cause) {
        super(cause);
    }


    /**
     * Constructor.
     *
     * @param message Message
     * @param cause Cause of exception
     */
    public SSC32Exception(final String message, final Throwable cause) {
        super(message, cause);
    }
}
