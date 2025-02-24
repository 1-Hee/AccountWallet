package com.aiden.accountwallet.ui.viewmodel

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ListAccountViewModel : ViewModel() {

    // * --------------------------------------------------------
    // *    Variables
    // * --------------------------------------------------------
    // 검색어 (문자열)
    private val _searchQuery = ObservableField("")  // 내부 저장용

    // 체크 여부 (불리언 값)
    val _isChecked =	MutableLiveData<Boolean>();

    // 정렬 옵션 (문자열 프리셋)
    private val _sortOption = MutableLiveData("")
    val sortOptionList = ObservableArrayList<String>()

    var sortOptionAdapter:ArrayAdapter<String>? = null
    private var sortCallback: SortOptionCallback? = null


    // * --------------------------------------------------------
    // *    Getter  /   Setter
    // * --------------------------------------------------------
    val searchQuery: String
        get() = _searchQuery.get() ?: ""  // null 방지
    fun setSearchQuery(value: String) {
        _searchQuery.set(value)
    }

    val isChecked: LiveData<Boolean> get() = _isChecked
    fun setIsChecked(value: Boolean) {
        _isChecked.value = value
    }

    val sortOption: LiveData<String> get() = _sortOption
    fun setSortOption(value: String) {
        _sortOption.value = value
    }

    fun initSortOptions(items:Array<String>, adapter:ArrayAdapter<String>){
        setSortOption("")
        sortOptionList.clear()
        sortOptionList.addAll(items)
        this.sortOptionAdapter = adapter
    }

    fun setSortCallback(callback: SortOptionCallback?) {
        this.sortCallback = callback
    }


    // * ------------------------------------------------
    // *    ViewModel's Callback
    // * ------------------------------------------------
    interface SortOptionCallback {
        fun onItemSelected(selectedItem: String?, position: Int)
    }


    // * ------------------------------------------------
    // *    ViewModel's  Listener
    // * ------------------------------------------------
    // Spinner의 onItemSelected 이벤트 처리
    fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (sortCallback != null) {
            val selectedItem: String = sortOptionList[position]
            sortCallback?.onItemSelected(selectedItem, position)
        }
    }


}