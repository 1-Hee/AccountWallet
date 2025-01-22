package com.aiden.accountwallet.ui.fragment

import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentEditAccountBinding

class EditAccountFragment : BaseFragment<FragmentEditAccountBinding>(),
    ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_edit_account)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
        val navController = (childFragmentManager
            .findFragmentById(R.id.fragment_edit_form) as NavHostFragment).navController

        // navController.navigate(R.id.productFormFragment)
    }

    override fun onViewClick(view: View) {
        when(view.id){
            R.id.btn_edit_account -> {
                nav().popBackStack()
            }
            else ->{

            }
        }
    }
}