package com.minerva.shareart.ui.auth;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isAuthorized;

    public AuthViewModel() {
        isAuthorized = new MutableLiveData<>();
        isAuthorized.setValue(false);
    }

    public LiveData<Boolean> getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(boolean isAuthorized) {
        Log.d("SA", ""+isAuthorized);
        this.isAuthorized.setValue(isAuthorized);
    }
}