package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.data.model.IdAccountInfo
import java.util.Date
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat

class AccountFormViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------
    val updateStatus:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val siteName:ObservableField<String> = ObservableField("")
    val personalAccount:ObservableField<String> = ObservableField("")
    val password:ObservableField<String> = ObservableField("")
    val createDateStr:ObservableField<String> = ObservableField(getSimpleDateFormat().format(Date()))
    val createDate:ObservableField<Date> = ObservableField(Date())
    val tagColor:ObservableField<String> = ObservableField("#93534C")
    val siteUrl:ObservableField<String> = ObservableField("")
    val memo:ObservableField<String> = ObservableField("")

    fun initVariables() {
        this.updateStatus.postValue(false)
        this.siteName.set("")
        this.personalAccount.set("")
        this.password.set("")
        this.createDateStr.set(getSimpleDateFormat().format(Date()))
        this.createDate.set(Date())
        this.tagColor.set("#93534C")
        this.siteUrl.set("")
        this.memo.set("")
    }

    fun initVariables(idAccountInfo : IdAccountInfo) {
        this.siteName.set(idAccountInfo.baseInfo.providerName)
        this.personalAccount.set(idAccountInfo.accountInfo.userAccount)
        this.password.set(idAccountInfo.accountInfo.userPassword)
        this.createDateStr.set(getSimpleDateFormat()
            .format(idAccountInfo.accountInfo.acCreatedAt
            ))
        this.createDate.set(idAccountInfo.accountInfo.acCreatedAt)
        this.tagColor.set(idAccountInfo.baseInfo.tagColor)
        this.siteUrl.set(idAccountInfo.accountInfo.officialUrl)
        this.memo.set(idAccountInfo.baseInfo.memo)
        this.updateStatus.postValue(true) // notify data setup
    }

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
        val dateStr:String = getSimpleDateFormat().format(date)
        this.createDateStr.set(dateStr)
        this.createDate.set(date)
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