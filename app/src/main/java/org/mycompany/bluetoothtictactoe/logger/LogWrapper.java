package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee on 4/19/2015.
 */
public class LogWrapper implements LogNode  {
    private LogNode logNode;
    public void setNext(LogNode node) {
        logNode = node;
    }
    public void println(int priority, String tag, String msg, Throwable tr) {
        // There actually are log methods that don't take a msg parameter.  For now,
        // if that's the case, just convert null to the empty string and move on.
        String useMsg = msg;
        if (useMsg == null) {
            useMsg = "";
        }
        // If an exeption was provided, convert that exception to a usable string and attach
        // it to the end of the msg method.
        if (tr != null) {
            msg += "\n" + android.util.Log.getStackTraceString(tr);
        }

        // This is functionally identical to Log.x(tag, useMsg);
        // For instance, if priority were Log.VERBOSE, this would be the same as Log.v(tag, useMsg)
        android.util.Log.println(priority, tag, useMsg);

        // If this isn't the last node in the chain, move things along.
        if (logNode != null) {
            logNode.println(priority, tag, msg, tr);
        }
    }
}
