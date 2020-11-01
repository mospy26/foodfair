package com.foodfair.ui.hamburger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ManageFoodViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ManageFoodViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is manage food fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
