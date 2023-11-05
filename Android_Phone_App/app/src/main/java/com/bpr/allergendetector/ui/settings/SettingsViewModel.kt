package com.bpr.allergendetector.ui.settings

import android.content.Context
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.UiText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingsViewModel : ViewModel() {

    private val _buttons = MutableLiveData<List<UiText>>().apply {
        value = generateButtons(isSignedInWithGoogle)
    }

    private fun generateButtons(isSignedInWithGoogle: Boolean): List<UiText> {
        return if (isSignedInWithGoogle) {
            listOf(
                UiText.StringResource(R.string.change_avatar_button),
                UiText.StringResource(R.string.change_language_button),
                UiText.StringResource(R.string.feedback_button),
                UiText.StringResource(R.string.delete_account_button)
            )
        } else {
            listOf(
                UiText.StringResource(R.string.change_avatar_button),
                UiText.StringResource(R.string.change_password_button),
                UiText.StringResource(R.string.change_language_button),
                UiText.StringResource(R.string.feedback_button),
                UiText.StringResource(R.string.delete_account_button)
            )
        }
    }

    val buttons: LiveData<List<UiText>> = _buttons

    companion object {
        val isSignedInWithGoogle: Boolean
            get() {
                val user = Firebase.auth.currentUser
                return user?.providerData?.any { it.providerId == "google.com" } ?: false
            }
    }

    // check if password change is successful
    val passwordChangeSuccessLiveData = MutableLiveData<Boolean>()

    fun updatePassword(
        context: Context,
        oldPassword: EditText,
        newPassword: EditText,
        confirmPassword: EditText
    ) {
        val user = Firebase.auth.currentUser

        if (user == null) {
            Log.e("RE-AUTHENTICATE", "User is null")
            Toast.makeText(context, "User is null", Toast.LENGTH_SHORT).show()
            return
        }

        val oldPasswordStr = oldPassword.text.toString()
        val newPass = newPassword.text.toString()
        val confirmPass = confirmPassword.text.toString()

        if (oldPasswordStr.isEmpty()) {
            Log.e("RE-AUTHENTICATE", "Re-authentication failed: old password cannot be empty")
            Toast.makeText(
                context,
                "Re-authentication failed: old password cannot be empty",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPass.length < 6) {
            Toast.makeText(
                context,
                "Password must be at least 6 characters long",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (newPass != confirmPass) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val credential = EmailAuthProvider.getCredential(user.email!!, oldPasswordStr)

        user.reauthenticate(credential)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    Log.d("RE-AUTHENTICATE", "User re-authenticated.")

                    user.updatePassword(newPass)
                        .addOnCompleteListener { passwordUpdateResult ->
                            if (passwordUpdateResult.isSuccessful) {
                                Log.d("PASSWORD_UPDATE", "User password updated.")
                                Toast.makeText(
                                    context,
                                    "Password updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                passwordChangeSuccessLiveData.value = true

                            } else {
                                Log.e(
                                    "PASSWORD_UPDATE",
                                    "Failed to update password: ${passwordUpdateResult.exception}"
                                )
                                Toast.makeText(
                                    context,
                                    "Failed to update password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Log.e("RE-AUTHENTICATE", "Re-authentication failed: ${result.exception}")
                    Toast.makeText(context, "Wrong current password", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
