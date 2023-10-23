package com.bpr.allergendetector.ui.scan

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.CompletableFuture


class PhotoViewModel : ViewModel() {

    // ML Kit Text Recognition
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    public fun imageFromPath(context: Context, uri: Uri): InputImage? {
        var image: InputImage? = null
        try {
            image = InputImage.fromFilePath(context, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    @RequiresApi(Build.VERSION_CODES.N)
    public fun performTextRecognition(image: InputImage?): CompletableFuture<String> {
        val future = CompletableFuture<String>()

        image?.let { img ->
            recognizer.process(img)
                .addOnSuccessListener { visionText ->
                    val recognizedText = StringBuilder()
                    for (block in visionText.textBlocks) {
                        for (line in block.lines) {
                            val textLine = line.text
                            recognizedText.append(textLine).append("\n")
                        }
                    }
                    val resultText = recognizedText.toString()
                    future.complete(resultText)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    future.complete("")
                }
        }
        return future
    }

    fun deleteImage(context: Context, imageUri: Uri) {
        val contentResolver: ContentResolver = context.contentResolver

        try {
            val result = contentResolver.delete(imageUri, null, null)

            if (result > 0) {
                // Image deleted successfully
                Log.d("ImageDeletion", "Image deleted: $imageUri")
            } else {
                // Failed to delete the image
                Log.d("ImageDeletion", "Failed to delete image: $imageUri")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageDeletion", "Error while deleting image: $e")
        }
    }

    fun saveImageToGallery(context: Context, bitmap: Bitmap): Uri? {
        val contentResolver: ContentResolver = context.contentResolver
        val format = "yyyy-MM-dd-HH-mm-ss-SSS"

        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis())
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Allergen Detector")
        }

        var imageUri: Uri? = null
        var outputStream: OutputStream? = null

        try {
            imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (imageUri != null) {
                outputStream = contentResolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                outputStream?.close()

                Log.d("ImageSaving", "Image saved: $imageUri")
            } else {
                Log.e("ImageSaving", "Failed to save image")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ImageSaving", "Error while saving image: $e")
        } finally {
            outputStream?.close()
        }

        return imageUri
    }
}