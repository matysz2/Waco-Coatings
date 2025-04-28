package com.example.waco.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.waco.R
import com.example.waco.data.ColorResponse
import com.example.waco.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ColorPickerActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textSelectedColor: TextView
    private lateinit var btnTakePhoto: Button
    private lateinit var btnPickPhoto: Button
    private lateinit var btnSendColor: Button

    private var selectedBitmap: Bitmap? = null
    private var selectedColorHex: String = ""
    private var selectedColorRGB: String = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        imageView = findViewById(R.id.imageView)
        textSelectedColor = findViewById(R.id.textSelectedColor)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)
        btnPickPhoto = findViewById(R.id.btnPickPhoto)
        btnSendColor = findViewById(R.id.btnSendColor)

        btnTakePhoto.setOnClickListener { takePhoto() }
        btnPickPhoto.setOnClickListener { pickPhoto() }
        btnSendColor.setOnClickListener { sendColorToServer() }
        imageView.setOnTouchListener { v, event ->
            selectedBitmap?.let { bitmap ->
                val imageViewWidth = imageView.width.toFloat()
                val imageViewHeight = imageView.height.toFloat()

                val bitmapWidth = bitmap.width.toFloat()
                val bitmapHeight = bitmap.height.toFloat()

                // Oblicz skalę
                val scaleX = bitmapWidth / imageViewWidth
                val scaleY = bitmapHeight / imageViewHeight

                // Skoryguj współrzędne kliknięcia
                val x = (event.x * scaleX).toInt()
                val y = (event.y * scaleY).toInt()

                if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                    val pixel = bitmap.getPixel(x, y)
                    selectedColorHex = String.format("#%06X", 0xFFFFFF and pixel)
                    selectedColorRGB = "${Color.red(pixel)},${Color.green(pixel)},${Color.blue(pixel)}"
                    textSelectedColor.text = "Wybrany kolor: $selectedColorHex\nRGB: $selectedColorRGB"
                }
            }
            true
        }


        textSelectedColor.setOnClickListener {
            if (selectedColorHex.isNotEmpty() && selectedColorRGB.isNotEmpty()) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText(
                    "Kolor",
                    "HEX: $selectedColorHex\nRGB: $selectedColorRGB"
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "Kolor skopiowany do schowka!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            bitmap?.let {
                selectedBitmap = it
                imageView.setImageBitmap(it)
            }
        }
    }

    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            uri?.let {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                selectedBitmap = bitmap
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private fun takePhoto() {
        if (checkPermission()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePhotoLauncher.launch(intent)
        } else {
            requestPermission()
        }
    }

    private fun pickPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickPhotoLauncher.launch(intent)
    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
    }

    private fun sendColorToServer() {
        if (selectedColorHex.isEmpty() || selectedColorRGB.isEmpty()) {
            Toast.makeText(this, "Wybierz kolor na zdjęciu!", Toast.LENGTH_SHORT).show()
            return
        }

        val progressDialog = android.app.ProgressDialog(this)
        progressDialog.setMessage("Wysyłanie danych...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val service = RetrofitInstance.create()
        val call = service.sendColor(selectedColorHex, selectedColorRGB)

        call.enqueue(object : Callback<ColorResponse> { // <--- WAŻNA zmiana tutaj!
            override fun onResponse(call: Call<ColorResponse>, response: Response<ColorResponse>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    val colorName = response.body()?.colorName ?: "Nieznany kolor"
                    android.util.Log.d("ColorPicker", "Odpowiedź JSON: $colorName")

                    val intent = Intent(this@ColorPickerActivity, ColorResultActivity::class.java)
                    intent.putExtra("colorName", colorName)
                    intent.putExtra("colorHex", selectedColorHex)
                    intent.putExtra("colorRGB", selectedColorRGB)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                } else {
                    android.util.Log.e("ColorPicker", "Błąd odpowiedzi: ${response.code()} ${response.message()}")
                    Toast.makeText(this@ColorPickerActivity, "Błąd odpowiedzi: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ColorResponse>, t: Throwable) {
                progressDialog.dismiss()
                android.util.Log.e("ColorPicker", "Błąd połączenia: ${t.message}", t)
                Toast.makeText(this@ColorPickerActivity, "Błąd połączenia: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}
