package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class ProductFormViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------
    val providerName: ObservableField<String> = ObservableField("")
    val productKey: ObservableField<String> = ObservableField("")
    val tagColor:ObservableField<String> = ObservableField("#93534C")
    val siteUrl:ObservableField<String> = ObservableField("")
    val memo:ObservableField<String> = ObservableField("")

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
        this.tagColor.set("#$tagColor")
    }

    fun setSiteUrl(siteUrl:String){
        this.siteUrl.set(siteUrl)
    }

    fun setMemo(memo:String){
        this.memo.set(memo)
    }
}