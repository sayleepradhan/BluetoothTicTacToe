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
 * Created by Saylee on 4/19/2015.
 */
public class LogFragment extends Fragment {

    private LogView logView;
    private ScrollView scrollView;

    public LogFragment() {}

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

        // Want to set padding as 16 dips, setPadding takes pixels.  Hooray math!
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflateViews();

        logView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        return result;
    }

    public LogView getLogView() {
        return logView;
    }
}