import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.waco.R

class OrderActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        // Sprawdzenie, czy użytkownik jest zalogowany
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)

        if (userId == null) {
            // Jeśli użytkownik nie jest zalogowany, przekieruj do ekranu logowania
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

}
    }