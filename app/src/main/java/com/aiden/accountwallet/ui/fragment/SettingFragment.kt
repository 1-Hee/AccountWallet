package com.aiden.accountwallet.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
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
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.data.vo.ImportProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import com.aiden.accountwallet.databinding.FragmentSettingBinding
import com.aiden.accountwallet.ui.dialog.AlertDialog
import com.aiden.accountwallet.ui.dialog.ImportDataDialog
import com.aiden.accountwallet.ui.dialog.ProgressDialog
import com.aiden.accountwallet.util.FileManager
import com.aiden.accountwallet.util.Logger
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
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
                    val titleDataImport:String = getString(R.string.title_data_import)
                    val btnImport:String = getString(R.string.btn_data_import)
                    val alertInfo = AlertInfo(titleDataImport, txtOk = btnImport)
                    val dialog = ImportDataDialog (
                        alertInfo, importDialogListener
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
                        val statusMSg:String = context.getString(
                            R.string.msg_export_file_progress
                        ) + "\t( $progress% )"
                        dialog.setDialogStatus(statusMSg)
                    }

                    override fun onFileSaveFail() {
                        dialog.notifyFinishTask()
                    }
                }
            )
        }
    }

    // 데이터 불러오기
    private fun startImportData(filePath:String, dialog: ProgressDialog){
        val msgPrepareImport:String = getString(R.string.load_backup_data)
        val context:Context = requireContext()
        var currentProgress = 0;
        val finView:View = mBinding.spBottom

        lifecycleScope.launch(Dispatchers.IO) {
            while(!(dialog.getIsInitView())){ // 준비 될때까지 대기!
                delay(200)
            }
            currentProgress += 5
            dialog.setDialogStatus(msgPrepareImport)
            dialog.setDialogProgress(currentProgress)

            // 실제 파일 불러오기 작업!
            // FileManager ...
            lifecycleScope.launch(Dispatchers.IO) {

                FileManager.importJsonFile(filePath, object : FileManager.ReadListener {
                    override fun onFileRead(readData: String) {
                        // Logger.i("Read Data : %s", readData)
                        currentProgress += 15;
                        CoroutineScope(Dispatchers.Main).launch {
                            dialog.setDialogProgress(currentProgress)
                            dialog.setDialogStatus("백업 데이터를 불러오는데 성공하였습니다.")
                        }

                        val jsonString:String = readData
                        val jsonObject:JsonObject = Gson()
                            .fromJson(jsonString, JsonObject::class.java) // JSON 파싱

                        val mAccountTag:String = context.getString(R.string.key_user_account)
                        val mProductTag:String = context.getString(R.string.key_product)

                        // "UserAccount" 배열 꺼내기
                        val userAccounts: JsonArray = jsonObject
                            .getAsJsonArray(mAccountTag)
                        currentProgress += 5
                        CoroutineScope(Dispatchers.Main).launch {
                            dialog.setDialogProgress(currentProgress)
                            dialog.setDialogStatus("사용자 계정 정보 불러오는 중 ...")
                        }

                        // ProductKey
                        val productKey: JsonArray = jsonObject
                            .getAsJsonArray(mProductTag)
                        currentProgress += 5
                        CoroutineScope(Dispatchers.Main).launch {
                            dialog.setDialogProgress(currentProgress)
                            dialog.setDialogStatus("제품키 정보 불러오는 중 ...")
                        }


                        val progressUnit:Int = (100 - currentProgress)
                        val totalSize:Int = productKey.size() + userAccounts.size()
                        val progress:Int = (1 * progressUnit / totalSize)

                        val dateFormat = SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss",
                            Locale.getDefault()) // 날짜 형식 지정

                        val arrIdentity:Array<String> = context.resources
                            .getStringArray(R.array.items_identity_header)

                        val arrAccount:Array<String> = context.resources
                            .getStringArray(R.array.items_account_header)

                        val arrProduct:Array<String> = context.resources
                            .getStringArray(R.array.items_account_header)

                        val delayGap:Long = 500

                        userAccounts.forEach { element ->
                            val userObject:JsonObject = element.asJsonObject
                            currentProgress += progress
                            dialog.setDialogProgress(currentProgress)
                            val importAccount = ImportUserAccount()

                            userObject.entrySet().forEach { entry ->
                                val key = entry.key
                                val value = entry.value

                                // 데이터 타입 판별
                                /*
                                val parsedValue = when {
                                    value.isJsonPrimitive -> {
                                        when {
                                            value.asJsonPrimitive.isNumber -> value.asInt // 정수형
                                            value.asJsonPrimitive.isBoolean -> value.asBoolean // 불리언
                                            value.asJsonPrimitive.isString -> {
                                                val stringValue = value.asString
                                                if (stringValue.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))) { // 날짜 형식이면 Date로 변환
                                                    dateFormat.parse(stringValue)
                                                } else {
                                                    stringValue // 일반 문자열
                                                }
                                            }
                                            else -> value.toString()
                                        }
                                    }
                                    value.isJsonArray -> "Array(${value.asJsonArray.size()})" // 배열 크기 출력
                                    value.isJsonObject -> "Object" // JSON 객체 표시
                                    else -> "Unknown"
                                }
                                 */

                                when(key){
                                    arrIdentity[0] -> {
                                        importAccount.infoType = value.asInt
                                    }
                                    arrIdentity[1] -> {
                                        importAccount.providerName = value.asString
                                    }
                                    arrIdentity[2] -> {
                                        val stringValue:String = value.asString
                                        if (stringValue.matches(Regex(
                                                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                            ))) { // 날짜 형식 변환
                                            importAccount.createAt = dateFormat.parse(stringValue)
                                        }
                                    }
                                    arrIdentity[3] -> {
                                        val stringValue:String = value.asString
                                        if (stringValue.matches(Regex(
                                                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                            ))) { // 날짜 형식 변환
                                            importAccount.lastUpdate = dateFormat.parse(stringValue)
                                        }
                                    }
                                    arrIdentity[4] -> {
                                        importAccount.userMemo = value.asString
                                    }
                                    arrIdentity[5] -> {
                                        importAccount.tagColor = value.asString
                                    }
                                    arrAccount[0] -> {
                                        importAccount.usrAccount = value.asString
                                    }
                                    arrAccount[1] -> {
                                        importAccount.usrPwd = value.asString
                                    }
                                    arrAccount[2] -> {
                                        val stringValue:String = value.asString
                                        if (stringValue.matches(Regex(
                                                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                            ))) { // 날짜 형식 변환
                                            importAccount.acCreateAt = dateFormat.parse(stringValue)
                                        }
                                    }
                                    arrAccount[3] -> {
                                        importAccount.offUrl = value.asString
                                    }
                                }
                            }

                            lifecycleScope.launch(Dispatchers.IO) {
                                identityInfoViewModel.addImportUserAccount(importAccount)
                            }

                            Thread.sleep(delayGap)
                            // delay(delayGap)
                        }


                        productKey.forEach { element ->
                            val productObject:JsonObject = element.asJsonObject

                            currentProgress += progress
                            dialog.setDialogProgress(currentProgress)
                            val importProductKey = ImportProductKey()

                            productObject.entrySet().forEach { entry ->
                                val key = entry.key
                                val value = entry.value

                                // 데이터 타입 판별
                                /*
                                val parsedValue = when {
                                    value.isJsonPrimitive -> {
                                        when {
                                            value.asJsonPrimitive.isNumber -> value.asInt // 정수형
                                            value.asJsonPrimitive.isBoolean -> value.asBoolean // 불리언
                                            value.asJsonPrimitive.isString -> {
                                                val stringValue = value.asString
                                                if (stringValue.matches(Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))) { // 날짜 형식이면 Date로 변환
                                                    dateFormat.parse(stringValue)
                                                } else {
                                                    stringValue // 일반 문자열
                                                }
                                            }
                                            else -> value.toString()
                                        }
                                    }
                                    value.isJsonArray -> "Array(${value.asJsonArray.size()})" // 배열 크기 출력
                                    value.isJsonObject -> "Object" // JSON 객체 표시
                                    else -> "Unknown"
                                }
                                 */

                                when(key){
                                    arrIdentity[0] -> {
                                        importProductKey.infoType = value.asInt
                                    }
                                    arrIdentity[1] -> {
                                        importProductKey.providerName = value.asString
                                    }
                                    arrIdentity[2] -> {
                                        val stringValue:String = value.asString
                                        if (stringValue.matches(Regex(
                                                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                            ))) { // 날짜 형식 변환
                                            importProductKey.createAt = dateFormat.parse(stringValue)
                                        }
                                    }
                                    arrIdentity[3] -> {
                                        val stringValue:String = value.asString
                                        if (stringValue.matches(Regex(
                                                "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"
                                            ))) { // 날짜 형식 변환
                                            importProductKey.lastUpdate = dateFormat.parse(stringValue)
                                        }
                                    }
                                    arrIdentity[4] -> {
                                        importProductKey.userMemo = value.asString
                                    }
                                    arrIdentity[5] -> {
                                        importProductKey.tagColor = value.asString
                                    }
                                    arrAccount[0] -> {
                                        importProductKey.productKey = value.asString
                                    }
                                    arrProduct[1] -> {
                                        importProductKey.offUrl = value.asString
                                    }
                                }
                            }

                            lifecycleScope.launch(Dispatchers.IO) {
                                identityInfoViewModel.addImportProductKey(importProductKey)
                            }
                            // delay(delayGap)
                            Thread.sleep(delayGap)
                        }

                    }

                    override fun onFileReadFail() {
                        // Fail to import data...
                        dialog.notifyFinishTask()
                    }

                })
            }

            // dialog.notifyFinishTask(100)
        }
    }

    // Dialog Listener

    // 데이터 불러오가 리스너
    private val importDialogListener = object : ImportDataDialog.OnDialogClickListener {
        override fun onImport(fileName: String) {
            val titleDataImport:String = getString(R.string.title_data_import)
            val txtStop:String = getString(R.string.btn_stop)
            val alertInfo = AlertInfo(titleDataImport, txtOk = txtStop, flag = true)
            val dialog = ProgressDialog(
                alertInfo, object : ProgressDialog.OnProgressListener {
                    // 사용자에 의한 작업 중단 요청
                    override fun onAction(view: View) {

                    }
                }
            )
            dialog.show(requireActivity().supportFragmentManager, null)
            startImportData(fileName, dialog)
        }

        override fun onCancel() {

        }
    }

    // 데이터 내보내기 리스너
    private val exportDialogListener = object :AlertDialog.OnDialogClickListener{
        override fun onOk(view: View) {
            when(view.id){
                R.id.btn_ok -> {
                    val titleDataExport:String = getString(R.string.title_data_export)
                    val txtStop:String = getString(R.string.btn_stop)
                    val alertInfo = AlertInfo(titleDataExport, txtOk = txtStop, flag = true)
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