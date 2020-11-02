package com.foodfair.ui.qrscanner;

import android.app.Activity;

public interface OnStateChangeListener {
    public void onAttach(Activity activity);
    public void onStateChange(Object object);
}
