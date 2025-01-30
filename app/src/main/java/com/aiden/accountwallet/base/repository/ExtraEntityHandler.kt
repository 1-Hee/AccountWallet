package com.aiden.accountwallet.base.repository

interface ExtraEntityHandler<T> {

    // Read
    suspend fun readExtraEntity(entityId: Long): T
    suspend fun readExtraEntityList(): List<T>
}