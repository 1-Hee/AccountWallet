package com.aiden.accountwallet.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.data.vo.DisplayAccountInfo

class InfoItemViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    ViewModel's Variables
    // * ------------------------------------------------
    val mDisplayAccountInfo: MutableLiveData<DisplayAccountInfo> = MutableLiveData<DisplayAccountInfo>(null)

    fun initInfoItemViewModel() {
        this.mDisplayAccountInfo.postValue(null)
    }

    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------
    fun setDisplayAccountInfo(displayAccountInfo : DisplayAccountInfo){
        this.mDisplayAccountInfo.postValue(displayAccountInfo)
    }



}