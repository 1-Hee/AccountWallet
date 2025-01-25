package com.aiden.accountwallet.ui.fragment

import android.view.View
import android.widget.ArrayAdapter
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentEditAccountBinding
import com.aiden.accountwallet.ui.viewmodel.InfoTypeViewModel

class EditAccountFragment : BaseFragment<FragmentEditAccountBinding>(),
    ViewClickListener, InfoTypeViewModel.InfoTypeCallback  {

    private lateinit var infoTypeViewModel: InfoTypeViewModel
    private lateinit var navController: NavController


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_edit_account)
            .addBindingParam(BR.infoVm, infoTypeViewModel)
            .addBindingParam(BR.click, this)
    }

    override fun initViewModel() {
        infoTypeViewModel = getFragmentScopeViewModel(InfoTypeViewModel::class.java)
        // 타입 배열
        val items = resources.getStringArray(R.array.spinner_info_type)
        // ArrayAdapter에 커스텀 레이아웃 적용
        val adapter:ArrayAdapter<String> = ArrayAdapter(requireContext(), R.layout.custom_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        infoTypeViewModel.initInfoTypes(items, adapter)
        infoTypeViewModel.setCallback(this)
    }

    override fun initView() {
        navController = (childFragmentManager
            .findFragmentById(R.id.fragment_edit_form) as NavHostFragment).navController
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

    override fun onItemSelected(selectedItem: String?, position: Int) {
        when(position){
            1 -> {
                navController.navigate(R.id.productFormFragment)
            }
            else ->{
                navController.navigate(R.id.accountFormFragment)
            }
        }
    }
}