package com.aiden.accountwallet.base.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField

interface SubEntityHandler<T> {

    // * ----------------------------------------
    // *        Variables
    // * ----------------------------------------
    val extraEntity: ObservableField<T>
    val extraEntityList: ObservableArrayList<T>

    // * ----------------------------------------
    // *        Extra API...
    // * ----------------------------------------
    suspend fun readExtraEntity(entityId: Long): T
    suspend fun readExtraEntityList(): List<T>
    fun readAsyncExtraEntity(entityId: Long)
    fun readAsyncExtraEntityList()
}