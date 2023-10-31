package com.bpr.allergendetector

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bpr.allergendetector.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        Log.e(LoginActivity.TAG, "auth: $auth")

        binding.buttonRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextRepeatPassword.text.toString()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(
                    baseContext,
                    "Please fill in all of the provided fields",
                    Toast.LENGTH_SHORT,
                ).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    baseContext,
                    "Please enter a valid email address",
                    Toast.LENGTH_SHORT,
                ).show()

            } else if (password != confirmPassword) {
                Toast.makeText(
                    baseContext,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                createAccount(email, password)
            }
        }

        // Make "Login" look like a button
        val spannableString = SpannableString(binding.textViewLogin.text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        spannableString.setSpan(
            clickableSpan,
            binding.textViewLogin.text.indexOf("Login"),
            binding.textViewLogin.text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textViewLogin.text = spannableString
        binding.textViewLogin.movementMethod = LinkMovementMethod.getInstance()
    }


    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(LoginActivity.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    if (user != null) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(LoginActivity.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Registration failed, please try again",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}