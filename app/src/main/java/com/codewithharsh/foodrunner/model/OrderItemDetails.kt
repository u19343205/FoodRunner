package com.codewithharsh.foodrunner.model

import org.json.JSONArray

data class OrderItemDetails(
    val oderId: String,
    val resName: String,
    val orderDate: String,
    val foodItems: JSONArray
)