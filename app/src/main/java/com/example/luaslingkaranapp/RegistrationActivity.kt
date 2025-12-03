package com.example.luaslingkaranapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.luaslingkaranapp.ui.theme.LuasLingkaranAppTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LuasLingkaranAppTheme {
                RegistrationScreen()
            }
        }
    }
}

@Composable
fun RegistrationScreen() {
    val context = LocalContext.current

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("User") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.klikfood2),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Registration",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Daftar sebagai:",
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            RoleSelectionButton("User", selectedRole == "User") {
                selectedRole = "User"
            }
            Spacer(modifier = Modifier.width(8.dp))
            RoleSelectionButton("Driver", selectedRole == "Driver") {
                selectedRole = "Driver"
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(username, { username = it }, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            password,
            { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = {
                if (username.isBlank() || email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        val uid = result.user!!.uid

                        val userData = hashMapOf(
                            "username" to username,
                            "name" to name,
                            "email" to email,
                            "phone" to phone,
                            "role" to selectedRole,
                            "createdAt" to Timestamp.now()
                        )

                        firestore.collection("users")
                            .document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Registrasi berhasil", Toast.LENGTH_LONG).show()
                                context.startActivity(Intent(context, LoginActivity::class.java))
                                (context as ComponentActivity).finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    }
            }
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Already have account? ")
            Text(
                "Sign In",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as ComponentActivity).finish()
                }
            )
        }
    }
}

@Composable
fun RowScope.RoleSelectionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color.LightGray,
            contentColor = if (selected) Color.White else Color.Black
        ),
        onClick = onClick
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegister() {
    LuasLingkaranAppTheme {
        RegistrationScreen()
    }
}
