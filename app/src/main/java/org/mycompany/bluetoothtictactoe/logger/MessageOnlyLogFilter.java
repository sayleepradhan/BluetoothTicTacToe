package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee Pradhan (sap140530) on 4/19/2015.
 * Course: CS6360.001
 */
public class MessageOnlyLogFilter implements LogNode {

    LogNode logNode;


    /**
     * This is a default constructor.
     *
     * Author: Saylee Pradhan (sap140530)
     */
    public MessageOnlyLogFilter() {
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
        if (logNode != null) {
            getNext().println(Log.NONE, null, msg, null);
        }
    }

    /**
     * This is a getter method which returns the next
     * LogNode in the chain.
     *
     * Author: Saylee Pradhan (sap140530)
     */
    public LogNode getNext() {
        return logNode;
    }

    /**
     * This is a setter method which sets the
     * LogNode data will be sent to.
     *
     * Author: Saylee Pradhan (sap140530)
     */
    public void setNext(LogNode node) {
        logNode = node;
    }

}

