package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class AccountFormViewModel : ViewModel() {

    val dateFormat: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------
    val siteName:ObservableField<String> = ObservableField("")
    val personalAccount:ObservableField<String> = ObservableField("")
    val password:ObservableField<String> = ObservableField("")
    val createDate:ObservableField<String> = ObservableField(dateFormat.format(Date()))
    val tagColor:ObservableField<String> = ObservableField("#93534C")
    val siteUrl:ObservableField<String> = ObservableField("")
    val memo:ObservableField<String> = ObservableField("")


    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------
    fun setSiteName(siteName:String){
        this.siteName.set(siteName);
    }

    fun setPersonalAccount(personalAccount:String){
        this.personalAccount.set(personalAccount)
    }

    fun setPassword(password:String){
        this.password.set(password)
    }

    fun setCreateDate(date:Date){
        this.createDate.set(dateFormat.format(date))
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