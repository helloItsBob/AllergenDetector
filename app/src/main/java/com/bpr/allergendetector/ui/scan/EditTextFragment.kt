package com.bpr.allergendetector.ui.scan

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
        val editTextViewModel =
            ViewModelProvider(this)[EditTextViewModel::class.java]

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
        binding.editTextView.setOnKeyListener { _: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                // confirmation pop-up
                editTextViewModel.showAlertOnEnterPressed(
                    this,
                    resultPicture,
                    binding.editTextView.text.toString()
                )

                return@setOnKeyListener true
            }
            false
        }

        binding.saveButton.setOnClickListener {
            editTextViewModel.showAlertOnEnterPressed(
                this,
                resultPicture,
                binding.editTextView.text.toString()
            )
        }

        // handle on back pressed
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            editTextViewModel.showAlertOnBackPressed(
                this@EditTextFragment,
                binding.editTextView,
                resultText
            )
        }

        binding.backButton.setOnClickListener {
            editTextViewModel.showAlertOnBackPressed(
                this,
                binding.editTextView,
                resultText
            )
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}