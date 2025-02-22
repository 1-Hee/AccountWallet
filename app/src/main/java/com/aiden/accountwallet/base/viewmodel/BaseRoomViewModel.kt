package com.aiden.accountwallet.base.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aiden.accountwallet.base.repository.BaseRoomRepository
import timber.log.Timber

abstract class BaseRoomViewModel<T> : ViewModel() {

    abstract val repository: BaseRoomRepository<T>

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    val addStatus: MutableLiveData<Long> = MutableLiveData<Long>(0)
    val entity:ObservableField<T> = ObservableField()
    val entityList: ObservableArrayList<T> = ObservableArrayList<T>()
    val isEntityEmpty: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

    fun initVariables() {
        this.addStatus.postValue(0)
        this.entityList.clear()
        this.isEntityEmpty.postValue(true)
    }

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    // Create
    abstract suspend fun addEntity(entity: T):Long
    // Read
    abstract suspend fun readEntityList():List<T>
    abstract suspend fun readEntity(entityId:Long):T

    // Update
    abstract suspend fun editEntity(entity: T)

    // Delete
    abstract suspend fun removeEntity(entityId:Long)
    abstract suspend fun removeEntity(entity: T)
    abstract suspend fun removeAll()

    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    // Create
    abstract fun addAsyncEntity(entity:T)

    // Read
    abstract fun readAsyncEntityList()
    abstract fun readAsyncEntity(entityId:Long)

    // Update
    abstract fun editAsyncEntity(entity: T)

    // Delete
    abstract fun removeAsyncEntity(entityId:Long)
    abstract fun removeAsyncEntity(entity: T)
    abstract fun removeAsyncAll()

    fun setAddEntityStatus(result:Long) {
        this.addStatus.postValue(result)
        Timber.d("vm.. addEntity result : %s", result)
    }

    fun addEntityList(entityList: List<T>) {
        if(this.entityList.isNotEmpty()){
            this.entityList.clear()
        }
        this.entityList.addAll(entityList)
        this.isEntityEmpty.postValue(entityList.isEmpty())
    }

}