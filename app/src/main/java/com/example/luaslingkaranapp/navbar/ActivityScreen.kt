package com.example.luaslingkaranapp.navbar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActivityScreen() {
    var selectedTab by remember { mutableStateOf(OrderStatus.DIPROSES) }

    val orders = OrderManager.orders.filter { it.status == selectedTab }

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Aktivitas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            ActivityTab(
                title = "Selesai",
                selected = selectedTab == OrderStatus.SELESAI
            ) {
                selectedTab = OrderStatus.SELESAI
            }

            Spacer(modifier = Modifier.width(8.dp))

            ActivityTab(
                title = "Dalam Proses",
                selected = selectedTab == OrderStatus.DIPROSES
            ) {
                selectedTab = OrderStatus.DIPROSES
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada pesanan", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    ActivityCard(order)
                }
            }
        }
    }
}

@Composable
fun ActivityTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color.Black else Color.LightGray
        )
    ) {
        Text(title, color = Color.White)
    }
}

@Composable
fun ActivityCard(order: Order) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5E5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = order.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(order.foodName, fontWeight = FontWeight.Bold)
                Text(order.restaurantName, fontSize = 12.sp)
                Text(order.date, fontSize = 11.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp ${order.price}",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        CartManager.addItem(
                            name = order.foodName,
                            price = order.price,
                            imageRes = order.imageRes
                        )

                        Toast.makeText(
                            context,
                            "${order.foodName} ditambahkan ke keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = RoundedCornerShape(20),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Beli lagi", fontSize = 12.sp)
                }
            }
        }
    }
}
