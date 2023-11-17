package com.bpr.allergendetector.ui

import android.content.Context
import androidx.core.content.ContextCompat
import com.bpr.allergendetector.R
import com.bpr.allergendetector.databinding.CustomSwitchBinding

class SwitchState {

    companion object {
        const val HARMLESS_STATE = "HARMLESS"
        const val HARMFUL_STATE = "HARMFUL"

        fun updateSwitchUI(switchBinding: CustomSwitchBinding, context: Context, state: String) {
            with(switchBinding) {
                switchBinding.switchHarmfulHarmless.isChecked = (state == HARMLESS_STATE)

                val textColorHarmful = ContextCompat.getColor(
                    context,
                    R.color.purple_500
                )
                val textColorHarmless = ContextCompat.getColor(
                    context,
                    R.color.white
                )

                val textColorChecked =
                    if (state == HARMLESS_STATE) textColorHarmful else textColorHarmless
                val textColorUnchecked =
                    if (state == HARMLESS_STATE) textColorHarmless else textColorHarmful

                switchHarmful.setTextColor(textColorChecked)
                switchHarmless.setTextColor(textColorUnchecked)
            }
        }
    }
}