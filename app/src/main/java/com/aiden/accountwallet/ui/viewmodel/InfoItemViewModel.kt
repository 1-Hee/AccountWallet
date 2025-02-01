package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.vo.DisplayAccountInfo

class InfoItemViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    ViewModel's Variables
    // * ------------------------------------------------
    val mDisplayAccountInfo: MutableLiveData<DisplayAccountInfo> = MutableLiveData<DisplayAccountInfo>()
    val mIdAccountInfo:ObservableField<IdAccountInfo> = ObservableField()
    val mIdProductKey:ObservableField<IdProductKey> = ObservableField()

    fun initInfoItemViewModel() {
        this.mDisplayAccountInfo.postValue(null)
        this.mIdAccountInfo.set(null)
        this.mIdProductKey.set(null)
    }

    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------
    fun setDisplayAccountInfo(displayAccountInfo : DisplayAccountInfo){
        this.mDisplayAccountInfo.postValue(displayAccountInfo) // todo remove
    }

    fun setIdAccountInfo(idAccountInfo : IdAccountInfo){
        this.mIdAccountInfo.set(idAccountInfo)
    }

    fun setIdProductKey(idProductKey : IdProductKey){
        this.mIdProductKey.set(idProductKey)
    }



}