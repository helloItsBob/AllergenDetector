package com.bpr.allergendetector.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AvatarUtil {
    companion object {
        fun loadAvatarFromSharedPrefs(imageView: ImageView, context: Context) {

            // retrieve the shared preferences
            val sharedPreferences =
                context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val encodedImage = sharedPreferences.getString("avatar", null)

            if (encodedImage != null) {
                // decode the Base64 string and display the image
                val decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT)
                val decodedBitmap =
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                Glide.with(context)
                    .load(decodedBitmap)
                    .apply(RequestOptions.circleCropTransform()) // make image circular
                    .into(imageView)
            } else {
                Log.e("ProfileFragment", "No image found in shared preferences")

                // use placeholder or google profile image
                if (Firebase.auth.currentUser?.photoUrl != null) {
                    Glide.with(context)
                        .load(Firebase.auth.currentUser?.photoUrl)
                        .apply(RequestOptions.circleCropTransform()) // make image circular
                        .into(imageView)
                }
            }
        }
    }
}