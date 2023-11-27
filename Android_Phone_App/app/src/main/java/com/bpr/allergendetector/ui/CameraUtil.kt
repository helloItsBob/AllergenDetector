package com.bpr.allergendetector.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.scan.ScanFragment
import com.bpr.allergendetector.ui.scan.ScanFragmentDirections
import java.text.SimpleDateFormat
import java.util.Locale

class CameraUtil {

    companion object {

        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        private var imageCapture: ImageCapture? = null
        fun startCamera(
            previewView: PreviewView,
            context: Context,
            lifecycleOwner: LifecycleOwner
        ) {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                imageCapture = ImageCapture.Builder()
                    .build()

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture
                    )

                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(context))
        }

        fun takePhoto(context: Context, fragment: Fragment, imageView: ImageView? = null) {
            // Get a stable reference of the modifiable image capture use case
            val imageCapture = imageCapture ?: return

            // Create time stamped name and MediaStore entry.
            val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Allergen Detector")
                }
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions
                .Builder(
                    context.applicationContext.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()

            // Set up image capture listener, which is triggered after photo has been taken
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        // Image is saved to the gallery to 'Allergen Detector' folder
                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Log.d(TAG, msg)

                        // navigate to photo fragment with a passed argument
                        val imagePath = output.savedUri.toString()

                        if (fragment is ScanFragment) {
                            val action =
                                ScanFragmentDirections.actionNavigationScanToNavigationPhoto(
                                    imagePath
                                )
                            findNavController(fragment).navigate(action)

                        } else {
                            val builder = AlertDialog.Builder(context)
                            val inflater =
                                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val view = inflater.inflate(R.layout.dialog_image, null)

                            // Set the image resource to the ImageView in the custom layout
                            val dialogImageView = view.findViewById<ImageView>(R.id.imageView)
                            dialogImageView.setImageURI(Uri.parse(imagePath))

                            builder.setView(view)
                                .setTitle("Confirm Image")
                                .setMessage("Do you want to use this image?")
                                .setPositiveButton("Yes") { dialog, _ ->
                                    imageView?.setImageURI(Uri.parse(imagePath))
                                    dialog.dismiss()
                                }
                                .setNegativeButton("Cancel") { dialog, _ ->
                                    dialog.cancel()
                                }

                            val alertDialog = builder.create()
                            alertDialog.show()
                        }
                    }
                }
            )
        }
    }
}