package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee Pradhan (sap140530) on 4/19/2015.
 * Course: CS6360.001
 *
 * This class sets the log based on different actions
 * and conditions and sets the node to send the data to.
 */
public class LogWrapper implements LogNode  {
    private LogNode logNode;

    /**
     * This is a setter method to set the next LogNode
     * on which data has to be send .
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param node
     */
    public void setNext(LogNode node) {
        logNode = node;
    }

    /**
     * This method sets log based on different conditions.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param priority
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     *
     */
    @Override
    public void println(int priority, String tag, String msg, Throwable throwable) {
        String useMsg = msg;

        if (useMsg == null) {
            useMsg = "";
        }

        if (throwable != null) {
            msg += "\n" + android.util.Log.getStackTraceString(throwable);
        }

        android.util.Log.println(priority, tag, useMsg);

        if (logNode != null) {
            logNode.println(priority, tag, msg, throwable);
        }
    }
}
