package com.example.registrationapplication.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.registrationapplication.R
import com.example.registrationapplication.databinding.FragmentCreateBinding
import com.example.registrationapplication.models.User
import com.github.florent37.runtimepermission.kotlin.askPermission
import io.realm.Realm

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateFragment : Fragment() {
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
    lateinit var fragmentCreateBinding: FragmentCreateBinding
    lateinit var root:View
    lateinit var realm: Realm
    var bool:Boolean=false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCreateBinding = FragmentCreateBinding.inflate(inflater,container,false)
        root = fragmentCreateBinding.root
        askPermission(android.Manifest.permission.CAMERA){
            bool=false
            realm = Realm.getDefaultInstance()
            fragmentCreateBinding.registration.setOnClickListener {
                var bundle = Bundle()
                var navoptions = NavOptions.Builder()
                navoptions.setEnterAnim(R.anim.enter)
                navoptions.setExitAnim(R.anim.exite)
                navoptions.setPopEnterAnim(R.anim.pop_enter)
                navoptions.setPopExitAnim(R.anim.pop_exite)
                findNavController().navigate(R.id.registrationFragment,bundle,navoptions.build())
            }
            fragmentCreateBinding.openSystem.setOnClickListener {
                val phone_number= fragmentCreateBinding.phoneNumber1.text.toString()
                val password = fragmentCreateBinding.password1.text.toString()
                if (phone_number.isNullOrBlank()){
                    Toast.makeText(root.context, "Iltimos telefon raqamni kiriting", Toast.LENGTH_SHORT).show()
                }else if (password.isNullOrBlank()){
                    Toast.makeText(root.context, "Iltimos parolni kiriting", Toast.LENGTH_SHORT).show()
                }else if (phone_number.isNotBlank() && password.isNotBlank()){
                    var isHave = false
                    var index = -1
                    val findAll = realm.where(User::class.java).findAll()
                    for (i in findAll) {
                        if (i.phone_number.equals(phone_number, ignoreCase = true) && i.password.equals(password, ignoreCase = true)){
                            isHave=true
                            break
                        }
                    }
                    if (isHave){
                        var bundle = Bundle()
                        var navOptions = NavOptions.Builder()
                        navOptions.setEnterAnim(R.anim.enter)
                        navOptions.setExitAnim(R.anim.exite)
                        navOptions.setPopEnterAnim(R.anim.pop_enter)
                        navOptions.setPopExitAnim(R.anim.pop_exite)
                        Toast.makeText(root.context, "Xush kelibsiz", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.listRegisterUsersFragment,bundle,navOptions.build())
                        fragmentCreateBinding.phoneNumber1.setText("")
                        fragmentCreateBinding.password1.setText("")
                    }else{
                        Toast.makeText(root.context, "Bunday Foydalanuvchi mavjud emas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }.onDeclined {
                it ->
            if (it.hasDenied()) {
                AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, which ->
                        it.askAgain()
                    }.setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }.show()
            }
            if (it.hasForeverDenied()) {
                AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, which ->
                        bool = true
                        it.goToSettings()
                    }.show()
            }

        }

        return root
    }

    override fun onResume() {
        super.onResume()
        if (bool) {
            if (ContextCompat.checkSelfPermission(
                    root.context,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    root.context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fragmentCreateBinding.registration.setOnClickListener {
                    var bundle = Bundle()
                    var navOptions = NavOptions.Builder()
                    navOptions.setEnterAnim(R.anim.enter)
                    navOptions.setExitAnim(R.anim.exite)
                    navOptions.setPopEnterAnim(R.anim.pop_enter)
                    navOptions.setPopExitAnim(R.anim.pop_exite)
                    findNavController().navigate(
                        R.id.registrationFragment,
                        bundle,
                        navOptions.build()
                    )
                }
            }else{
                AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialog, which ->
                        val fragmentActivity: FragmentActivity = requireActivity()
                        if (fragmentActivity != null) {
                            fragmentActivity.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", fragmentActivity.packageName, null)
                                )
                            )
                        }
                    }.show()
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}