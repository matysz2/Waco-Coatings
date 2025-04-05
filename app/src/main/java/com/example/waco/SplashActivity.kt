package com.example.waco

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waco.ui.theme.WacoTheme
import androidx.compose.ui.res.painterResource

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Opóźnienie przed przejściem do MainActivity (np. 3 sekundy)
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Zakończenie SplashActivity
        }, 3000) // 3000 ms = 3 sekundy

        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    // Logo aplikacji, upewnij się, że masz plik logo.png w folderze res/drawable
    val logo: Painter = painterResource(id = R.drawable.icon)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = logo, contentDescription = "Logo")
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator() // Pokazuje kółko ładowania
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    WacoTheme {
        SplashScreen()
    }
}
