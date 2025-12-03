package com.example.luaslingkaranapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.luaslingkaranapp.ui.theme.LuasLingkaranAppTheme
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.luaslingkaranapp.chat.ChatListScreen
import com.example.luaslingkaranapp.chat.ChatRoomScreen
import com.example.luaslingkaranapp.navbar.CartManager
import com.example.luaslingkaranapp.navbar.OrderStatus

// --- DATA MODELS ---
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
    val latitude: Double,
    val longitude: Double,
    val menu: List<MenuItem>
)

// Data Model untuk Keranjang (Dummy)
data class CartItem(val name: String, val price: Int, var quantity: Int)

// Data Model untuk History (Dummy)
data class OrderHistory(val date: String, val restaurantName: String, val items: String, val total: String, val status: String)

// --- NAVIGATION ITEMS ---
sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Chat : BottomNavItem("chat", "Chat", Icons.Filled.Chat) // simplified route
    object Cart : BottomNavItem("cart", "Keranjang", Icons.Filled.ShoppingCart)
    object History : BottomNavItem("history", "History", Icons.Filled.History)
}

// --- DATA DUMMY ---
val promoBanners = listOf(
    PromoBanner(1, R.drawable.banner_promo1),
    PromoBanner(2, R.drawable.banner_promo3),
    PromoBanner(3, R.drawable.banner_promo4)
)

val foodCategories = listOf(
    FoodCategory("Semua", R.drawable.ic_launcher_background), // Icon placeholder
    FoodCategory("Nasi", R.drawable.kategori_nasi),
    FoodCategory("Sate", R.drawable.kategori_sate),
    FoodCategory("Bakso", R.drawable.kategori_bakso),
    FoodCategory("Minuman", R.drawable.kategori_minuman),
    FoodCategory("Jajanan", R.drawable.kategori_jajanan),
    FoodCategory("Martabak", R.drawable.kategori_martabak)
)

val restaurants = listOf(
    Restaurant(
        "Sate Ayam Pak Budi", "Sate, Bakaran", 4.8, "1.2 km", R.drawable.resto_sate, -7.2759, 112.7538,
        menu = listOf(MenuItem("Sate Ayam", "20.000"), MenuItem("Sate Kambing", "30.000"))
    ),
    Restaurant(
        "Bakso Cak Man", "Bakso, Mie", 4.9, "0.8 km", R.drawable.resto_bakso, -7.2882, 112.7490,
        menu = listOf(MenuItem("Bakso Biasa", "10.000"), MenuItem("Bakso Spesial", "16.500"))
    ),
    Restaurant(
        "Nasi Goreng Merdeka", "Nasi, Chinese Food", 4.7, "2.5 km", R.drawable.resto_nasgor, -7.2888, 112.793,
        menu = listOf(MenuItem("Nasi goreng Spesial", "14.500"), MenuItem("Nasi goreng Biasa", "10.000"))
    ),
    Restaurant(
        "Kopi Kenangan Senja", "Minuman Dingin", 4.9, "0.5 km", R.drawable.resto_kopken, -7.2800, 112.7900,
        menu = listOf(MenuItem("Kopi senja", "16.500"), MenuItem("Americano", "10.000"))
    ),
    Restaurant(
        "Geprek Juara", "Ayam, Pedas", 4.6, "3.1 km", R.drawable.resto_geprek, -7.2905, 112.8001,
        menu = listOf(MenuItem("Geprek Biasa", "10.000"), MenuItem("Geprek Spesial", "16.500"))
    ),
    Restaurant(
        "Martabak Sinar Bulan", "Martabak, Manis", 4.8, "1.8 km", R.drawable.resto_martabak, -7.2785, 112.7650,
        menu = listOf(MenuItem("Martabak Kecil", "26.500"), MenuItem("Martabak Sedang", "32.500"))
    ),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LuasLingkaranAppTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // --- ambil username dari Firestore (tetap seperti sebelumnya) ---
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val uid = auth.currentUser?.uid
    var username by remember { mutableStateOf("User") }

    LaunchedEffect(uid) {
        if (uid != null) {
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        username = document.getString("username") ?: "User"
                    }
                }
        }
    }
    // --- end username ---

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Chat,
        BottomNavItem.Cart,
        BottomNavItem.History
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            if (item is BottomNavItem.Cart && CartManager.totalItems() > 0) {
                                BadgedBox(
                                    badge = { Badge { Text(CartManager.totalItems().toString()) } }
                                ) {
                                    Icon(item.icon, contentDescription = item.title)
                                }
                            } else {
                                Icon(item.icon, contentDescription = item.title)
                            }
                        },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(username = username)
            }

            composable(BottomNavItem.Chat.route) {
                ChatListScreen(navController = navController)
            }

            composable("chatRoom/{chatId}") {
                ChatRoomScreen(chatId = it.arguments?.getString("chatId") ?: "")
            }

            composable(BottomNavItem.Cart.route) {
                CartScreen()
            }

            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
        }
    }
}
// --- HOME SCREEN ---
@Composable
fun HomeScreen(username: String) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    val context = LocalContext.current

    val filteredRestaurants = if (selectedCategory == "Semua") {
        restaurants
    } else {
        restaurants.filter { it.category.contains(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        item { HeaderSection(username = username) }
        item { PromoSection(promoBanners) }
        item {
            CategorySection(
                categories = foodCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category.name
                }
            )
        }

        item {
            Text(
                text = "Restoran Terdekat (${filteredRestaurants.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                color = Color.Black
            )
        }

        if (filteredRestaurants.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Tidak ada restoran di kategori ini", color = Color.Gray)
                }
            }
        } else {
            items(filteredRestaurants) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    onClick = {
                        val intent = Intent(context, RestaurantDetailActivity::class.java).apply {
                            putExtra("EXTRA_NAME", restaurant.name)
                            putExtra("EXTRA_CATEGORY", restaurant.category)
                            putExtra("EXTRA_RATING", restaurant.rating)
                            putExtra("EXTRA_DISTANCE", restaurant.distance)
                            putExtra("EXTRA_IMAGE_RES", restaurant.imageRes)
                            putExtra("EXTRA_LATITUDE", restaurant.latitude)
                            putExtra("EXTRA_LONGITUDE", restaurant.longitude)
                            putParcelableArrayListExtra("EXTRA_MENU", ArrayList(restaurant.menu))
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

// --- CART SCREEN ---
@Composable
fun CartScreen() {
    val cartItems = CartManager.cartItems

    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val ongkir = if (cartItems.isNotEmpty()) 10000 else 0
    val total = subtotal + ongkir

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                "Keranjang Pesanan",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(item.name, color = Color.Black, fontWeight = FontWeight.Bold)
                            Text("Rp ${item.price}", color = Color.Black, fontSize = 12.sp)
                        }
                        Row {
                            IconButton(onClick = {
                                if (item.quantity > 1) item.quantity--
                                else cartItems.remove(item)
                            }) {
                                Icon(Icons.Default.RemoveCircleOutline, null)
                            }
                            Text("${item.quantity}")
                            IconButton(onClick = { item.quantity++ }) {
                                Icon(Icons.Default.AddCircleOutline, null)
                            }
                        }
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Subtotal")
                    Text("Rp $subtotal")
                }
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Ongkir")
                    Text("Rp $ongkir")
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("Rp $total", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (cartItems.isEmpty()) return@Button

                        val itemsText = cartItems.joinToString {
                            "${it.name} x${it.quantity}"
                        }

                        com.example.luaslingkaranapp.navbar.OrderManager.addOrder(
                            restaurantName = "Pesanan Kamu",
                            foodName = itemsText,
                            price = total,
                            imageRes = R.drawable.resto_bakso
                        )

                        CartManager.clear()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Checkout Sekarang")
                }


            }
        }
    }
}

