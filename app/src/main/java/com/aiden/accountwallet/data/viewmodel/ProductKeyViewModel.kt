package com.aiden.accountwallet.data.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import com.aiden.accountwallet.base.repository.ExtraEntityHandler
import com.aiden.accountwallet.base.viewmodel.BaseRoomViewModel
import com.aiden.accountwallet.base.viewmodel.SubEntityHandler
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.data.repository.ProductKeyRepository
import com.aiden.accountwallet.util.Logger
import com.aiden.accountwallet.util.RoomTool.checkInstance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductKeyViewModel @Inject constructor (
    override val repository: ProductKeyRepository
) :  BaseRoomViewModel<ProductKey>(), SubEntityHandler<IdProductKey> {

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    override val extraEntity: ObservableField<IdProductKey> = ObservableField()
    override val extraEntityList: ObservableArrayList<IdProductKey> = ObservableArrayList()

    // * ----------------------------------------
    // *        Sync Task API
    // * ----------------------------------------
    override suspend fun addEntity(entity: ProductKey): Long {
        Logger.d("vm addEntity %s", entity)
        val result = repository.addEntity(entity)
        this@ProductKeyViewModel.setAddEntityStatus(result)
        return result
    }

    override suspend fun readEntityList(): List<ProductKey> {
        val list:List<ProductKey> = repository.readEntityList()
        Logger.d("vm readEntityList : %s", list)
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
        Logger.d("vm readExtraEntity id : %d", entityId)
        val entity: IdProductKey = (repository as ExtraEntityHandler<IdProductKey>)
            .readExtraEntity(entityId)
        Logger.d("vm readExtraEntity : %s", entity)
        this@ProductKeyViewModel.extraEntity.set(entity)
        return entity
    }

    override suspend fun readExtraEntityList(): List<IdProductKey> {
        return emptyList()
    }

    override suspend fun editEntity(entity: ProductKey) {
        Logger.d("vm editEntity : %s", entity)
        repository.modifyEntity(entity)
    }

    override suspend fun removeEntity(entityId: Long) {
        Logger.d("vm removeEntity (id) : %s", entityId)
        repository.deleteEntity(entityId)
    }

    override suspend fun removeAll() {

    }

    override suspend fun removeEntity(entity: ProductKey) {
        Logger.d("vm removeEntity  : %s", entity)
        repository.deleteEntity(entity.productId)
    }


    // * ----------------------------------------
    // *        Async Task API
    // * ----------------------------------------

    override fun addAsyncEntity(entity: ProductKey) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm addEntity %s", entity)
            val result = repository.addEntity(entity)
            this@ProductKeyViewModel.setAddEntityStatus(result)
        }
    }

    override fun readAsyncEntityList() {
        viewModelScope.launch(Dispatchers.IO) {
            val list:List<ProductKey> = repository.readEntityList()
            Logger.d("vm readEntityList : %s", list)
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
            Logger.d("vm readExtraEntity id : %d", entityId)
            val entity: IdProductKey = (repository as ExtraEntityHandler<IdProductKey>)
                .readExtraEntity(entityId)
            Logger.d("vm readExtraEntity : %s", entity)
            this@ProductKeyViewModel.extraEntity.set(entity)
        }
    }

    override fun readAsyncExtraEntityList() {

    }


    override fun editAsyncEntity(entity: ProductKey) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm editEntity : %s", entity)
            repository.modifyEntity(entity)
        }
    }

    override fun removeAsyncEntity(entityId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeEntity (id) : %s", entityId)
            repository.deleteEntity(entityId)
        }
    }

    override fun removeAsyncEntity(entity: ProductKey) {
        viewModelScope.launch(Dispatchers.IO) {
            Logger.d("vm removeEntity  : %s", entity)
            repository.deleteEntity(entity.productId)
        }
    }

    override fun removeAsyncAll() {

    }


}