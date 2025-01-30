package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.databinding.FragmentViewAccountBinding
import com.aiden.accountwallet.ui.dialog.AlertDialog
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel

class ViewAccountFragment : BaseFragment<FragmentViewAccountBinding>(),
    ViewClickListener {

    // vm
    private lateinit var infoItemViewModel: InfoItemViewModel
    private lateinit var navController: NavController

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_view_account)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_view_form) as NavHostFragment).navController
        // navController.navigate(R.id.productViewFragment)

        infoItemViewModel.mDisplayAccountInfo.observe(viewLifecycleOwner) { it ->
            if(it != null && it.providerName.isNotBlank()){
                Toast.makeText(requireContext(), it.providerName, Toast.LENGTH_SHORT).show()

                when(it.typeIdx){
                    1 -> {
                        navController.navigate(R.id.productViewFragment)
                    }
                    else -> {
                        navController.navigate(R.id.accountViewFragment)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        infoItemViewModel.initInfoItemViewModel()
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