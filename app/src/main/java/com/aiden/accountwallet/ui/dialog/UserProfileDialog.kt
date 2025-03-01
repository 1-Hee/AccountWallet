package com.aiden.accountwallet.ui.dialog

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseDialog
import com.aiden.accountwallet.data.dto.UserProfile
import com.aiden.accountwallet.data.model.UserInfo
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.DialogUserProfileBinding
import com.aiden.accountwallet.ui.viewmodel.UserProfileViewModel
import com.aiden.accountwallet.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileDialog(
    private val userProfile: UserProfile,
    private val listener: OnDialogClickListener
) : BaseDialog<DialogUserProfileBinding>(),
    ViewClickListener {

    // vm
    private lateinit var userProfileViewModel:UserProfileViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.dialog_user_profile, BR.vm, userProfileViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        userProfileViewModel = getDialogScopeViewModel(
            UserProfileViewModel::class.java
        )
        userProfileViewModel.setUserProfile(this.userProfile)
        userInfoViewModel = getApplicationScopeViewModel(UserInfoViewModel::class.java)
    }

    override fun initView() {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userInfo: UserInfo = userInfoViewModel.getLastUserInfo() ?: return@launch
            val mUserProfile:UserProfile = this@UserProfileDialog.userProfile
            mUserProfile.userNickname = userInfo.nickName
            Logger.i("[EDIT NICKNAME] Load Nickname : %s / %s",
                userInfo.nickName, mUserProfile.userNickname
            )

            withContext(Dispatchers.Main){
                userProfileViewModel.setUserProfile(mUserProfile)
                userProfileViewModel.setUserInfo(userInfo)
                mBinding.setVariable(BR.vm, userProfileViewModel)
                mBinding.notifyChange()
            }
        }
    }

    private suspend fun editUserProfileTask(): Boolean {
        val nNewNickname:String = mBinding.etEditNickname.text.toString()
        if(nNewNickname.isBlank()) return false

        val mProfile:UserProfile = this.userProfile
        mProfile.userNickname = nNewNickname

        val mUserInfo:UserInfo = this@UserProfileDialog
            .userProfileViewModel.userInfo.get()?:return false

        Logger.i(
            "[EDIT NICKNAME] Change User Nick name | %s -> %s",
            mUserInfo.nickName, mProfile.userNickname
        )
        mUserInfo.nickName = mProfile.userNickname

        withContext(Dispatchers.Main){
            this@UserProfileDialog.userProfileViewModel.setUserProfile(mProfile)
            this@UserProfileDialog.userProfileViewModel.setUserInfo(mUserInfo)
        }
        // Database Task
        userInfoViewModel.editEntity(mUserInfo)
        return true
    }

    interface OnDialogClickListener {
        fun onEdit(view: View, item:UserProfile?)
        fun onEditCancel(view: View)
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_ok -> {
                lifecycleScope.launch(Dispatchers.IO){
                    editUserProfileTask()
                    val item:UserProfile? = userProfileViewModel.userProfile.get()
                    withContext(Dispatchers.Main){
                        listener.onEdit(view, item)
                        dismiss()
                    }
                }
            }
            R.id.btn_cancel -> {
                listener.onEditCancel(view)
                dismiss()
            }
        }
    }
}