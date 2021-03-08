package com.e.letsplant.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    public void setUserMutableLiveData(User user){
        userMutableLiveData.setValue(user);
    }
    public LiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }
}
