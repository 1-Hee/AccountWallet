package com.aiden.accountwallet.ui.viewmodel

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel


class InfoTypeViewModel : ViewModel() {

    // * ------------------------------------------------
    // *    Variables
    // * ------------------------------------------------
    val selectedType:ObservableField<Int> = ObservableField(0)
    private val infoTypes: ObservableArrayList<String> = ObservableArrayList()
    var infoTypeAdapter:ArrayAdapter<String>? = null
    private var callback: InfoTypeCallback? = null

    fun initInfoTypes(items:Array<String>, infoTypeAdapter:ArrayAdapter<String>){
        infoTypes.clear()
        infoTypes.addAll(items)
        this.infoTypeAdapter = infoTypeAdapter
    }

    // * ------------------------------------------------
    // *    ViewModel's Callback
    // * ------------------------------------------------
    interface InfoTypeCallback {
        fun onItemSelected(selectedItem: String?, position: Int)
    }

    // * ------------------------------------------------
    // *    ViewModel's Setter
    // * ------------------------------------------------
    fun setCallback(callback: InfoTypeCallback?) {
        this.callback = callback
    }

    fun setSelectedType(typeIdx:Int) {
        this.selectedType.set(typeIdx)
    }

    // * ------------------------------------------------
    // *    ViewModel's  Listener
    // * ------------------------------------------------
    // Spinner의 onItemSelected 이벤트 처리
    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (callback != null) {
            val selectedItem: String = infoTypes[position]
            callback?.onItemSelected(selectedItem, position)
        }
    }

}