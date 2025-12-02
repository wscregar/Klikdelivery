package com.example.luaslingkaranapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.luaslingkaranapp.ui.theme.LuasLingkaranAppTheme
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
// DATA CLASS
data class PromoBanner(val id: Int, @DrawableRes val imageRes: Int)
data class FoodCategory(val name: String, @DrawableRes val iconRes: Int)

@Parcelize
data class MenuItem(val name: String, val price: String) : Parcelable
data class Restaurant(
    val name: String,
    val category: String,
    val rating: Double,
    val distance: String,
    @DrawableRes val imageRes: Int,
    val latitude: Double, // TAMBAHAN
    val longitude: Double, // TAMBAHAN
    val menu: List<MenuItem> // <-- TAMBAHAN
)

// Interface untuk mensimulasikan pola OnClickListener
interface CategoryClickListener {
    fun onCategoryClicked(category: FoodCategory)
}

// DATA DUMMY
val promoBanners = listOf(
    PromoBanner(1, R.drawable.banner_promo1),
    PromoBanner(2, R.drawable.banner_promo3),
    PromoBanner(3, R.drawable.banner_promo4)
)

val foodCategories = listOf(
    FoodCategory("Nasi", R.drawable.kategori_nasi),
    FoodCategory("Sate", R.drawable.kategori_sate),
    FoodCategory("Bakso", R.drawable.kategori_bakso),
    FoodCategory("Minuman", R.drawable.kategori_minuman),
    FoodCategory("Jajanan", R.drawable.kategori_jajanan),
    FoodCategory("Martabak", R.drawable.kategori_martabak)
)

