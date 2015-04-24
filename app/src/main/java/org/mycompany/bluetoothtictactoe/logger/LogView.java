package org.mycompany.bluetoothtictactoe.logger;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Saylee Pradhan (sap140530) on 4/19/2015.
 * Course: CS6360.001
 *
 * This class prints the log data returned by the LogNode.
 */
public class LogView extends TextView implements LogNode {

    /**
     * This is a constructor to set the context.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param context
     */
    public LogView(Context context) {
        super(context);
    }

    /**
     * This is a constructor to set the context and attributes.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param context
     *
     * @param attributeSet
     */
    public LogView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * This is a constructor to set the context, attributes and
     * style.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param context
     *
     * @param attributeSet
     *
     * @param defStyle
     */
    public LogView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
    }

    /**
     * This method displays the data to view.
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
    @Override
    public void println(int priority, String tag, String msg, Throwable throwable) {
        String priorityStr = null;

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

        String exceptionStr = null;
        if (throwable != null) {
            exceptionStr = android.util.Log.getStackTraceString(throwable);
        }

        final StringBuilder outputBuilder = new StringBuilder();

        String delimiter = "\t";
        appendIfNotNull(outputBuilder, priorityStr, delimiter);
        appendIfNotNull(outputBuilder, tag, delimiter);
        appendIfNotNull(outputBuilder, msg, delimiter);
        appendIfNotNull(outputBuilder, exceptionStr, delimiter);

        ((Activity) getContext()).runOnUiThread( (new Thread(new Runnable() {
            @Override
            public void run() {
                appendToLog(outputBuilder.toString());
            }
        })));

        if (logNode != null) {
            logNode.println(priority, tag, msg, throwable);
        }
    }



    /** This method appends the string to StringBuilder if
     *  string is not null.
     *
     *  Author: Saylee Pradhan (sap140530)
     *
     * @param source
     *          containing the text to append to.
     * @param stringToAdd
     *          it is a String to append
     * @param delimiter
     *
     *          separator to separate the source and appended string
     * @return StringBuilder source
     */
    private StringBuilder appendIfNotNull(StringBuilder source, String stringToAdd, String delimiter) {
        if (stringToAdd != null) {
            if (stringToAdd.length() == 0) {
                delimiter = "";
            }

            return source.append(stringToAdd).append(delimiter);
        }
        return source;
    }

    LogNode logNode;

    public void appendToLog(String s) {
        append("\n" + s);
    }
}
