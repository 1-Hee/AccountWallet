package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.aiden.accountwallet.data.repository.BaseRoomRepository
import timber.log.Timber

abstract class BaseRoomViewModel<T>(
    application: Application
) : AndroidViewModel(application) {

    abstract val repository: BaseRoomRepository<T>

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    val addStatus: MutableLiveData<Long> = MutableLiveData<Long>(0)
    val entityList: ObservableArrayList<T> = ObservableArrayList<T>()
    val isEntityEmpty: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    // Create
    abstract suspend fun addEntity(entity: T):Long
    // Read
    abstract suspend fun readEntity():List<T>
    // Update
    abstract suspend fun editEntity(entity: T)

    // Delete
    abstract suspend fun removeEntity(entityId:Long)
    abstract suspend fun removeEntity(entity: T)

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    // Create
    abstract fun addAsyncEntity(entity:T)
    // Read
    abstract fun readAsyncEntity()
    // Update
    abstract fun editAsyncEntity(entity: T)

    // Delete
    abstract fun removeAsyncEntity(entityId:Long)
    abstract fun removeAsyncEntity(entity: T)

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