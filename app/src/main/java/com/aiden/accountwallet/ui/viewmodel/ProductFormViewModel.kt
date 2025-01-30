package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.data.model.IdProductKey

class ProductFormViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------
    val updateStatus: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val providerName: ObservableField<String> = ObservableField("")
    val productKey: ObservableField<String> = ObservableField("")
    val tagColor:ObservableField<String> = ObservableField("#93534C")
    val siteUrl:ObservableField<String> = ObservableField("")
    val memo:ObservableField<String> = ObservableField("")

    fun initVariables() {
        this.updateStatus.postValue(false)
        this.providerName.set("")
        this.productKey.set("")
        this.tagColor.set("#93534C")
        this.siteUrl.set("")
        this.memo.set("")
    }

    fun initVariables(idProductKey: IdProductKey) {
        this.providerName.set(idProductKey.baseInfo.providerName)
        this.productKey.set(idProductKey.productKey.productKey)
        this.tagColor.set(idProductKey.baseInfo.tagColor)
        this.siteUrl.set(idProductKey.productKey.officialUrl)
        this.memo.set(idProductKey.baseInfo.memo)
        this.updateStatus.postValue(true) // notify data setup
    }

    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------

    fun setProviderName(providerName:String){
        this.providerName.set(providerName)
    }

    fun setProductKey(productKey:String){
        this.productKey.set(productKey)
    }

    fun setTagColor(tagColor:String){
        this.tagColor.set(tagColor)
    }

    fun setSiteUrl(siteUrl:String){
        this.siteUrl.set(siteUrl)
    }

    fun setMemo(memo:String){
        this.memo.set(memo)
    }
}