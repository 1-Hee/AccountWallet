package com.aiden.accountwallet.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiden.accountwallet.data.model.ProductKey

@Dao
interface ProductKeyDao {

    // Create
    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addProductKey(productKey : ProductKey) : Long

    @Insert(onConflict = OnConflictStrategy.NONE)
    fun addAllProductKey(vararg productKey : ProductKey)

    // Read
    @Query("""
        SELECT 
            pi.product_id, pi.fk_info_id, pi.product_key, pi.official_url
        FROM identity_info ii
        JOIN PRODUCT_KEY pi 
        ON ii.info_id = pi.fk_info_id
        WHERE ii.status = 0
        ORDER BY ii.created_at DESC;
    """)
    fun readProductKeyList(): List<ProductKey>

    // Update
    @Update
    fun modifyProductKey(productKey : ProductKey)

    // Delete
    @Query(
        """
        UPDATE identity_info
        SET status = 1
        WHERE info_id IN (
            SELECT fk_info_id FROM product_key
            WHERE product_id = :productId
        ); 
        """
    )
    fun removeProductKey(productId: Long)

    @Delete
    fun removeProductKey(productKey : ProductKey)

    @Delete
    fun removeProductKeyList(vararg productKey : ProductKey)

    // delete all
    //    @Query("DELETE FROM sqlite_sequence where name='table_task_registration'")
    //    fun clearAll()

    @Query("DELETE FROM product_key")
    fun deleteAll()
}