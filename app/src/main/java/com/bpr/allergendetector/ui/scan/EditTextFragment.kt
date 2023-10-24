package com.bpr.allergendetector.ui.scan

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bpr.allergendetector.MainActivity
import com.bpr.allergendetector.databinding.FragmentEditTextBinding


class EditTextFragment : Fragment() {
    private var _binding: FragmentEditTextBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val args: EditTextFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentEditTextBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Hide back button in the action bar
        val actionBar: ActionBar? = (activity as MainActivity?)?.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)

        val resultText = args.textString
        binding.editTextView.setText(resultText)

        val resultPicture = args.imagePath

        // focus, set cursors at the end, and show keyboard
        binding.editTextView.requestFocus()
        binding.editTextView.onFocusChangeListener =
            OnFocusChangeListener { v: View, hasFocus: Boolean ->
                if (hasFocus) {
                    binding.editTextView.setSelection(binding.editTextView.text.length - 1)
                    val imm =
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.editTextView, InputMethodManager.SHOW_IMPLICIT)
                }
            }

        // show a pop-up when enter is pressed
        binding.editTextView.setOnKeyListener { v: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                // confirmation pop-up
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder
                    .setTitle("Confirm changes")
                    .setMessage("Are you sure you want to proceed with the changes?")
                    .setPositiveButton("Proceed") { dialog: DialogInterface, _ ->
                        // navigate to Allergen Detection result fragment
                        val action =
                            EditTextFragmentDirections.actionNavigationEditTextToNavigationDetectionResult(
                                resultPicture, binding.editTextView.text.toString()
                            )
                        findNavController().navigate(action)
                        dialog.dismiss()
                    }

                    .setNegativeButton("Cancel") { dialog: DialogInterface, _ ->
                        dialog.dismiss()
                        findNavController().popBackStack()
                    }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.show()
                return@setOnKeyListener true
            }
            false
        }

        // handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val alertBuilder = AlertDialog.Builder(requireContext())
            alertBuilder
                .setTitle("Confirm action")
                .setMessage("Are you sure you want to proceed without saving the changes?")
                .setPositiveButton("Yes") { dialog: DialogInterface, _ ->
                    dialog.dismiss()
                    findNavController().popBackStack()
                }
                .setNegativeButton("No") { dialog: DialogInterface, _ ->
                    dialog.dismiss()
                    // open keyboard
                    binding.editTextView.setSelection(binding.editTextView.text.length - 1)
                    val imm =
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.editTextView, InputMethodManager.SHOW_IMPLICIT)
                }

            val alertDialog: AlertDialog = alertBuilder.create()
            alertDialog.show()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}