package org.wleclair.myrobotclient.log;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Environment;
import android.util.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.wleclair.myrobotclient.R;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import android.util.Log;

/** Simple TextView which is used to output log data received through the LogNode interface.
 */
public class LogView implements LogNode {

    String logs = "Logging has started";
    TextView texty=null;
    Activity context;
    private ArrayAdapter<String> adapter;
    ArrayList list;
    FileOutputStream outputStream;
    ArrayList<String> buffer = new ArrayList<String>();

    public ArrayList<String> getBuffer(){
        return buffer;
    }

    public void resetBuffer(){
        buffer = new ArrayList<String>();
    }

    public LogView(Activity context, TextView txt) {
        this.context = context;
        this.texty = txt;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            try {
                outputStream = context.openFileOutput("robot.log", Context.MODE_PRIVATE);
            } catch (Exception ex) {
                outputStream = null;
            }
        } else
            outputStream =null;
    }

  //  public void onCreate(Bundle savedInstance){
  //      super.onCreate(savedInstance);
  //  }


    /**
     * Formats the log data and prints it out to the LogView.
     * @param priority Log level of the data being logged.  Verbose, Error, etc.
     * @param tag Tag for for the log data.  Can be used to organize log statements.
     * @param msg The actual message to be logged. The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     *           to extract and print useful information.
     */
    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {


        String priorityStr = null;
        GregorianCalendar joy = new GregorianCalendar();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        // For the purposes of this View, we want to print the priority as readable text.
        switch(priority) {
            case android.util.Log.VERBOSE:
                priorityStr = "VERBOSE";
                break;
            case android.util.Log.DEBUG:
                priorityStr = "DEBUG";
                break;
            case android.util.Log.INFO:
                priorityStr = "INFO";
                break;
            case android.util.Log.WARN:
                priorityStr = "WARN";
                break;
            case android.util.Log.ERROR:
                priorityStr = "ERROR";
                break;
            case android.util.Log.ASSERT:
                priorityStr = "ASSERT";
                break;
            default:
                break;
        }

        // Handily, the Log class has a facility for converting a stack trace into a usable string.
        String exceptionStr = null;
        if (tr != null) {
            exceptionStr = android.util.Log.getStackTraceString(tr);
        }

        // Take the priority, tag, message, and exception, and concatenate as necessary
        // into one usable line of text.
        final StringBuilder outputBuilder = new StringBuilder();

        String delimiter = "\t";
        appendIfNotNull(outputBuilder, priorityStr, delimiter);
        appendIfNotNull(outputBuilder, tag, delimiter);
        appendIfNotNull(outputBuilder, msg, delimiter);
        appendIfNotNull(outputBuilder, exceptionStr, delimiter);
        //buffer.add(outputBuilder.toString());
        android.util.Log.i("Info",outputBuilder.toString());
   //     list.add(outputBuilder.toString());
  //      adapter.notifyDataSetChanged();

        // In case this was originally called from an AsyncTask or some other off-UI thread,
        // make sure the update occurs within the UI thread.
        context.runOnUiThread((new Thread(new Runnable() {
            @Override
            public void run() {
                // Display the text we just generated within the LogView.
                appendToLog(outputBuilder.toString());
            }
        })));
        try {
            outputStream.write(outputBuilder.toString().getBytes());
        } catch(Exception ex){
            buffer.size();
            // I know do nothing is bad, but in this case I am hosed anyway
        }
        if(mNext!=null) {
            mNext.println(priority, tag, dateFormat.format(joy.getTime()) + ": " + msg, tr);
        }
    }

    public LogNode getNext() {
        return mNext;
    }

    public void setNext(LogNode node) {
        mNext = node;
    }

    /** Takes a string and adds to it, with a separator, if the bit to be added isn't null. Since
     * the logger takes so many arguments that might be null, this method helps cut out some of the
     * agonizing tedium of writing the same 3 lines over and over.
     * @param source StringBuilder containing the text to append to.
     * @param addStr The String to append
     * @param delimiter The String to separate the source and appended strings. A tab or comma,
     *                  for instance.
     * @return The fully concatenated String as a StringBuilder
     */
    private StringBuilder appendIfNotNull(StringBuilder source, String addStr, String delimiter) {
        if (addStr != null) {
            if (addStr.length() == 0) {
                delimiter = "";
            }

            return source.append(addStr).append(delimiter);
        }
        return source;
    }

    // The next LogNode in the chain.
    LogNode mNext;

    /** Outputs the string as a new line of log data in the LogView. */
    public void appendToLog(String s) {
        texty.append("\n" + s);
    }
}
