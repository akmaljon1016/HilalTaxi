package com.example.hilaltaxi.ui.registrationDialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.hilaltaxi.R
import com.example.hilaltaxi.databinding.RegistrationFragmentBinding
import java.lang.ClassCastException

class RegistrationFragment : DialogFragment() {

    var listener: Continue? = null
    lateinit var binding: RegistrationFragmentBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = RegistrationFragmentBinding.inflate(LayoutInflater.from(context))

        binding.btnContinue.setOnClickListener {
            if (TextUtils.isEmpty(binding.editFirstName.text)) {
                Toast.makeText(requireContext(), "First Name is empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.editLastName.text)) {
                Toast.makeText(requireContext(), "Last Name is empty", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(binding.editPhoneNumber.text)) {
                Toast.makeText(requireContext(), "Phone Number is empty", Toast.LENGTH_SHORT).show()
            } else {
                listener?.onClick(
                    binding.editFirstName.text.toString(),
                    binding.editLastName.text.toString(),
                    binding.editPhoneNumber.text.toString()
                )
                dismiss()
            }
        }
        binding.btnClose.setOnClickListener {
            dialog?.dismiss()
        }


        return AlertDialog.Builder(requireContext(), R.style.DialogTheme)
            .setView(binding.root)
            .create()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as Continue
        } catch (e: ClassCastException) {

        }
    }

    interface Continue {
        fun onClick(fistName: String, lastName: String, phoneNumber: String)
    }
}