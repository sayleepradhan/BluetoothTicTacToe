package org.mycompany.bluetoothtictactoe.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.mycompany.bluetoothtictactoe.logger.Log;
import org.mycompany.bluetoothtictactoe.logger.LogWrapper;

/**
 * Created by Saylee Pradhan (sap140530)  on 4/19/2015.
 * Course: CS6301.001
 *
 * This class sets up the targets to receive the log data.
 */
public class SampleActivityBase extends FragmentActivity {

    public static final String TAG = "SampleActivityBase";

    /**
     * This method sets up the savedInstanceState.
     *
     * Author: Saylee Pradhan (sap140530)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * This method initialize the targets on startup.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    @Override
    protected  void onStart() {
        super.onStart();
        initializeLogging();
    }

    /**
     * This method set up the targets to receive log data.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     */
    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);
        Log.info(TAG, "Ready");
    }
}
