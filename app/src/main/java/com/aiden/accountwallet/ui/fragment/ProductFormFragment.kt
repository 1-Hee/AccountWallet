package com.aiden.accountwallet.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.BuildConfig
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.OnEditorActionListener
import com.aiden.accountwallet.base.listener.OnKeyListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentProductFormBinding
import com.aiden.accountwallet.ui.viewmodel.ProductFormViewModel
import com.aiden.accountwallet.util.UIManager.hideKeyPad
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import timber.log.Timber

class ProductFormFragment : BaseFragment<FragmentProductFormBinding>(),
    ViewClickListener, OnEditorActionListener, OnKeyListener {

    private lateinit var productFormViewModel: ProductFormViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_product_form, BR.vm, productFormViewModel)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.editAction, this)
            .addBindingParam(BR.onKeyAction, this)
    }

    override fun initViewModel() {
        productFormViewModel = getApplicationScopeViewModel(
            ProductFormViewModel::class.java
        )
    }

    override fun initView() {
        productFormViewModel.updateStatus.observe(viewLifecycleOwner) {
            if(it){
                notifyAccountInfo()
                productFormViewModel.updateStatus.postValue(false)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        productFormViewModel.initVariables()
    }

    private fun notifyAccountInfo() {
        val providerName:String = mBinding.etProviderName.text.toString()
        val productKey:String = mBinding.etProductKey.text.toString()
        var tagStr:String = mBinding.tvColorTag.text.toString()
        if(tagStr.isBlank()) {
            tagStr = getString(R.string.def_tag_color)
        }
        val urlStr:String = mBinding.etSiteUrl.text.toString()
        val memoStr:String = mBinding.etMemo.text.toString()

        productFormViewModel.setProviderName(providerName)
        productFormViewModel.setProductKey(productKey)
        productFormViewModel.setTagColor(tagStr)
        productFormViewModel.setSiteUrl(urlStr)
        productFormViewModel.setMemo(memoStr)
    }

    @SuppressLint("SetTextI18n")
    private fun popUpColorDialog(context: Context) {
        ColorPickerDialog.Builder(context)
            .setTitle(getString(R.string.title_select_tag))
            .setPreferenceName("SelectTagColorDialog")
            .setPositiveButton(
                getString(R.string.btn_select),
                ColorEnvelopeListener { envelope, fromUser ->
                    mBinding.vColorTag.setBackgroundColor(envelope.color)
                    val colorStr = "#${envelope.hexCode.substring(2)}"
                    mBinding.tvColorTag.text = colorStr
                    productFormViewModel.setTagColor(colorStr)
                })
            .setNegativeButton(
                getString(R.string.btn_cancel)
            ) { dialogInterface, i -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(false) // the default value is true.
            .attachBrightnessSlideBar(false) // the default value is true.
            .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
            .show()
    }

    override fun onViewClick(view: View) {
        val context:Context = requireContext()
        when(view.id){
            R.id.v_color_tag -> {
                popUpColorDialog(context)
            }
        }

    }

    override fun onEditorAction(view: TextView?, actionEvent: Int, keyEvent: KeyEvent?): Boolean {
        if(view == null) return false
        if(keyEvent == null) return false
        val inputText:String = view.text.toString()

        if (BuildConfig.DEBUG) {
            Timber.i("Input Text : %s", inputText)
            Timber.i("Action Event : %d", actionEvent)
        }

        return if(actionEvent == 5) {
            hideKeyPad(requireActivity())
            when(view.id){
                R.id.et_provider_name -> {
                    productFormViewModel.setProviderName(inputText)
                }
                R.id.et_product_key -> {
                    productFormViewModel.setProductKey(inputText)
                }
                R.id.et_site_url -> {
                    productFormViewModel.setSiteUrl(inputText)
                }
                R.id.et_memo -> {
                    productFormViewModel.setMemo(inputText)
                }
                else -> {
                    return false
                }
            }
            true
        } else {
            false
        }
    }

    override fun onKey(view: View?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
        if(view == null) return false
        if(view !is TextView) return false
        if(keyEvent == null) return false
        val inputText:String = view.text.toString()

        return if(keyCode == KeyEvent.KEYCODE_ENTER
            && keyEvent.action == KeyEvent.ACTION_DOWN) {
            hideKeyPad(requireActivity())
            when(view.id){
                R.id.et_provider_name -> {
                    productFormViewModel.setProviderName(inputText)
                }
                R.id.et_product_key -> {
                    productFormViewModel.setProductKey(inputText)
                }
                R.id.et_site_url -> {
                    productFormViewModel.setSiteUrl(inputText)
                }
                R.id.et_memo -> {
                    productFormViewModel.setMemo(inputText)
                }
                else -> {
                    return false
                }
            }
            true
        } else {
            if (BuildConfig.DEBUG) {
                Timber.i("Input Text : %s", inputText)
                Timber.i("Key Code : %d", keyCode)
            }
            false
        }
    }
}