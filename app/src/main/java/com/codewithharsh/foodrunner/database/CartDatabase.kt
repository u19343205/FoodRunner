package com.codewithharsh.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [CartEntity::class, RestaurantEntity::class], version = 1)
abstract class CartDatabase : RoomDatabase() {

    abstract fun restaurantDao() : RestaurantDao
    abstract  fun orderDao() : CartDao
}