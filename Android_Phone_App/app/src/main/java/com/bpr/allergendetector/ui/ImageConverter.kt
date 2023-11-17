package com.bpr.allergendetector.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

class ImageConverter {
    companion object {
        fun resizeBitmap(originalBitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
            return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
        }

        fun compressAndCovertBitmapToByteArray(originalBitmap: Bitmap, quality: Int): ByteArray {
            val stream = ByteArrayOutputStream()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            return stream.toByteArray()
        }

        fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }

        fun convertByteArrayToBase64(byteArray: ByteArray): String {
            return  Base64.encodeToString(byteArray, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        }

        fun convertStringToBitmap(base64: String): Bitmap {
            val decodedString: ByteArray = Base64.decode(base64, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}