package com.aiden.accountwallet.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aiden.accountwallet.data.dao.*
import com.aiden.accountwallet.data.model.*

@Database(entities = [
    UserInfo::class, IdentityInfo::class,
    AccountInfo::class, ProductKey::class ],
    version = 1, exportSchema = false
)
@TypeConverters(AppConverter::class)
abstract class AppDataBase : RoomDatabase(){
    // dao
    abstract fun getUserInfoDao(): UserInfoDao
    abstract fun getIdentityInfoDao(): IdentityInfoDao
    abstract fun getAccountInfoDao(): AccountInfoDao
    abstract fun getProductKeyDao(): ProductKeyDao


    companion object {
        const val DB_NAME:String = "account_wallet_data_base"

        @Volatile
        private var INSTANCE:AppDataBase? = null
        fun getInstance(context: Context):AppDataBase{
            // context.deleteDatabase(DB_NAME)
            if(INSTANCE==null){
                synchronized(this){
                    val instance = Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDataBase::class.java,
                            DB_NAME
                        )
                        .build()
                    INSTANCE = instance
                    return instance
                }
            }else {
                return INSTANCE!!
            }
        }
    }
}