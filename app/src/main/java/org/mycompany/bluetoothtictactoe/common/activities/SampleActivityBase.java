package org.mycompany.bluetoothtictactoe.common.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.mycompany.bluetoothtictactoe.common.logger.Log;
import org.mycompany.bluetoothtictactoe.common.logger.LogWrapper;

/**
 * Created by Saylee on 4/19/2015.
 */
public class SampleActivityBase extends FragmentActivity {

    public static final String TAG = "SampleActivityBase";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void onStart() {
        super.onStart();
        initializeLogging();
    }

    /** Set up targets to receive log data */
    public void initializeLogging() {
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        Log.i(TAG, "Ready");
    }
}
