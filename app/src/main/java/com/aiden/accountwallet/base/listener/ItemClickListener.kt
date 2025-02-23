package com.aiden.accountwallet.base.listener

import android.view.View

interface ItemClickListener<T> {
    fun onItemClick(view: View, position:Int, item : T)
}