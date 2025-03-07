package com.aiden.accountwallet.ui.fragment

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
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
import com.aiden.accountwallet.base.listener.StopTaskListener
import com.aiden.accountwallet.base.listener.ViewClickListener
import com.aiden.accountwallet.base.ui.BaseFragment
import com.aiden.accountwallet.data.dto.AlertInfo
import com.aiden.accountwallet.data.dto.Permission
import com.aiden.accountwallet.data.dto.SettingItem
import com.aiden.accountwallet.data.model.IdAccountInfo
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.viewmodel.IdentityInfoViewModel
import com.aiden.accountwallet.data.viewmodel.UserInfoViewModel
import com.aiden.accountwallet.data.vo.ImportProductKey
import com.aiden.accountwallet.data.vo.ImportUserAccount
import com.aiden.accountwallet.databinding.FragmentSettingBinding
import com.aiden.accountwallet.ui.dialog.AlertDialog
import com.aiden.accountwallet.ui.dialog.ImportDataDialog
import com.aiden.accountwallet.ui.dialog.ProgressDialog
import com.aiden.accountwallet.ui.viewmodel.SettingViewModel
import com.aiden.accountwallet.util.AppJsonParser
import com.aiden.accountwallet.util.FileManager
import com.aiden.accountwallet.util.Logger
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
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

    // vm
    private lateinit var settingViewModel: SettingViewModel

    // dialog instance
    private var importDataDialog:ImportDataDialog? = null

    // 전면 광고 런처
    private var mInterstitialAd: InterstitialAd? = null

    override fun initViewModel() {
        userInfoViewModel = getApplicationScopeViewModel(UserInfoViewModel::class.java)
        identityInfoViewModel = getApplicationScopeViewModel(IdentityInfoViewModel::class.java)
        settingViewModel = getFragmentScopeViewModel(SettingViewModel::class.java)
        settingViewModel.initVariables()
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
        // 광고 준비
        loadInterstitialAd()
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
                    val btnCancel:String = getString(R.string.btn_cancel)
                    val alertInfo = AlertInfo(
                        titleDataImport,
                        txtOk = btnImport,
                        txtCancel = btnCancel
                    )
                    val dialog = ImportDataDialog (
                        alertInfo, importDialogListener, importStopListener
                    )
                    this@SettingFragment.importDataDialog = dialog
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
        // var currentProgress = 0;
        val finView:View = mBinding.spBottom
        settingViewModel.setIsStopExportTask(false)

        lifecycleScope.launch(Dispatchers.IO) {
            while(!(dialog.getIsInitView())){ // 준비 될때까지 대기!
                delay(200)
            }
            dialog.setDialogStatus(msgLoadAccount)

            // 계정 준비 작업
            val accountList:List<IdAccountInfo> = identityInfoViewModel.readAllAccountList()
            delay(100)

            // 제품키 준비 작업
            dialog.setDialogStatus(msgLoadProduct)

            val productList:List<IdProductKey> = identityInfoViewModel.readAllProductList()
            delay(100)

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

                    override fun isStopSaveTask(): Boolean {
                        return settingViewModel.isStopExportTask.get()
                    }

                    override fun onFileSaveFail() {
                        val finMsg:String = getString(R.string.status_fail_export_data)
                        Snackbar.make(finView, finMsg, Snackbar.LENGTH_LONG).show()
                        // dialog.notifyFinishTask()
                    }
                }
            )
        }
    }

    // 데이터 불러오기
    private fun startImportData(fileUri:Uri?, dialog: ImportDataDialog?, ){
        lifecycleScope.launch(Dispatchers.Main) {
            val finView:View = mBinding.spBottom
            if (fileUri == null || dialog == null){
                val snackMsg:String = getString(R.string.msg_occur_err_import_data)
                Snackbar.make(finView, snackMsg, Snackbar.LENGTH_LONG).show()
                return@launch
            }

            val context:Context = requireContext()
            val msgPrepareImport:String = getString(R.string.load_backup_data)
            var currentProgress = 0;

            dialog.setDialogStatus(msgPrepareImport)
            dialog.setDialogProgress(currentProgress)

            // data... read ..
            val resolver:ContentResolver = requireActivity().contentResolver

            val jsonStr:String
            withContext(Dispatchers.IO){
                jsonStr = FileManager.readJsonFromUri(resolver, fileUri)
            }
            currentProgress += 10
            dialog.setDialogProgress(currentProgress)

            AppJsonParser.parseJsonData(context, jsonStr,
                importAccountListener,
                importProductListener,
                object : AppJsonParser.ParseListener {
                    override fun onUpdateStatus(status: String) {
                        dialog.setDialogStatus(status)
                    }
                    override fun onUpdateProgress(progress: Int) {
                        dialog.setDialogProgress(progress)
                    }

                    override fun isStopTask(): Boolean {
                        return settingViewModel.isStopImportTask.get()
                    }

                    override fun onParseFail() {

                    }
                    override fun onParseFinish() {
                        val finMsg:String = getString(R.string.status_success_load_data)
                        dialog.setDialogStatus(finMsg)
                        dialog.notifyFinishTask(200)

                        if(!(settingViewModel.isStopImportTask.get())){
                            val handler = Handler(Looper.getMainLooper())
                            val delayedTask = Runnable {
                                Snackbar.make(finView, finMsg, Snackbar.LENGTH_LONG).show()
                            }
                            handler.postDelayed(delayedTask, 100) // 300ms 뒤 실행

                            val adDelayedTask = Runnable {
                                requestAdScreen()
                            }
                            handler.postDelayed(adDelayedTask, 500) // 500ms 뒤 실행
                        }
                    }
                }
            )
        }
    }

    // Dialog Listener
    private val importAccountListener =  object : AppJsonParser.EntityImportHandler<ImportUserAccount> {
        override fun onRequest(entity: ImportUserAccount) {
            lifecycleScope.launch(Dispatchers.IO) {
                identityInfoViewModel.addImportUserAccount(entity)
            }
        }
    }

    private val importProductListener =  object : AppJsonParser.EntityImportHandler<ImportProductKey> {
        override fun onRequest(entity: ImportProductKey) {
            lifecycleScope.launch(Dispatchers.IO) {
                identityInfoViewModel.addImportProductKey(entity)
            }
        }
    }

    // 데이터 불러오기 리스너
    private val importStopListener = object : StopTaskListener {
        override fun onRequestStop(flag: Boolean) {
            settingViewModel.setIsStopImportTask(flag)
        }
    }

    private val importDialogListener = object : ImportDataDialog.OnDialogClickListener {
        override fun onImport(fileUri:Uri?) {
            startImportData(fileUri, this@SettingFragment.importDataDialog)
        }
        override fun onCancel(view:View) {
            val finView:View = requireView().findViewById(R.id.sp_bottom)
            val finMsg:String = getString(R.string.status_fail_load_data)
            Snackbar.make(finView, finMsg, Snackbar.LENGTH_LONG).show()
            importDataDialog?.notifyFinishTask(300)
        }
    }

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
                                settingViewModel.setIsStopExportTask(true)
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

    // * ----------------------------------------------------------------------------
    // *    광고 호출을 위한 함수
    // * ----------------------------------------------------------------------------
    // 광고 호출 함수
    private fun requestAdScreen() {
        if(mInterstitialAd != null){
            Logger.i("mInterstitialAd null 아님!!!")
            mInterstitialAd?.show(requireActivity())
        }else {
            Logger.i("mInterstitialAd null....!!!!!!!!!!")
        }
    }

    // 광로 로드
    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(requireActivity(), BuildConfig.ADMOB_SCREEN_SDK_KEY, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Logger.e("%s", adError.toString())
                mInterstitialAd = null
            }
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Logger.d("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        // 광고 호출 콜백
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Logger.i("Ad was clicked...!!!!!!!!!!!")
            }
            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Logger.i("Ad dismissed fullscreen content...!!!!!!!!!")
                mInterstitialAd = null
            }
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // Called when ad fails to show.
                Logger.e("Ad failed to show fullscreen content...!!!!!!!!")
                mInterstitialAd = null
            }
            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Logger.e("Ad recorded an impression...!!!!!!!!!!!")
            }
            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Logger.e("Ad showed fullscreen content...!!!!!!!!!!!!!")
            }
        }
    }
}