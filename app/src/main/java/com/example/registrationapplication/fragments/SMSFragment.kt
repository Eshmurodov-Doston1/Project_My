package com.example.registrationapplication.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.registrationapplication.R
import com.example.registrationapplication.databinding.FragmentSMSBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SMSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SMSFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    lateinit var fragmentSMSBinding: FragmentSMSBinding
    lateinit var root:View
    var phone_number: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentSMSBinding = FragmentSMSBinding.inflate(inflater,container,false)
        root= fragmentSMSBinding.root
        var name_user = arguments?.getString("name")
        phone_number = arguments?.getString("phone_number")
        println(name_user)
        println(phone_number)
        fragmentSMSBinding.name.text = name_user
        fragmentSMSBinding.phoneNumber.text = phone_number
        fragmentSMSBinding.sendMessage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(root.context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    sendMessage()
                } else {
                    requestPermissions(arrayOf(Manifest.permission.SEND_SMS), 1)
                }
            }

        }

        fragmentSMSBinding.clousSms.setOnClickListener {
            findNavController().popBackStack()
        }
        return root
    }
    private fun sendMessage() {
        var message = fragmentSMSBinding.messageSmsText.text.toString().trim()
        if (message.isNullOrBlank()) {
            Toast.makeText(root.context, "Iltimos yubotiladigan malumotni kiriting", Toast.LENGTH_SHORT).show()
        } else if (message.isNotBlank()) {
            try {
                var smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phone_number, null, message, null, null)
                fragmentSMSBinding.messageSmsText.setText("")
                Toast.makeText(root.context, "Message yuborildi", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(root.context, "Message jo`natilmadi", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            sendMessage()
        } else {
            Toast.makeText(root.context, "Permission Denied!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SMSFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SMSFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}