val restaurants = listOf(
    Restaurant(
        "Sate Ayam Pak Budi",
        "Sate, Bakaran",
        4.8,
        "1.2 km",
        R.drawable.resto_sate,
        -7.2759, 112.7538,
    menu = listOf(
        MenuItem("Sate Ayam", "20.000"),
        MenuItem("Sate Kambing", "30.000"),
        MenuItem("Sate Padang", "25.500"),
        MenuItem("Sop Buntut", "43.000")
    )),
    Restaurant(
        "Bakso Cak Man",
        "Bakso, Mie",
        4.9,
        "0.8 km",
        R.drawable.resto_bakso,
        -7.2882, 112.7490,
    menu = listOf(
        MenuItem("Bakso Biasa", "10.000"),
        MenuItem("Bakso Spesial", "16.500"),
        MenuItem("Bakso Urat", "13.000"),
        MenuItem("Bakso Jeroan", "17.500")
    )),
    Restaurant(
        "Nasi Goreng Merdeka",
        "Nasi, Chinese Food",
        4.7,
        "2.5 km",
        R.drawable.resto_nasgor,
        -7.2888, 112.793,
        menu = listOf(
        MenuItem("Nasi goreng Spesial", "14.500"),
        MenuItem("Nasi goreng Biasa", "10.000"),
        MenuItem("Nasi goreng Mawut", "17.500")
    )),
    Restaurant(
        "Kopi Kenangan Senja",
        "Minuman Dingin",
        4.9,
        "0.5 km",
        R.drawable.resto_kopken,
        -7.2800, 112.7900,
        menu = listOf(
            MenuItem("Kopi senja", "16.500"),
            MenuItem("Americano", "10.000"),
            MenuItem("Cappucino", "12.500"),
            MenuItem("Kopi Tubruk", "9.000"),
            MenuItem("Espresso", "10.000")
        )),
    Restaurant(
        "Geprek Juara",
        "Ayam, Pedas",
        4.6,
        "3.1 km",
        R.drawable.resto_geprek,
        -7.2905, 112.8001,
        menu = listOf(
            MenuItem("Geprek Biasa", "10.000"),
            MenuItem("Geprek Spesial", "16.500"),
            MenuItem("Geprek Bakar", "12.000"),
            MenuItem("Geprek Komplit", "18.000"),
            MenuItem("Geprek Saus Spesial", "14.000")
        )),
    Restaurant(
        "Martabak Sinar Bulan",
        "Martabak, Manis",
        4.8,
        "1.8 km",
        R.drawable.resto_martabak,
        -7.2785, 112.7650,
        menu = listOf(
            MenuItem("Martabak Kecil", "26.500"),
            MenuItem("Martabak Sedang", "32.500"),
            MenuItem("Martabak Besar", "45.500")
            )),
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LuasLingkaranAppTheme {
                FoodAppHomeScreen()
            }
        }
    }
}
// fungsi untuk menampilkan layar utama aplikasi
@Composable
fun FoodAppHomeScreen() {
    val context = LocalContext.current

    // Implementasi dari interface onClickListener
    val categoryListener = object : CategoryClickListener {
        override fun onCategoryClicked(category: FoodCategory) {
            // Logika yang dijalankan saat kategori diklik
            Toast.makeText(context, "Listener Kategori: ${category.name}", Toast.LENGTH_SHORT).show()
        }
    }
    // fungsi scroll vertikal
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        item { HeaderSection() }
        item {
            PromoSection(
                banners = promoBanners,
                // onClick untuk promo
                onPromoClick = { banner ->
                    Toast.makeText(context, "OnClick Promo ID: ${banner.id}", Toast.LENGTH_SHORT).show()
                }
            )
        }
        item {
            CategorySection(
                categories = foodCategories,
                // teruskan objek listener yang sudah dibuat
                listener = categoryListener
            )
        }
        item {
            Text(
                text = "Restoran Terdekat",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
            )
        }
        items(restaurants) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                // onClick
                onClick = {
                    // Intent
                    // Membuat Intent untuk pindah ke RestaurantDetailActivity
                    val intent = Intent(context, RestaurantDetailActivity::class.java).apply {
                        // Mengisi package dengan data ke dalam Intent
                        // Setiap data diberi key seperti "EXTRA_NAME"
                        putExtra("EXTRA_NAME", restaurant.name)
                        putExtra("EXTRA_CATEGORY", restaurant.category)
                        putExtra("EXTRA_RATING", restaurant.rating)
                        putExtra("EXTRA_DISTANCE", restaurant.distance)
                        putExtra("EXTRA_IMAGE_RES", restaurant.imageRes)
                        putExtra("EXTRA_LATITUDE", restaurant.latitude)
                        putExtra("EXTRA_LONGITUDE", restaurant.longitude)
                        putParcelableArrayListExtra("EXTRA_MENU", ArrayList(restaurant.menu))
                    }
                    // Menjalankan Intent untuk memulai Activity baru
                    context.startActivity(intent)
                }
            )
        }
    }
}

// fungsi untuk header aplikasi
@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "Selamat Pagi, Wira!",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Mau makan apa hari ini?",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// fungsi untuk banner promo
@Composable
fun PromoSection(
    banners: List<PromoBanner>,
    onPromoClick: (PromoBanner) -> Unit
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = "Promo Spesial Untukmu",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        // fungsi scroll horizontal
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(banners) { banner ->
                PromoCard(banner = banner, onClick = { onPromoClick(banner) })
            }
        }
    }
}

@Composable
fun PromoCard(
    banner: PromoBanner,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .size(width = 280.dp, height = 140.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = banner.imageRes),
            contentDescription = "Promo Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// fungsi untuk kategori makanan
@Composable
fun CategorySection(
    categories: List<FoodCategory>,
    listener: CategoryClickListener
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = "Pilih Kategori",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        // fungsi scroll horizontal
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(categories) { category ->
                CategoryItem(category = category, listener = listener)
            }
        }
    }
}

// fungsi untuk item kategori makanan
@Composable
fun CategoryItem(
    category: FoodCategory,
    listener: CategoryClickListener
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable { listener.onCategoryClicked(category) }
    ) {
        Image(
            painter = painterResource(id = category.iconRes),
            contentDescription = category.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// fungsi untuk menampilkan restoran
@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = restaurant.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = restaurant.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.star_on),
                        contentDescription = "Rating",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.rating} \u2022 ${restaurant.distance}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FoodAppPreview() {
    LuasLingkaranAppTheme {
        FoodAppHomeScreen()
    }
}