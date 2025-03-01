package com.aiden.accountwallet.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.BuildConfig
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.data.dto.Permission
import com.aiden.accountwallet.data.dto.SettingItem
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.databinding.FragmentSettingBinding
import com.aiden.accountwallet.ui.dialog.AlertDialog
import com.aiden.accountwallet.ui.dialog.ProgressDialog
import com.aiden.accountwallet.ui.dialog.SelectDialog
import com.aiden.accountwallet.util.FileManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SettingFragment : BaseFragment<FragmentSettingBinding>(){

    // 다른 액티비티 이동후 결과 값을 받아 핸들링할 런쳐
    private lateinit var launcher: ActivityResultLauncher<Intent>;

    private lateinit var permissionInfoList:MutableList<Permission>
    private lateinit var settingOptionList:MutableList<SettingItem>

    // db vm
    private lateinit var userInfoViewModel:UserInfoViewModel
    private lateinit var identityInfoViewModel: IdentityInfoViewModel

    override fun initViewModel() {
        userInfoViewModel = getApplicationScopeViewModel(UserInfoViewModel::class.java)
        identityInfoViewModel = getApplicationScopeViewModel(IdentityInfoViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        permissionInfoList = mutableListOf()
        settingOptionList = mutableListOf()

        // set User permissions...
        val context = requireContext()
        val nameArray:Array<String> = context.resources
            .getStringArray(R.array.arr_user_permission); // 사용자 권한 명
        val guideArray:Array<String> = context.resources
            .getStringArray(R.array.arr_user_permission_desc); // 사용자 권한 설명
        val iconArray: TypedArray = context.resources.obtainTypedArray(R.array.icon_user_permissions) // 아이콘

        for(i in nameArray.indices){
            val drawable: Drawable? = iconArray.getDrawable(i)
            val mPermission = Permission(nameArray[i], guideArray[i], drawable, false)
            permissionInfoList.add(mPermission)
        }
        iconArray.recycle()

        // Setting Option...
        val settingArray:Array<String> = context.resources
            .getStringArray(R.array.arr_setting_option); // 메뉴 명

        // TODO : 실제 설정 다이얼로그와 연계하여 기능 구현...
        for(i in 0..5) {
            val settingItem = SettingItem(settingArray[i], "")
            when(i){
                0 -> { // 언어 설정
                    val langStr:String = Locale.getDefault().toLanguageTag()
                    settingItem.value = langStr
                }
                1 -> { // 앱 업데이트
                    settingItem.value = BuildConfig.VERSION_NAME
                }
                
                5 -> { // 데이터 초기화
                    settingItem.dangerFlag = true
                }
            }
            settingOptionList.add(settingItem)
        }

        return DataBindingConfig(R.layout.fragment_setting)
            .addBindingParam(BR.settingItemList, settingOptionList)
            .addBindingParam(BR.permissionInfoList, permissionInfoList)
            .addBindingParam(BR.pClick, permissionClickListener)
            .addBindingParam(BR.sClick, settingClickListener)
    }

    override fun initView() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { // 액티비티 종료시 결과릴 리턴받기 위한 콜백 함수
            val cancelFlag = it.resultCode == Activity.RESULT_CANCELED;
            if (it.resultCode == Activity.RESULT_OK) {
                // 데이터 찍어볼경우!
                /*
                val intent = result.data
                val resultState = intent?.getStringExtra("newAlbumName")
                Logger.i("resultState : %s", resultState)
                 */
            }
        }
        hideSettingMenu() // 세팅 메뉴 숨김
        checkUserPermission() // 사용자 권한 체크
    }

    // 권한 변경 페이지로 이동
    private fun popUpToAppSetting(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        Toast.makeText(requireContext(), getString(R.string.txt_guide_modify_permission), Toast.LENGTH_SHORT).show()
        launcher.launch(intent)
    }

    // 사용자 권한 체크 함수
    private fun checkUserPermission() {
        // 저장소 권한 체크
        val isStorageAllowed = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            val storagePermission = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            var flag = false;
            storagePermission.forEach { it ->
                flag = ContextCompat.checkSelfPermission(
                    requireContext(), it
                ) == PackageManager.PERMISSION_GRANTED || flag
            }
            flag
        }else true

        val mPermission = this.permissionInfoList[0]
        mPermission.isAllowed = isStorageAllowed
        this.permissionInfoList[0] = mPermission
        mBinding.setVariable(BR.permissionInfoList, permissionInfoList)
        mBinding.notifyChange()
    }

    // 세팅 메뉴 숨김 함수
    private fun hideSettingMenu(){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(@NonNull menu: Menu, @NonNull menuInflater: MenuInflater) {}
            override fun onPrepareMenu(@NonNull menu: Menu) {
                val item = menu.findItem(R.id.action_settings)
                item?.setVisible(false)
            }

            override fun onMenuItemSelected(@NonNull menuItem: MenuItem): Boolean {
                //...
                return false
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED)
    }

    // Listener
    // 사용자 권한 클릭 리스너
    private val permissionClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            val context = view.context
            val nameArray:Array<String> = context.resources
                .getStringArray(R.array.arr_user_permission); // 사용자 권한 명

            when(view.tag){
                nameArray[0] -> { // 저장소 권한
                    popUpToAppSetting()
                }
                else -> {

                }
            }

        }
    }

    // 세팅 클릭 리스너
    private val settingClickListener = object : ViewClickListener {
        override fun onViewClick(view: View) {
            val context = view.context
            val settingArray:Array<String> = context.resources
                .getStringArray(R.array.arr_setting_option); // 세팅 옵션 명

            when(view.tag){
                settingArray[0] -> { // 언어 설정
                    val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    launcher.launch(intent)
                }
                settingArray[1] -> { // 앱 업데이트 이동
                    try {
                        val mPlayUrl = context.getString(R.string.app_play_store_url)
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(mPlayUrl)
                            setPackage("com.android.vending")
                        }
                        startActivity(intent)
                    } catch (_:Exception){
                        Toast.makeText(
                            context,
                            getString(R.string.msg_invalid_vending),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                settingArray[2] -> { // 오픈 소스 라이선스 ...
                    val intent = Intent(requireActivity(), OssLicensesMenuActivity::class.java)
                    launcher.launch(intent)
                }
                settingArray[3] -> { // 데이터 백업 하기
                    val title:String = getString(R.string.title_data_export)
                    val content:String = getString(R.string.content_export_database)
                    val mBtnOk:String = getString(R.string.btn_start)
                        getString(R.string.btn_delete_database)
                    val mBtnCancel:String = getString(R.string.btn_cancel)
                    val alertInfo = AlertInfo(title, content, mBtnCancel, mBtnOk)
                    val dialog = AlertDialog(alertInfo, exportDialogListener)
                    dialog.show(requireActivity().supportFragmentManager, null)
                }
                settingArray[4] -> { // 데이터 불러오기

                    val alertInfo = AlertInfo(
                        "Title", "Content..."
                    )

                    val dialog = SelectDialog (
                        alertInfo,
                        object : SelectDialog.OnSelectListener {
                            override fun onSelect(position: Int) {
                                Toast.makeText(
                                    requireContext(),
                                    "user select idx = $position",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onCancel() {

                            }
                        }
                    )
                    dialog.show(requireActivity().supportFragmentManager, null)

                }
                settingArray[5] -> { // 데이터 초기화
                    val title:String = getString(R.string.title_warning)
                    val content:String = getString(R.string.content_delete_database)
                    val mBtnOk:String = getString(R.string.btn_delete_database)
                    val mBtnCancel:String = getString(R.string.btn_cancel)
                    val alertInfo = AlertInfo(
                        title, content, mBtnCancel, mBtnOk, true
                   )
                    val dialog = AlertDialog(alertInfo, resetDialogListener)
                    dialog.show(requireActivity().supportFragmentManager, null)
                }
                else -> {

                }
            }
        }
    }

    // 데이터 내보내기 함수 (실제 내보내기 작업을 처리하는 핸들러)
    private fun startExportData(dialog: ProgressDialog){
        val msgLoadAccount:String = getString(R.string.load_user_account)
        val msgLoadProduct:String = getString(R.string.load_product_key)
        val context:Context = requireContext()
        var currentProgress = 0;
        val finView:View = mBinding.spBottom

        lifecycleScope.launch(Dispatchers.IO) {
            while(!(dialog.getIsInitView())){ // 준비 될때까지 대기!
                delay(200)
            }
            currentProgress += 5
            dialog.setDialogStatus(msgLoadAccount)
            dialog.setDialogProgress(currentProgress)

            // 계정 준비 작업
            val accountList:List<IdAccountInfo> = identityInfoViewModel.readAllAccountList()
            delay(100)
            currentProgress += 5
            dialog.setDialogProgress(currentProgress)

            // 제품키 준비 작업
            currentProgress += 5
            dialog.setDialogStatus(msgLoadProduct)
            dialog.setDialogProgress(currentProgress)

            val productList:List<IdProductKey> = identityInfoViewModel.readAllProductList()
            delay(100)
            currentProgress += 5
            dialog.setDialogProgress(currentProgress)

            // 실제 파일 저장 작업!
            FileManager.exportJsonData(
                context, accountList, productList,
                object : FileManager.FileListener {
                    override fun onFileSaved(savePath:String) {
                        val snackMsg:String = getString(R.string.msg_fin_export_path, savePath)
                        CoroutineScope(Dispatchers.Main).launch {
                            Snackbar.make(finView, snackMsg, Snackbar.LENGTH_LONG).show()
                        }
                        dialog.notifyFinishTask(100)
                    }

                    override fun onFileSaveListener(progress: Int) {
                        dialog.setDialogProgress(progress)
                    }

                    override fun onFileSaveFail() {
                        dialog.notifyFinishTask()
                    }
                }
            )
        }
    }



    // Dialog Listener
    // 데이터 내보내기 리스너
    private val exportDialogListener = object :AlertDialog.OnDialogClickListener{
        override fun onOk(view: View) {
            when(view.id){
                R.id.btn_ok -> {
                    val titleDataExport:String = getString(R.string.title_data_xport)
                    val alertInfo = AlertInfo(titleDataExport, flag = true)
                    val dialog = ProgressDialog(
                        alertInfo, object : ProgressDialog.OnProgressListener {
                            // 사용자에 의한 작업 중단 요청
                            override fun onAction(view: View) {

                            }
                        }
                    )
                    dialog.show(requireActivity().supportFragmentManager, null)
                    startExportData(dialog)
                }
            }
        }
        override fun onCancel(view: View) {}
    }

    // 데이터 초기화 리스너
    private val resetDialogListener = object : AlertDialog.OnDialogClickListener {
        override fun onOk(view: View) {
            when(view.id){
                R.id.btn_ok -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        identityInfoViewModel.removeAll()
                        userInfoViewModel.removeAll()

                        // Notify to User...
                        withContext(Dispatchers.Main){
                            val msg:String = getString(R.string.msg_database_delete_all)
                            val context:Context = requireContext()
                            Toast.makeText(
                                context,
                                msg,
                                Toast.LENGTH_SHORT
                            ).show()

                            nav().popBackStack(R.id.settingFragment, true)
                            nav().navigate(R.id.introFragment)
                        }
                    }
                }
            }

        }
        override fun onCancel(view: View) {}
    }


}