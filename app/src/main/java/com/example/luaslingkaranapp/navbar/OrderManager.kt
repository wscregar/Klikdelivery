package com.example.luaslingkaranapp.navbar

import androidx.annotation.DrawableRes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OrderStatus {
    DIPROSES,
    SELESAI
}

data class Order(
    val date: String,
    val restaurantName: String,
    val foodName: String,
    val price: Int,
    @DrawableRes val imageRes: Int,
    var status: OrderStatus
)

object OrderManager {
    val orders = mutableListOf<Order>()

    fun addOrder(
        restaurantName: String,
        foodName: String,
        price: Int,
        @DrawableRes imageRes: Int
    ) {
        val date = SimpleDateFormat(
            "dd MMM yyyy • HH:mm",
            Locale("id", "ID")
        ).format(Date())

        orders.add(
            Order(
                date = date,
                restaurantName = restaurantName,
                foodName = foodName,
                price = price,
                imageRes = imageRes,
                status = OrderStatus.DIPROSES
            )
        )
    }
}
