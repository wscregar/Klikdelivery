package com.example.luaslingkaranapp.navbar

import androidx.annotation.DrawableRes
import androidx.compose.runtime.mutableStateListOf

data class CartItem(
    val name: String,
    val price: Int,
    @DrawableRes val imageRes: Int,
    var quantity: Int
)

object CartManager {
    val cartItems = mutableStateListOf<CartItem>()

    fun addItem(name: String, price: Int, @DrawableRes imageRes: Int) {
        val existing = cartItems.find { it.name == name }
        if (existing != null) {
            existing.quantity++
        } else {
            cartItems.add(CartItem(name, price, imageRes, 1))
        }
    }

    fun totalItems(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun clear() {
        cartItems.clear()
    }
}
