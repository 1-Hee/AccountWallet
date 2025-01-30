package com.aiden.accountwallet.base.repository

abstract class BaseRoomRepository<T> {

    // Create
    abstract suspend fun addEntity(entity:T): Long
    // Read
    abstract suspend fun readEntity(entityId: Long): T
    abstract suspend fun readEntityList(): List<T>

    // Update
    abstract suspend fun modifyEntity(entity: T)
    // Delete
    abstract suspend fun deleteEntity(entityId: Long)
    abstract suspend fun deleteEntity(entity: T)

}