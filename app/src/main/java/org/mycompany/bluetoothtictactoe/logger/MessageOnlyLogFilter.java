package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee on 4/19/2015.
 */
public class MessageOnlyLogFilter implements LogNode {

    LogNode logNode;



    public MessageOnlyLogFilter() {
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        if (logNode != null) {
            getNext().println(Log.NONE, null, msg, null);
        }
    }

    /**
     * Returns the next LogNode in the chain.
     */
    public LogNode getNext() {
        return logNode;
    }

    /**
     * Sets the LogNode data will be sent to..
     */
    public void setNext(LogNode node) {
        logNode = node;
    }

}

