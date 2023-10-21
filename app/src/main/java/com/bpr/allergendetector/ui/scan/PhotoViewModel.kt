package com.bpr.allergendetector.ui.scan

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
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
}