package com.foodfair.ui.hamburger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HistoryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HistoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is history of items fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
