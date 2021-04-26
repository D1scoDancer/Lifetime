package ru.alekssey7227.lifetime.others;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StatsPageViewModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();

    public void setIndex(int index) {
        mIndex.setValue(index);
    }
}