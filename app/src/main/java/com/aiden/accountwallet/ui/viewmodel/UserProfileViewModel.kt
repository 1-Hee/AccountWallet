package com.aiden.accountwallet.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.data.dto.UserProfile
import com.aiden.accountwallet.data.model.UserInfo

class UserProfileViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------
    val userProfile:ObservableField<UserProfile> = ObservableField()
    val userInfo:ObservableField<UserInfo> = ObservableField()

    // * ------------------------------------------------
    // *    init Method
    // * ------------------------------------------------
    fun initVariables() {
        this.userProfile.set(null)
        this.userInfo.set(null)
    }

    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------

    fun setUserProfile(userProfile: UserProfile?) {
        this.userProfile.set(userProfile)
    }

    fun setUserInfo(userInfo: UserInfo?) {
        this.userInfo.set(userInfo)
    }

}