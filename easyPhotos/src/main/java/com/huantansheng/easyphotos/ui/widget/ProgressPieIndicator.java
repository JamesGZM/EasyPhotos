package com.huantansheng.easyphotos.ui.widget;

import android.view.LayoutInflater;
import android.view.View;
import com.filippudak.ProgressPieView.ProgressPieView;
import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.view.BigImageView;
import com.huantansheng.easyphotos.R;

import java.util.Locale;

/**
 * Created by Piasy{github.com/Piasy} on 12/11/2016.
 */

public class ProgressPieIndicator implements ProgressIndicator {
    private ProgressPieView mProgressPieView;

    @Override
    public View getView(BigImageView parent) {
        mProgressPieView = (ProgressPieView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_progress_pie_indicator, parent, false);
        return mProgressPieView;
    }

    @Override
    public void onStart() {
        // not interested
    }

    @Override
    public void onProgress(int progress) {
        if (progress < 0 || progress > 100 || mProgressPieView == null) {
            return;
        }
        mProgressPieView.setProgress(progress);
        mProgressPieView.setText(String.format(Locale.getDefault(), "%d%%", progress));
    }

    @Override
    public void onFinish() {
        // not interested
    }
}