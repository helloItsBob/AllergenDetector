package com.bpr.allergendetector.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.UiText

class ProfileViewModel : ViewModel() {

    private val _image = MutableLiveData<Int>().apply {
        value = R.drawable.baseline_profile_circle_24
    }
    val image: LiveData<Int> = _image

    private val _text = MutableLiveData<UiText>().apply {
        value = UiText.StringResource(R.string.profile_name_default_placeholder)
    }
    val text: LiveData<UiText> = _text


    private val _buttons = MutableLiveData<List<UiText>>().apply {
        value = listOf(
            UiText.StringResource(R.string.profile_button_allergens),
            UiText.StringResource(R.string.profile_button_statistics),
            UiText.StringResource(R.string.profile_button_metrics),
            UiText.StringResource(R.string.profile_button_educational_tips),
            UiText.StringResource(R.string.profile_button_settings)
        )
    }

    val buttons: LiveData<List<UiText>> = _buttons
}