package com.codewithharsh.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query



@Dao
interface CartDao {

    @Insert()
     fun insertOrder(cartEntity: CartEntity)

    @Delete
     fun deleteOrders(cartEntity: CartEntity)

    @Query("SELECT *  FROM orders")
     fun getAllOrders(): List<CartEntity>

    @Query("DELETE FROM orders WHERE resId = :resId")
     fun deleteOrders(resId: String)

}