package org.mycompany.bluetoothtictactoe.logger;

/**
 * Created by Saylee Pradhan (sap140530) on 4/19/2015.
 * Course: CS6301.001
 */
public class Log {
    public static final int NONE = -1;
    public static final int VERBOSE = android.util.Log.VERBOSE;
    public static final int DEBUG = android.util.Log.DEBUG;
    public static final int INFO = android.util.Log.INFO;
    public static final int WARN = android.util.Log.WARN;
    public static final int ERROR = android.util.Log.ERROR;
    public static final int ASSERT = android.util.Log.ASSERT;

    private static LogNode logNode;

    /**
     * This is a getter method which returns the next
     * LogNode in the linked list.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public static LogNode getLogNode() {
        return logNode;
    }

    /**
     * This is a setter method which sets the LogNode data
     * will be sent to.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public static void setLogNode(LogNode node) {
        logNode = node;
    }

    /**
     * This method instructs the LogNode to print the log data provided.
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
     */
    public static void println(int priority, String tag, String msg, Throwable throwable) {
        if (logNode != null) {
            logNode.println(priority, tag, msg, throwable);
        }
    }

    /**
     * This method instructs the LogNode to print the log data provided.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param priority
     *
     * @param tag
     *
     * @param msg
     */
    public static void println(int priority, String tag, String msg) {
        println(priority, tag, msg, null);
    }

    /**
     * This method prints a message at VERBOSE priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     */
    public static void verbose(String tag, String msg, Throwable throwable) {
        println(VERBOSE, tag, msg, throwable);
    }

    /**
     * This method prints a message at VERBOSE priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     */
    public static void verbose(String tag, String msg) {
        verbose(tag, msg, null);
    }


    /**
     * This method prints a message at DEBUG priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     */
    public static void debug(String tag, String msg, Throwable throwable) {
        println(DEBUG, tag, msg, throwable);
    }

    /**
     * This method prints a message at DEBUG priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     */
    public static void debug(String tag, String msg) {
        debug(tag, msg, null);
    }

    /**
     * This method prints a message at INFO priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     */
    public static void info(String tag, String msg, Throwable throwable) {
        println(INFO, tag, msg, throwable);
    }

    /**
     * This method prints a message at INFO priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     */
    public static void info(String tag, String msg) {
        info(tag, msg, null);
    }

    /**
     * This method prints a message at WARN priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     */
    public static void warn(String tag, String msg, Throwable throwable) {
        println(WARN, tag, msg, throwable);
    }

    /**
     * This method prints a message at WARN priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     */
    public static void warn(String tag, String msg) {
        warn(tag, msg, null);
    }

    /**
     * This method prints a message at WARN priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param throwable
     */
    public static void warn(String tag, Throwable throwable) {
        warn(tag, null, throwable);
    }

    /**
     * This message prints a message at ERROR priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     */
    public static void error(String tag, String msg, Throwable throwable) {
        println(ERROR, tag, msg, throwable);
    }

    /**
     * This message prints a message at ERROR priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     */
    public static void error(String tag, String msg) {
        error(tag, msg, null);
    }

    /**
     * This method prints a message at ASSERT priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     *
     * @param throwable
     */
    public static void assertPriority(String tag, String msg, Throwable throwable) {
        println(ASSERT, tag, msg, throwable);
    }

    /**
     * This method prints a message at ASSERT priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param msg
     */
    public static void assertPriority(String tag, String msg) {
        assertPriority(tag, msg, null);
    }

    /**
     * This method prints a message at ASSERT priority.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param tag
     *
     * @param throwable
     */
    public static void assertPriority(String tag, Throwable throwable) {
        assertPriority(tag, null, throwable);
    }
}
