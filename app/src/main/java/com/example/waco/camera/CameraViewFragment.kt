package com.example.waco.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.waco.R
import com.example.waco.custom.TransparentOverlayView
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.util.concurrent.Executors

class CameraViewFragment : Fragment() {

    private lateinit var previewView: androidx.camera.view.PreviewView
    private lateinit var overlayView: TransparentOverlayView
    private lateinit var recoloredImageView: ImageView

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var selectedColor: Int = Color.RED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_camera_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        overlayView = view.findViewById(R.id.overlayView)
        recoloredImageView = view.findViewById(R.id.recoloredImageView)

        arguments?.let {
            val r = it.getInt(ARG_R)
            val g = it.getInt(ARG_G)
            val b = it.getInt(ARG_B)
            selectedColor = Color.rgb(r, g, b)
            overlayView.setBorderColor(selectedColor) // ustaw kolor obramowania
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        if (!OpenCVLoader.initDebug()) {
            throw RuntimeException("OpenCV initialization failed")
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            val imageAnalyzer = androidx.camera.core.ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, CameraAnalyzer { bitmap ->
                        processFrame(bitmap)
                    })
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun processFrame(bitmap: Bitmap) {
        val rect = overlayView.rect

        if (rect.left < 0 || rect.top < 0 || rect.right > bitmap.width || rect.bottom > bitmap.height) {
            return
        }

        val croppedBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
        val recoloredBitmap = recolorObject(croppedBitmap, selectedColor)

        requireActivity().runOnUiThread {
            recoloredImageView.setImageBitmap(recoloredBitmap)
        }
    }

    private fun recolorObject(bitmap: Bitmap, targetColor: Int): Bitmap {
        val mat = Mat()
        org.opencv.android.Utils.bitmapToMat(bitmap, mat)

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGBA2RGB)

        val targetB = Color.blue(targetColor).toDouble()
        val targetG = Color.green(targetColor).toDouble()
        val targetR = Color.red(targetColor).toDouble()

        for (row in 0 until mat.rows()) {
            for (col in 0 until mat.cols()) {
                val pixel = mat.get(row, col)

                val b = pixel[0]
                val g = pixel[1]
                val r = pixel[2]

                val distance = Math.sqrt(
                    (r - 128).pow(2.0) + (g - 128).pow(2.0) + (b - 128).pow(2.0)
                )

                if (distance < 100.0) { // Jeśli piksel jest "średni" kolorystycznie
                    mat.put(row, col, targetB, targetG, targetR)
                }
            }
        }

        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        org.opencv.android.Utils.matToBitmap(mat, outputBitmap)
        return outputBitmap
    }

    private fun Double.pow(exponent: Double) = Math.pow(this, exponent)


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val ARG_R = "r"
        private const val ARG_G = "g"
        private const val ARG_B = "b"

        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

        fun newInstance(r: Int, g: Int, b: Int) = CameraViewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_R, r)
                putInt(ARG_G, g)
                putInt(ARG_B, b)
            }
        }
    }
}
