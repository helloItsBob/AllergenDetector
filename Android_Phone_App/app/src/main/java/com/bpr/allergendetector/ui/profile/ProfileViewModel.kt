package com.bpr.allergendetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.UiText
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = FirebaseAuth.getInstance().currentUser?.email
    }
    val text: LiveData<String> = _text


    private val _buttons = MutableLiveData<List<UiText>>().apply {
        value = listOf(
            UiText.StringResource(R.string.profile_button_allergens),
            UiText.StringResource(R.string.profile_button_statistics),
            UiText.StringResource(R.string.profile_button_settings)
        )
    }

    val buttons: LiveData<List<UiText>> = _buttons
}