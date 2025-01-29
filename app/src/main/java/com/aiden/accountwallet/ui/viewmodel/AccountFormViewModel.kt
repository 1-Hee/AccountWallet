package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData

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
    val updateStatus:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val siteName:ObservableField<String> = ObservableField("")
    val personalAccount:ObservableField<String> = ObservableField("")
    val password:ObservableField<String> = ObservableField("")
    val createDateStr:ObservableField<String> = ObservableField(dateFormat.format(Date()))
    val createDate:ObservableField<Date> = ObservableField(Date())
    val tagColor:ObservableField<String> = ObservableField("#93534C")
    val siteUrl:ObservableField<String> = ObservableField("")
    val memo:ObservableField<String> = ObservableField("")


    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------
    fun setSiteName(siteName:String){
        this.siteName.set(siteName);
        val flag = siteName.isNotBlank()
        this.updateStatus.postValue(flag)
    }

    fun setPersonalAccount(personalAccount:String){
        this.personalAccount.set(personalAccount)
        val flag = personalAccount.isNotBlank()
        this.updateStatus.postValue(flag)
    }

    fun setPassword(password:String){
        this.password.set(password)
        val flag = password.isNotBlank()
        this.updateStatus.postValue(flag)
    }

    fun setCreateDate(date:Date){
        val dateStr:String = dateFormat.format(date)
        this.createDateStr.set(dateStr)
        this.createDate.set(date)
        val flag = dateStr.isNotBlank()
        this.updateStatus.postValue(flag)

    }

    fun setTagColor(tagColor:String){
        this.tagColor.set("#$tagColor")
        val flag = tagColor.isNotBlank()
        this.updateStatus.postValue(flag)
    }

    fun setSiteUrl(siteUrl:String){
        this.siteUrl.set(siteUrl)
        val flag = siteUrl.isNotBlank()
        this.updateStatus.postValue(flag)
    }

    fun setMemo(memo:String){
        this.memo.set(memo)
        val flag = memo.isNotBlank()
        this.updateStatus.postValue(flag)
    }

}