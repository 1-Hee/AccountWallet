package com.aiden.accountwallet.data.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.data.db.AppDataBase
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.base.repository.ExtraEntityHandler
import com.aiden.accountwallet.base.viewmodel.ExtraViewModel
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.repository.ProductKeyRepository
import com.aiden.accountwallet.util.RoomTool.checkInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductKeyViewModel (
    application: Application
) :  BaseRoomViewModel<ProductKey>(application), ExtraViewModel<IdProductKey> {

    // db init
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
    override val extraEntity: ObservableField<IdProductKey> = ObservableField()
    override val extraEntityList: ObservableArrayList<IdProductKey> = ObservableArrayList()

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------
    override suspend fun addEntity(entity: ProductKey): Long {
        Timber.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@ProductKeyViewModel.setAddEntityStatus(result)
        return result
    }

    override suspend fun readEntityList(): List<ProductKey> {
        val list:List<ProductKey> = repository.readEntityList()
        Timber.d("vm readEntityList : %s", list)
        this@ProductKeyViewModel.addEntityList(list)
        return list
    }

    override suspend fun readEntity(entityId: Long): ProductKey {
        return ProductKey(fkInfoId = -1)
    }

    override suspend fun readExtraEntity(entityId: Long): IdProductKey {
        if(!checkInstance<ExtraEntityHandler<IdProductKey>>(repository)){
            throw RuntimeException("Invalid Repository Casting")
        }
        Timber.d("vm readExtraEntity id : %d", entityId)
        val entity: IdProductKey = (repository as ExtraEntityHandler<IdProductKey>)
            .readExtraEntity(entityId)
        Timber.d("vm readExtraEntity : %s", entity)
        this@ProductKeyViewModel.extraEntity.set(entity)
        return entity
    }

    override suspend fun readExtraEntityList(): List<IdProductKey> {
        return emptyList()
    }

    override suspend fun editEntity(entity: ProductKey) {
        Timber.d("vm editEntity : %s", entity)
        repository.modifyEntity(entity)
    }

    override suspend fun removeEntity(entityId: Long) {
        Timber.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeAll() {

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

    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list:List<ProductKey> = repository.readEntityList()
            Timber.d("vm readEntityList : %s", list)
            this@ProductKeyViewModel.addEntityList(list)
        }
    }

    override fun readAsyncEntity(entityId: Long) {

    }

    override fun readAsyncExtraEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!checkInstance<ExtraEntityHandler<IdProductKey>>(repository)){
                throw RuntimeException("Invalid Repository Casting")
            }
            Timber.d("vm readExtraEntity id : %d", entityId)
            val entity: IdProductKey = (repository as ExtraEntityHandler<IdProductKey>)
                .readExtraEntity(entityId)
            Timber.d("vm readExtraEntity : %s", entity)
            this@ProductKeyViewModel.extraEntity.set(entity)
        }
    }

    override fun readAsyncExtraEntityList() {

    }


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

    override fun removeAsyncAll() {

    }


}