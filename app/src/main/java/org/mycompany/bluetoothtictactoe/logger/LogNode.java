package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee Pradhan (sap140530) on 4/19/2015.
 * Course: CS6360.001
 *
 * This is an interface used for setting the logs.
 */
public interface LogNode  {
    /**
     * This method sets log based on different conditions.
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
    public void println(int priority, String tag, String msg, Throwable throwable);
}
