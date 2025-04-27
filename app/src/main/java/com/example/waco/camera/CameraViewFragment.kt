package com.example.waco.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.waco.R
import com.example.waco.data.ColorHex
import com.example.waco.data.Color as ColorRGB
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraViewFragment : Fragment() {

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var overlayView: DrawingOverlay
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var switchCamera: Switch
    private lateinit var saveButton: Button
    private lateinit var clearButton: Button
    private lateinit var undoButton: Button
    private lateinit var brushSizeSeekBar: SeekBar

    private var isFrontCamera = false
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_camera_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        overlayView = view.findViewById(R.id.drawingOverlay)
        switchCamera = view.findViewById(R.id.cameraSwitch)
        saveButton = view.findViewById(R.id.saveButton)
        clearButton = view.findViewById(R.id.clearButton)
        undoButton = view.findViewById(R.id.undoButton)
        brushSizeSeekBar = view.findViewById(R.id.brushSizeSeekBar)

        handleColorArguments()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        switchCamera.setOnCheckedChangeListener { _, isChecked ->
            isFrontCamera = isChecked
            startCamera()
        }

        saveButton.setOnClickListener { saveDrawing() }
        clearButton.setOnClickListener { overlayView.clearAll() }
        undoButton.setOnClickListener { overlayView.undoLastLine() }

        brushSizeSeekBar.max = 50
        brushSizeSeekBar.progress = 5
        brushSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress > 0) {
                    overlayView.setBrushSize(progress.toFloat())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
    private fun handleColorArguments() {
        arguments?.let {
            when {
                it.containsKey(ARG_COLOR_HEX) -> {
                    val hexCode = it.getString(ARG_COLOR_HEX)
                    if (!hexCode.isNullOrEmpty()) {
                        try {
                            val colorInt = android.graphics.Color.parseColor(hexCode)
                            val r = (colorInt shr 16) and 0xFF
                            val g = (colorInt shr 8) and 0xFF
                            val b = colorInt and 0xFF
                            overlayView.setPaintColor(r, g, b)
                        } catch (e: IllegalArgumentException) {
                            // Jeśli HEX jest zły — ustaw domyślny biały kolor
                            overlayView.setPaintColor(255, 255, 255)
                        }
                    } else {
                        // Jeśli HEX pusty lub null — ustaw biały
                        overlayView.setPaintColor(255, 255, 255)
                    }
                }

                it.containsKey(ARG_COLOR_NAME) -> {
                    val r = it.getInt(ARG_R)
                    val g = it.getInt(ARG_G)
                    val b = it.getInt(ARG_B)
                    overlayView.setPaintColor(r, g, b)
                }
            }
        }
    }



    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun saveDrawing() {
        val bitmap = overlayView.createBitmap()
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Drawing_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WacoDrawings")
        }
        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                Toast.makeText(requireContext(), "Zapisano rysunek!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val ARG_COLOR_NAME = "color_name"
        private const val ARG_R = "r"
        private const val ARG_G = "g"
        private const val ARG_B = "b"
        private const val ARG_COLOR_HEX = "color_hex"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10

        fun newInstance(color: ColorRGB) = CameraViewFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_COLOR_NAME, color.name)
                putInt(ARG_R, color.r)
                putInt(ARG_G, color.g)
                putInt(ARG_B, color.b)
            }
        }

        fun newInstanceHex(colorHex: ColorHex) = CameraViewFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_COLOR_HEX, colorHex.hex)
            }
        }
    }
}
