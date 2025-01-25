package com.aiden.accountwallet.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aiden.accountwallet.data.dao.IdentityInfoDao
import com.aiden.accountwallet.data.dao.UserInfoDao
import com.aiden.accountwallet.data.model.IdentityInfo
import com.aiden.accountwallet.data.model.UserInfo

@Database(entities = [ UserInfo::class, IdentityInfo::class ],
    version = 1, exportSchema = false
)
@TypeConverters(AppConverter::class)
abstract class AppDataBase : RoomDatabase(){
    // dao
    abstract fun getUserInfoDao(): UserInfoDao
    abstract fun getIdentityInfoDao(): IdentityInfoDao

    companion object {
        @Volatile
        private var INSTANCE:AppDataBase? = null
        fun getInstance(context: Context):AppDataBase{
            // context.deleteDatabase("task_data_base")
            if(INSTANCE==null){
                synchronized(this){
                    val instance = Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDataBase::class.java,
                            "account_wallet_data_base"
                        ).build()
                    INSTANCE = instance
                    return instance
                }
            }else {
                return INSTANCE!!
            }
        }
    }
}