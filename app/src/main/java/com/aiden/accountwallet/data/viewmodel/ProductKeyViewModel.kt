package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.data.db.AppDataBase
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.repository.BaseRoomRepository
import com.aiden.accountwallet.data.repository.ProductKeyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductKeyViewModel (
    application: Application
) :  BaseRoomViewModel<ProductKey>(application) {

    override val repository: BaseRoomRepository<ProductKey>
    init {
        val productKeyDao = AppDataBase
            .getInstance(application.applicationContext)
            .getProductKeyDao()
        repository = ProductKeyRepository(productKeyDao)
    }

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------


    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------

    override suspend fun addEntity(entity: ProductKey): Long {
        Timber.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@ProductKeyViewModel.setAddEntityStatus(result)
        return result
    }

    override suspend fun readEntity(): List<ProductKey> {
        val list = repository.readEntity()
        Timber.d("vm readEntity : %s", list)
        this@ProductKeyViewModel.addEntityList(list)
        return list
    }

    /*
    suspend fun readIdAccountInfoList():List<IdAccountInfo> {
        val idList = repository.readEntity()
        Timber.d("vm readAccountInfoList : ${idList.toString()}")
        return idList
    }
     */

    override suspend fun editEntity(entity: ProductKey) {
        Timber.d("vm editEntity : %s", entity)
        repository.modifyEntity(entity)
    }

    override suspend fun removeEntity(entityId: Long) {
        Timber.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeEntity(entity: ProductKey) {
        Timber.d("vm removeEntity  : %s", entity)
        repository.deleteEntity(entity.productId)
    }


    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    override fun addAsyncEntity(entity: ProductKey) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm addEntity %s", entity)
            val result = repository.addEntity(entity)
            this@ProductKeyViewModel.setAddEntityStatus(result)
        }
    }

    override fun readAsyncEntity() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.readEntity()
            Timber.d("vm readEntity : %s", list)
            this@ProductKeyViewModel.addEntityList(list)
        }
    }

    /*
    fun readIdAccountInfoList():List<IdAccountInfo> {
        val idList = repository.readEntity()
        Timber.d("vm readAccountInfoList : ${idList.toString()}")
        return idList
    }
    */

    override fun editAsyncEntity(entity: ProductKey) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm editEntity : %s", entity)
            repository.modifyEntity(entity)
        }
    }

    override fun removeAsyncEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeEntity (id) : %s", entityId)
            repository.deleteEntity(entityId)
        }
    }

    override fun removeAsyncEntity(entity: ProductKey) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.d("vm removeEntity  : %s", entity)
            repository.deleteEntity(entity.productId)
        }
    }

}