// --- HISTORY SCREEN ---
@Composable
fun HistoryScreen() {
    val histories = com.example.luaslingkaranapp.navbar.OrderManager.orders

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text("Riwayat Pesanan", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (histories.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada pesanan", color = Color.Gray)
                }
            }
        }

        items(histories) { history ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(history.restaurantName, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (history.status == OrderStatus.SELESAI) "Selesai" else "Diproses",
                            color = if (history.status == OrderStatus.SELESAI)
                                Color(0xFF10B981) else Color(0xFFF59E0B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(4.dp))
                    Text(history.date, fontSize = 12.sp, color = Color.Gray)

                    Spacer(Modifier.height(8.dp))
                    Text(history.foodName)
                    Text("Rp ${history.price}", fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            CartManager.clear()
                            CartManager.addItem(
                                name = "Pesanan Sebelumnya",
                                price = history.price,
                                imageRes = R.drawable.resto_bakso
                            )
                        },
                        border = BorderStroke(1.dp, Color.Blue),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Pesan Lagi", color = Color.Blue)
                    }
                }
            }
        }
    }
}

// --- KOMPONEN LAMA (Header, Promo, Category) ---

@Composable
fun HeaderSection(username: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF000000), Color(0xFF212121))
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "Selamat Datang, $username!",
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

@Composable
fun PromoSection(banners: List<PromoBanner>) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = "Promo Spesial Untukmu",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Black
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(banners) { banner ->
                PromoCard(banner)
            }
        }
    }
}

@Composable
fun PromoCard(banner: PromoBanner) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.size(width = 280.dp, height = 140.dp)
    ) {
        Image(
            painter = painterResource(id = banner.imageRes),
            contentDescription = "Promo Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CategorySection(
    categories: List<FoodCategory>,
    selectedCategory: String,
    onCategorySelected: (FoodCategory) -> Unit
) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = "Pilih Kategori",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Black
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    isSelected = selectedCategory == category.name,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: FoodCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color(0xFF2563EB) else Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (category.name == "Semua") {
                Icon(Icons.Filled.RestaurantMenu, contentDescription = "Semua", tint = if(isSelected) Color.White else Color.Gray)
            } else {
                Image(
                    painter = painterResource(id = category.iconRes),
                    contentDescription = category.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(if(isSelected) 4.dp else 0.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF2563EB) else Color.Black
        )
    }
}

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
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
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
                        text = "${restaurant.rating} • ${restaurant.distance}",
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
        MainScreen()
    }
}
