package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.ArrayAdapter
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

        // 타입 배열
        val items = resources.getStringArray(R.array.spinner_info_type)
        // ArrayAdapter에 커스텀 레이아웃 적용
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        return DataBindingConfig(R.layout.fragment_edit_account)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.spinnerAdapter, adapter)
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