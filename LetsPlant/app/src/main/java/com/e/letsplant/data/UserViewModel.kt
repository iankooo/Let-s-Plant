package com.e.letsplant.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    private val userMutableLiveData = MutableLiveData<User?>()
    fun setUserMutableLiveData(user: User?) {
        userMutableLiveData.value = user
    }

    fun getUserMutableLiveData(): LiveData<User?> {
        return userMutableLiveData
    }
}