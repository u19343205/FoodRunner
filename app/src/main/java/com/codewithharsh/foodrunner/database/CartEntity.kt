package com.codewithharsh.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class CartEntity(

    @PrimaryKey val resId: String,
    @ColumnInfo (name = "food_Items")  val foodItems: String
//    @PrimaryKey val item_id: Int,
//    @ColumnInfo(name = "item_name") val itemName: String,
//    @ColumnInfo(name = "item_price") val itemPrice: String
)