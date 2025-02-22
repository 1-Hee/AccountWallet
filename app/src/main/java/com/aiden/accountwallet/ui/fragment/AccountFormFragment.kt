package com.aiden.accountwallet.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.provider.Contacts.Intents.UI
import android.view.KeyEvent
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.BuildConfig
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.OnEditorActionListener
import com.aiden.accountwallet.base.listener.OnKeyListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.databinding.FragmentAccountFormBinding
import com.aiden.accountwallet.ui.viewmodel.AccountFormViewModel
import com.aiden.accountwallet.ui.viewmodel.InfoItemViewModel
import com.aiden.accountwallet.util.TimeParser.DATE_TIME_FORMAT
import com.aiden.accountwallet.util.TimeParser.getSimpleDateFormat
import com.aiden.accountwallet.util.UIManager
import com.aiden.accountwallet.util.UIManager.hideKeyPad
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import timber.log.Timber
import java.util.Calendar
import java.util.Date
import kotlin.math.cos


class AccountFormFragment : BaseFragment<FragmentAccountFormBinding>(),
    ViewClickListener, OnEditorActionListener, OnKeyListener {

    private lateinit var accountFormViewModel: AccountFormViewModel
    private lateinit var infoItemViewModel: InfoItemViewModel

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_account_form)
            .addBindingParam(BR.vm, accountFormViewModel)
            .addBindingParam(BR.click, this)
            .addBindingParam(BR.editAction, this)
            .addBindingParam(BR.onKeyAction, this)
    }

    override fun initViewModel() {
        accountFormViewModel = getApplicationScopeViewModel(
            AccountFormViewModel::class.java
        )
        infoItemViewModel = getApplicationScopeViewModel(
            InfoItemViewModel::class.java
        )

    }

    override fun initView() {
        accountFormViewModel.updateStatus.observe(viewLifecycleOwner){
            if(it){
                notifyAccountInfo()
                mBinding.setVariable(BR.vm, accountFormViewModel)
                mBinding.notifyChange()
                accountFormViewModel.setUpdateStatus(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        accountFormViewModel.initVariables()

    }

    private fun notifyAccountInfo() {
        val providerName:String = mBinding.etSiteName.text.toString()
        val personalAccount:String = mBinding.etPersonalAccount.text.toString()
        val password:String = mBinding.etPassword.text.toString()
        var tagStr:String = mBinding.tvColorTag.text.toString()
        if(tagStr.isBlank()) {
            tagStr = getString(R.string.def_tag_color)
        }
        val urlStr:String = mBinding.etSiteUrl.text.toString()
        val memoStr:String = mBinding.etMemo.text.toString()

        Timber.i("[Account Date] : %s", accountFormViewModel.createDate.get())

        accountFormViewModel.setSiteName(providerName)
        accountFormViewModel.setPersonalAccount(personalAccount)
        accountFormViewModel.setPassword(password)
        accountFormViewModel.setTagColor(tagStr)
        // set tag color
        val colorHex:String = tagStr
        val color:Int = UIManager.getColor(requireContext(), colorHex)
        mBinding.vColorTag.setBackgroundColor(color)
        accountFormViewModel.setSiteUrl(urlStr)
        accountFormViewModel.setMemo(memoStr)
        mBinding.notifyChange()
    }

    private fun popUpDateDialog(context: Context) {
        // 날짜 & 시간 다이얼로그
        val calendar: Calendar = Calendar.getInstance()

        // 날짜 선택 다이얼로그
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // 시간 선택 다이얼로그
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _: TimePicker?, hourOfDay: Int, minute: Int ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        // 선택된 날짜 및 시간 업데이트
                         accountFormViewModel.setCreateDate(calendar.time)
                        // selectedDateTime.set(dateTimeFormat.format(calendar.getTime()))
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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
                    mBinding.tvColorTag.setText(colorStr)
                    // mBinding.tvColorTag.text = colorStr
                     accountFormViewModel.setTagColor(colorStr)
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
            R.id.et_create_date -> {
                popUpDateDialog(context)
            }

            R.id.v_color_tag -> {
                popUpColorDialog(context)
            }

            R.id.btn_refresh_color -> {
                val colorStr:String = getString(R.string.def_tag_color)
                mBinding.tvColorTag.setText(colorStr)
                // accountFormViewModel.setTagColor(colorStr)
                val color:Int = UIManager.getColor(context, colorStr)
                mBinding.vColorTag.setBackgroundColor(color)

                val msg:String = getString(R.string.msg_refresh_tag_color)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onEditorAction(view: TextView?, actionEvent: Int, keyEvent: KeyEvent?): Boolean {
        Timber.i("event(action) : %s", keyEvent)
        if(view == null) return false
        val inputText:String = view.text.toString()

        if (BuildConfig.DEBUG) {
            Timber.i("Input Text : %s", inputText)
            Timber.i("Action Event : %d", actionEvent)
        }

        hideKeyPad(requireActivity())
        when(view.id){
            R.id.et_site_name -> {
                accountFormViewModel.setSiteName(inputText)
            }
            R.id.et_personal_account -> {
                accountFormViewModel.setPersonalAccount(inputText)
            }
            R.id.et_password -> {
                accountFormViewModel.setPassword(inputText)
            }
            R.id.et_site_url -> {
                accountFormViewModel.setSiteUrl(inputText)
            }
            R.id.et_memo -> {
                accountFormViewModel.setMemo(inputText)
            }
            R.id.tv_color_tag -> {
                val context:Context = requireContext()
                val colorStr:String = inputText
                mBinding.tvColorTag.setText(colorStr)
                val color:Int = UIManager.getColor(colorStr)
                // Check Color
                if(color == Color.GRAY){
                    val errColorMsg:String = getString(R.string.msg_invalid_color_value)
                    Toast.makeText(context, errColorMsg, Toast.LENGTH_SHORT).show()
                    return true
                }
                accountFormViewModel.setTagColor(colorStr)
                mBinding.vColorTag.setBackgroundColor(color)
                mBinding.notifyChange()
            }
            else -> {
                return false
            }
        }
         return true
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
                R.id.et_site_name -> {
                    accountFormViewModel.setSiteName(inputText)
                }
                R.id.et_personal_account -> {
                    accountFormViewModel.setPersonalAccount(inputText)
                }
                R.id.et_password -> {
                    accountFormViewModel.setPassword(inputText)
                }
                R.id.et_site_url -> {
                    accountFormViewModel.setSiteUrl(inputText)
                }
                R.id.et_memo -> {
                    accountFormViewModel.setMemo(inputText)
                }
                R.id.tv_color_tag -> {
                    val context:Context = requireContext()
                    val colorStr:String = inputText
                    mBinding.tvColorTag.setText(colorStr)
                    val color:Int = UIManager.getColor(colorStr)
                    // Check Color
                    if(color == Color.GRAY){
                        val errColorMsg:String = getString(R.string.msg_invalid_color_value)
                        Toast.makeText(context, errColorMsg, Toast.LENGTH_SHORT).show()
                        return true
                    }
                    accountFormViewModel.setTagColor(colorStr)
                    mBinding.vColorTag.setBackgroundColor(color)
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