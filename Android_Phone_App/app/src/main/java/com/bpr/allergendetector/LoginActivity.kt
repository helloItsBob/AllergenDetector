package com.bpr.allergendetector

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bpr.allergendetector.databinding.ActivityLoginBinding
import com.bpr.allergendetector.ui.allergenlist.Allergen
import com.bpr.allergendetector.ui.allergenlist.AllergenListViewModel
import com.bpr.allergendetector.ui.lists.ListsViewModel
import com.bpr.allergendetector.ui.lists.Product
import com.bpr.allergendetector.ui.recentscans.RecentScan
import com.bpr.allergendetector.ui.recentscans.RecentScansViewModel
import com.bpr.allergendetector.ui.statistics.ScanCounter
import com.bpr.allergendetector.ui.statistics.StatisticsViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest

    private val REQ_ONE_TAP = 2 // can be any Int
    private var showOneTapUI = true

    private lateinit var logRegViewModel: LogRegViewModel

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logRegViewModel =
            ViewModelProvider(this)[LogRegViewModel::class.java]

        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        auth = Firebase.auth
        Log.e(TAG, "auth: $auth")

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    baseContext,
                    "Please fill in all of the provided fields",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                signIn(email, password)
            }
        }

        binding.googleLogin.setOnClickListener {
            if (!logRegViewModel.isNetworkAvailable(this)) {
                Toast.makeText(
                    baseContext,
                    "No internet connection.",
                    Toast.LENGTH_SHORT,
                ).show()
            } else {
                signIn()
            }
        }

        // Make "Register" look like a button
        val spannableString = SpannableString(binding.textViewRegister.text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        spannableString.setSpan(
            clickableSpan,
            binding.textViewRegister.text.indexOf("Register"),
            binding.textViewRegister.text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textViewRegister.text = spannableString
        binding.textViewRegister.movementMethod = LinkMovementMethod.getInstance()

        // night mode
        DarkMode.setDarkModeBasedOnPrefs(this)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    welcomeUser(user?.email.toString())

                    updateUserAllergensAndProductLists(user)
                    updateProfileImage(user)

                } else if (!logRegViewModel.isNetworkAvailable(this)) {
                    Toast.makeText(
                        baseContext,
                        "No internet connection.",
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Incorrect email or password",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }

    private fun updateProfileImage(user: FirebaseUser?) {

        // clean up the shared preferences
        val sharedPreferences =
            getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("avatar")
        editor.apply()

        var avatar = ""
        val db = Firebase.firestore
        val docRef = db.collection("users").document(user?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data

                    avatar = data?.get("avatar").toString()
                    if (avatar != "null") {

                        // save avatar base64 string to shared preferences
                        editor.putString("avatar", avatar)
                        editor.apply()
                    }

                } else {
                    Log.e("DB fetch", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DB fetch", "failed with ", exception)
            }
    }

    private fun updateUserAllergensAndProductLists(user: FirebaseUser?) {

        val allergenListViewModel = AllergenListViewModel(application)
        val productListViewModel = ListsViewModel(application)
        val recentScansViewModel = RecentScansViewModel(application)
        val statisticsViewModel = StatisticsViewModel(application)

        val db = Firebase.firestore
        val docRef = db.collection("users").document(user?.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data

                    // Store data in Room database
                    val allergenList: List<Allergen> = parseJson(data?.get("allergen").toString())
                    allergenListViewModel.deleteAll()
                    allergenListViewModel.insertAll(allergenList)

                    val productList: List<Product> = parseJson(data?.get("product").toString())
                    Log.e("DB fetch", "Product list: $productList")

                    productListViewModel.deleteAll()
                    productListViewModel.insertAll(productList)

                    val recentScans: List<RecentScan> =
                        parseJson(data?.get("recentScan").toString())
                    recentScansViewModel.deleteAll()
                    recentScansViewModel.insertAll(recentScans)

                    val scanCounters: List<ScanCounter> =
                        parseJson(data?.get("scanCounter").toString())
                    statisticsViewModel.deleteAll()
                    statisticsViewModel.insertAll(scanCounters)

                } else {
                    Log.e("DB fetch", "No such document")
                    allergenListViewModel.deleteAll()
                    productListViewModel.deleteAll()
                    recentScansViewModel.deleteAll()
                    statisticsViewModel.deleteAll()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DB fetch", "get failed with ", exception)
            }
    }

    private inline fun <reified T> parseJson(jsonData: String): List<T> {
        val gson = Gson()
        val listType = object : TypeToken<List<T>>() {}.type
        if (jsonData == "null") {
            return emptyList()
        }
        Log.e("parseJson", jsonData)
        return gson.fromJson(jsonData, listType)
    }

    private fun signIn() {
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                e.localizedMessage?.let { Log.d(TAG, it) }
            }
    }

    private fun welcomeUser(email: String) {
        Toast.makeText(
            baseContext,
            "Welcome, $email!",
            Toast.LENGTH_SHORT,
        ).show()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val email = credential.displayName
                    welcomeUser(email.toString())
                    when {
                        idToken != null -> {
                            Log.d(TAG, "Got ID token.")
                            firebaseAuthWithGoogle(idToken)

                            // store idToken in shared preferences
                            val sharedPreferences =
                                getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("idToken", idToken)
                            editor.apply()
                        }

                        else -> {
                            Log.d(TAG, "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            showOneTapUI = false
                        }

                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                        }

                        else -> {
                            Log.d(
                                TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG2, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)

                    updateUserAllergensAndProductLists(user)
                    updateProfileImage(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG2, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    companion object {
        const val TAG = "EmailPassword"
        private const val TAG2 = "GoogleSignIn"
    }
}