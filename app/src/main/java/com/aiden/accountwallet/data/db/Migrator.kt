package com.aiden.accountwallet.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrator {

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // product_key 테이블 생성
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS product_key (
                product_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                fk_info_id INTEGER NOT NULL,
                provider_name TEXT NOT NULL DEFAULT '',
                product_key TEXT NOT NULL DEFAULT '',
                official_url TEXT NOT NULL DEFAULT '',
                FOREIGN KEY(fk_info_id) REFERENCES identity_info(info_id) ON DELETE SET NULL
            )
            """.trimIndent()
            )

            // fk_info_id 컬럼에 인덱스 추가
            database.execSQL("CREATE INDEX IF NOT EXISTS idx_fk_info_id ON product_key(fk_info_id);")
        }
    }

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // product_key 테이블 생성
            database.execSQL(
                """
            CREATE TABLE IF NOT EXISTS product_key (
                product_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                fk_info_id INTEGER,
                provider_name TEXT NOT NULL DEFAULT '',
                product_key TEXT NOT NULL DEFAULT '',
                official_url TEXT NOT NULL DEFAULT '',
                FOREIGN KEY(fk_info_id) REFERENCES identity_info(info_id) ON DELETE SET NULL
            )
            """.trimIndent()
            )

            // fk_info_id 컬럼에 인덱스 추가
            database.execSQL("CREATE INDEX IF NOT EXISTS idx_fk_info_id ON product_key(fk_info_id);")
        }
    }

}