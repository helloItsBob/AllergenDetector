package com.bpr.allergendetector.ui.scan

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

class EditTextViewModel : ViewModel() {

    fun showAlertOnEnterPressed(fragment: Fragment, pictureUri: String, editedText: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(fragment.requireContext())
        builder
            .setTitle("Confirm changes")
            .setMessage("Are you sure you want to proceed with the changes?")
            .setPositiveButton("Proceed") { dialog: DialogInterface, _: Int ->
                // navigate to Allergen Detection result fragment
                val action =
                    EditTextFragmentDirections.actionNavigationEditTextToNavigationDetectionResult(
                        pictureUri, editedText
                    )
                findNavController(fragment).navigate(action)
                dialog.dismiss()
            }

            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun showAlertOnBackPressed(fragment: Fragment, editTextView: EditText, string: String) {
        val alertBuilder = AlertDialog.Builder(fragment.requireContext())
        alertBuilder
            .setTitle("Confirm action")
            .setMessage("Are you sure you want to proceed without saving the changes?")
            .setPositiveButton("Yes") { dialog: DialogInterface, _ ->
                dialog.dismiss()
                findNavController(fragment).popBackStack()
            }
            .setNegativeButton("No") { dialog: DialogInterface, _ ->
                dialog.dismiss()
                // open keyboard
                val imm =
                    fragment.requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editTextView, InputMethodManager.SHOW_IMPLICIT)
            }

        val alertDialog: AlertDialog = alertBuilder.create()
        alertDialog.show()
    }
}