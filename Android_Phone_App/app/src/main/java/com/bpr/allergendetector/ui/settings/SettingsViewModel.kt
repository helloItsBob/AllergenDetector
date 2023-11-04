package com.bpr.allergendetector.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bpr.allergendetector.R
import com.bpr.allergendetector.ui.UiText

class SettingsViewModel : ViewModel() {

    private val _buttons = MutableLiveData<List<UiText>>().apply {
        value = listOf(
            UiText.StringResource(R.string.change_avatar_button),
            UiText.StringResource(R.string.change_password_button),
            UiText.StringResource(R.string.change_language_button),
            UiText.StringResource(R.string.feedback_button),
            UiText.StringResource(R.string.delete_account_button)
        )
    }

    val buttons: LiveData<List<UiText>> = _buttons
}
