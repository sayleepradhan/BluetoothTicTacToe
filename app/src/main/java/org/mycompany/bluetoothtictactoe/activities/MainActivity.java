package org.mycompany.bluetoothtictactoe.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import org.mycompany.bluetoothtictactoe.R;
import org.mycompany.bluetoothtictactoe.logger.Log;
import org.mycompany.bluetoothtictactoe.logger.LogFragment;
import org.mycompany.bluetoothtictactoe.logger.LogWrapper;
import org.mycompany.bluetoothtictactoe.logger.MessageOnlyLogFilter;

/**
 * Created by Malika Pahva (mxp134930) on 4/18/2015.
 * Course: CS6301.001
 *
 * This class creates the fragments for main screen.
 */
public class MainActivity extends SampleActivityBase {

    public static final String TAG = "MainActivity";

    /**
     * This method create new fragments for main screen.
     *
     * Author: Malika Pahva (mxp134930)
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            GameFragment fragment = new GameFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

    /**
     * This method sets the menu on action bar.
     *
     * @param
     *
     * @return boolean
     */

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }*/

    /**
     * This method set up the log data via fragment in TextView.
     *
     * Author: Malika Pahva (mxp134930)
     *
     */
    @Override
    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());

        Log.info(TAG, "Ready");
    }
}
