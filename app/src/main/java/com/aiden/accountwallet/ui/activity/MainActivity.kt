package com.aiden.accountwallet.ui.activity

import android.os.Bundle
import com.aiden.accountwallet.R
import com.aiden.accountwallet.BR
import com.aiden.accountwallet.base.bind.DataBindingConfig
import com.aiden.accountwallet.base.ui.BaseActivity
import com.aiden.accountwallet.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_main)
    }

    override fun init(savedInstanceState: Bundle?) {

    }
}