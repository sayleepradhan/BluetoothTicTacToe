package org.mycompany.bluetoothtictactoe.logger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

/**
 * Created by Saylee Pradhan (sap140530)  on 4/19/2015.
 * Course: CS6301.001
 */
public class LogFragment extends Fragment {

    private LogView logView;
    private ScrollView scrollView;

    /**
     * This is a default constructor.
     *
     * Author: Saylee Pradhan (sap140530)
     */
    public LogFragment() {}

    /**
     * This method sets the different log views.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @return View scrollView
     */
    public View inflateViews() {
        scrollView = new ScrollView(getActivity());
        ViewGroup.LayoutParams scrollParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        scrollView.setLayoutParams(scrollParams);

        logView = new LogView(getActivity());
        ViewGroup.LayoutParams logParams = new ViewGroup.LayoutParams(scrollParams);
        logParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        logView.setLayoutParams(logParams);
        logView.setClickable(true);
        logView.setFocusable(true);
        logView.setTypeface(Typeface.MONOSPACE);

        int paddingDips = 16;
        double scale = getResources().getDisplayMetrics().density;
        int paddingPixels = (int) ((paddingDips * (scale)) + .5);
        logView.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
        logView.setCompoundDrawablePadding(paddingPixels);

        logView.setGravity(Gravity.BOTTOM);
        logView.setTextAppearance(getActivity(), android.R.style.TextAppearance_Holo_Medium);

        scrollView.addView(logView);
        return scrollView;
    }

    /**
     * This method sets the different views for log before,
     * after or on text change.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @param inflater
     *
     * @param container
     *
     * @param savedInstanceState
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflateViews();

        logView.addTextChangedListener(new TextWatcher() {
            /**
             * This method sets the log view before the text changes.
             *
             * Author: Saylee Pradhan (sap140530)
             *
             * @param charSequence
             *
             * @param start
             *
             * @param after
             *
             * @param count
             */
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            /**
             * This method sets the log view on the text change event.
             *
             * Author: Saylee Pradhan (sap140530)
             *
             * @param charSequence
             *
             * @param start
             *
             * @param before
             *
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            /**
             * This method sets the log view after the text changes.
             *
             * Author: Saylee Pradhan (sap140530)
             *
             * @param editable
             */
            @Override
            public void afterTextChanged(Editable editable) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        return result;
    }

    /**
     * This method returns the log view where log displays.
     *
     * Author: Saylee Pradhan (sap140530)
     *
     * @return  logView
     *
     */
    public LogView getLogView() {
        return logView;
    }
}