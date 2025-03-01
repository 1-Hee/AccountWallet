package com.aiden.accountwallet.data.repository

import com.aiden.accountwallet.base.repository.BaseRoomRepository
import com.aiden.accountwallet.base.repository.ExtraEntityHandler
import com.aiden.accountwallet.data.dao.ProductKeyDao
import com.aiden.accountwallet.data.model.IdProductKey
import com.aiden.accountwallet.data.model.ProductKey
import com.aiden.accountwallet.util.Logger
import javax.inject.Inject

class ProductKeyRepository @Inject constructor(
    private val productKeyDao : ProductKeyDao
) : BaseRoomRepository<ProductKey>(), ExtraEntityHandler<IdProductKey> {

    override suspend fun addEntity(entity: ProductKey): Long {
        Logger.d("repo addEntity : %s ", entity)
        val result = productKeyDao.addProductKey(entity)
        return result

    }

    override suspend fun readEntity(entityId: Long): ProductKey {
        return ProductKey(fkInfoId = -1)
    }

    override suspend fun readEntityList(): List<ProductKey> {
        val list:List<ProductKey> = productKeyDao.readProductKeyList()
        Logger.d("repo eadEntityList : %s ", list)
        return list
    }

    override suspend fun readExtraEntity(entityId: Long): IdProductKey {
        Logger.d("repo readCombineEntity id : %d", entityId)
        val entity: IdProductKey = productKeyDao.readIdProductKeyById(entityId)
        Logger.d("repo readCombineEntity : %s", entity)
        return entity
    }

    override suspend fun readExtraEntityList(): List<IdProductKey> {
        return emptyList()
    }

    override suspend fun modifyEntity(entity: ProductKey) {
        Logger.d("repo modifyEntity : %s", entity)
        val productId:Long = entity.productId
        val fkInfoId:Long? = entity.fkInfoId
        val productKey:String = entity.productKey
        val officialUrl: String = entity.officialUrl

        this.productKeyDao.modifyProductKey(
            productId, fkInfoId, productKey, officialUrl
        )
        // productKeyDao.modifyProductKey(entity)
    }

    override suspend fun deleteEntity(entityId: Long) {
        Logger.d("repo deleteEntity (id) : %s", entityId)
        productKeyDao.removeProductKey(entityId)
    }

    override suspend fun deleteEntity(entity: ProductKey) {
        Logger.d("repo deleteEntity : %s", entity)
        productKeyDao.removeProductKey(entity.productId)
    }

    override suspend fun deleteAll() {
    }


}