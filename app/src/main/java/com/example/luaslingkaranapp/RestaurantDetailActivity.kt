package com.example.luaslingkaranapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luaslingkaranapp.ui.theme.LuasLingkaranAppTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

class RestaurantDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menerima semua data dari Intent
        val name = intent.getStringExtra("EXTRA_NAME") ?: "N/A"
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "N/A"
        val rating = intent.getDoubleExtra("EXTRA_RATING", 0.0)
        val distance = intent.getStringExtra("EXTRA_DISTANCE") ?: "N/A"
        val imageRes = intent.getIntExtra("EXTRA_IMAGE_RES", R.drawable.ic_launcher_background)
        val latitude = intent.getDoubleExtra("EXTRA_LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("EXTRA_LONGITUDE", 0.0)
        val menuItems: ArrayList<MenuItem> = intent.getParcelableArrayListExtra("EXTRA_MENU") ?: arrayListOf()

        setContent {
            LuasLingkaranAppTheme {
                Surface {
                    RestaurantDetailScreen(
                        name = name,
                        category = category,
                        rating = rating,
                        distance = distance,
                        imageRes = imageRes,
                        location = LatLng(latitude, longitude),
                        menu = menuItems
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantDetailScreen(
    name: String,
    category: String,
    rating: Double,
    distance: String,
    @DrawableRes imageRes: Int,
    location: LatLng,
    menu: List<MenuItem>
) {
    // LazyColumn membuat seluruh halaman bisa di-scroll
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bagian 1: Gambar Utama
        item {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
        }

        // Bagian 2: Informasi Restoran
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = name, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = category, fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.star_on),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$rating \u2022 $distance",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }

        // Bagian 3: Daftar Menu
        items(menu) { menuItem ->
            MenuItemRow(name = menuItem.name, price = menuItem.price)
        }

        // Bagian 4: Lokasi di Peta
        item {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Our Location",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(location, 16f) // Zoom lebih dekat
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp), // Beri tinggi tetap untuk peta
                    cameraPositionState = cameraPositionState
                ) {
                    Marker(
                        state = MarkerState(position = location),
                        title = name
                    )
                }
            }
        }
    }
}

// Composable untuk menampilkan satu baris menu
@Composable
fun MenuItemRow(name: String, price: String) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = price, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Tombol tambah — gunakan name & price dari parameter
            Button(
                onClick = {
                    // Konversi harga: hapus non-digit lalu parse
                    val numeric = price.replace("[^0-9]".toRegex(), "")
                    val priceInt = numeric.toIntOrNull() ?: 0

                    com.example.luaslingkaranapp.navbar.CartManager.addItem(
                        name,
                        priceInt,
                        R.drawable.resto_bakso
                    )

                    Toast.makeText(
                        context,
                        "$name berhasil ditambahkan ke keranjang",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                // ukur button supaya tidak memakan seluruh row
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Tambah")
            }
        }
    }
}