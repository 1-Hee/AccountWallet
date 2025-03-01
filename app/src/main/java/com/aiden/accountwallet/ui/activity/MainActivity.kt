package com.aiden.accountwallet.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.aiden.accountwallet.R
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.ui.BaseActivity
import com.aiden.accountwallet.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    // variables
    // 세팅 액티비티 팝업용 런처
    private lateinit var navController: NavController
    private lateinit var launcher: ActivityResultLauncher<Intent>;

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_main)
    }

    override fun init(savedInstanceState: Bundle?) {
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

        // set up navcontroller
        navController = findNavController(R.id.nav_host_fragment_content_main)
        setSupportActionBar(mBinding.mainAppBar.toolbar)

        //admob init
        MobileAds.initialize(this) {}

    }

    // 액션 바 메뉴
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_app_bar, menu)
        return true
    }

    // 액션 바 메뉴 리스너
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        val id = item.itemId
        return when(id) {
            R.id.action_settings -> {
                val navController = (supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment).navController
                navController.navigate(R.id.settingFragment)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}