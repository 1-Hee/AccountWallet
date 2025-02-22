package com.aiden.accountwallet.data.db

import android.content.Context
import com.aiden.accountwallet.data.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule  {

    @Provides
    fun provideUserInfoDao(database: AppDataBase): UserInfoDao {
        return database.getUserInfoDao()
    }

    @Provides
    fun provideIdentityInfoDao(database: AppDataBase): IdentityInfoDao {
        return database.getIdentityInfoDao()
    }

    @Provides
    fun provideAccountInfoDao(database: AppDataBase): AccountInfoDao {
        return database.getAccountInfoDao()
    }

    @Provides
    fun provideProductKeyDao(database: AppDataBase): ProductKeyDao {
        return database.getProductKeyDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase {
        return AppDataBase.getInstance(context)
    }
}