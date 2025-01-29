package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.data.dao.ProductKeyDao
import com.aiden.accountwallet.data.model.ProductKey
import timber.log.Timber

class ProductKeyRepository(
    private val productKeyDao : ProductKeyDao
) : BaseRoomRepository<ProductKey>() {

    override suspend fun addEntity(entity: ProductKey): Long {
        Timber.d("repo addEntity : %s ", entity)
        val result = productKeyDao.addProductKey(entity)
        return result

    }

    override suspend fun readEntity(): List<ProductKey> {
        val list = productKeyDao.readProductKeyList()
        Timber.d("repo eadEntity : %s ", list)
        return list
    }

    override suspend fun modifyEntity(entity: ProductKey) {
        Timber.d("repo modifyEntity : %s", entity)
        productKeyDao.modifyProductKey(entity)
    }


    override suspend fun deleteEntity(entityId: Long) {
        Timber.d("repo deleteEntity (id) : %s", entityId)
        productKeyDao.removeProductKey(entityId)
    }

    override suspend fun deleteEntity(entity: ProductKey) {
        Timber.d("repo deleteEntity : %s", entity)
        productKeyDao.removeProductKey(entity.productId)
    }

}