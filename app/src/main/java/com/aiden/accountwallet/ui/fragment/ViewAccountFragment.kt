package com.aiden.accountwallet.ui.fragment

import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.FragmentViewAccountBinding
import com.aiden.accountwallet.ui.dialog.AlertDialog

class ViewAccountFragment : BaseFragment<FragmentViewAccountBinding>(),
    ViewClickListener {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_view_account)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {

    }

    override fun initView() {
        val navController = (childFragmentManager
            .findFragmentById(R.id.fragment_view_form) as NavHostFragment).navController

        // navController.navigate(R.id.productViewFragment)
    }

    override fun onViewClick(view: View) {
        when(view.id) {
            R.id.btn_edit_account -> {
                nav().navigate(R.id.action_move_edit_account)
            }
            R.id.iv_delete -> {

                val tempInfo = AlertInfo(
                    "Title", "Content...", flag = true
                )

                val dialog = AlertDialog(
                    tempInfo,
                    object : AlertDialog.OnDialogClickListener {
                        override fun onOk(view: View) {
                        }

                        override fun onCancel(view: View) {
                        }
                    }
                )
                dialog.show(requireActivity().supportFragmentManager, null)
            }
            else -> {

            }
        }
    }
}