package com.foodfair.ui.hamburger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SettingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Copyright text, I guess");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
