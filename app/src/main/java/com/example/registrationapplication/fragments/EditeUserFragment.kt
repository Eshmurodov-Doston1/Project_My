package com.example.registrationapplication.fragments

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.example.registrationapplication.BuildConfig
import com.example.registrationapplication.R
import com.example.registrationapplication.databinding.FragmentEditeUserBinding
import com.example.registrationapplication.databinding.ItemBottomsheetBinding
import com.example.registrationapplication.models.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.realm.Realm
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditeUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditeUserFragment : Fragment() {
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
    lateinit var fragmentEditeUserBinding: FragmentEditeUserBinding
    lateinit var root:View
    lateinit var realm:Realm
    lateinit var fileAbsolutPath:String
    lateinit var photoUri: Uri
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentEditeUserBinding = FragmentEditeUserBinding.inflate(inflater,container,false)
        root = fragmentEditeUserBinding.root
        realm = Realm.getDefaultInstance()

        var user = arguments?.getSerializable("user") as User
        fragmentEditeUserBinding.userName.setText(user.name)
        fragmentEditeUserBinding.phoneNumber1.setText(user.phone_number)
        fragmentEditeUserBinding.password1.setText(user.password)
        fragmentEditeUserBinding.address1.setText(user.address)
        fileAbsolutPath = user.image!!
        fragmentEditeUserBinding.image.setImageURI(Uri.parse(user.image))

        fragmentEditeUserBinding.imageAdd.setOnClickListener {
            var bottomSheetDialog = BottomSheetDialog(root.context,R.style.BottomSheetDialogThem)
            var itemBottomsheetBinding = ItemBottomsheetBinding.inflate(LayoutInflater.from(root.context),null,false)

            itemBottomsheetBinding.camera.setOnClickListener {
                var imageFile1 =createImageFile()
                photoUri = FileProvider.getUriForFile(root.context, BuildConfig.APPLICATION_ID,imageFile1)
                getTakeImageUser.launch(photoUri)
                bottomSheetDialog.hide()
            }

            itemBottomsheetBinding.galereya.setOnClickListener {
                picImageForGallery()
                bottomSheetDialog.hide()
            }
            bottomSheetDialog.setContentView(itemBottomsheetBinding.root)
            bottomSheetDialog.show()
        }


        fragmentEditeUserBinding.registrBtn.setOnClickListener {
            realm.executeTransaction {
                user.name = fragmentEditeUserBinding.userName.text.toString()
                user.phone_number = fragmentEditeUserBinding.phoneNumber1.text.toString()
                user.country =
                    fragmentEditeUserBinding.countryName.textView_selectedCountry.text.toString()
                user.address = fragmentEditeUserBinding.address1.text.toString()
                user.password = fragmentEditeUserBinding.password1.text.toString()
                user.image = fileAbsolutPath
                if (user.name.isNullOrBlank()) {
                    Toast.makeText(root.context, "Iltimos ismingizni kiriting", Toast.LENGTH_SHORT)
                        .show()
                } else if (user.phone_number.isNullOrBlank()) {
                    Toast.makeText(
                        root.context,
                        "Iltimos telefon raqamingizni kiriting",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (user.address.isNullOrBlank()) {
                    Toast.makeText(
                        root.context,
                        "Iltimos manzilingizni kiriting",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (user.password.isNullOrBlank()) {
                    Toast.makeText(
                        root.context,
                        "Iltimos parolingizni kiriting",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (user.name!!.isNotBlank() && user.phone_number!!.isNotBlank() && user.address!!.isNotBlank() && user.password!!.isNotBlank()) {
                    try {

                        it.insertOrUpdate(user)
                        findNavController().popBackStack()

                    } catch (e: Exception) {
                        Toast.makeText(root.context, "Nimadir kiritilmadi", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
            return root
    }
    private val getTakeImageUser = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            fragmentEditeUserBinding.image.setImageURI(photoUri)
            var openInputStream = activity?.contentResolver?.openInputStream(photoUri)
            var format = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            var file = File(activity?.filesDir, "$format.jpg")
            var fileoutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileoutputStream)
            openInputStream?.close()
            fileoutputStream.close()
            fileAbsolutPath = file.absolutePath
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        var date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        var externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$date",".jpg",externalFilesDir).apply {
            absolutePath
        }
    }

    private fun picImageForGallery() {
        getImageContent.launch("image/*")
    }

    private var  getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?:return@registerForActivityResult
        fragmentEditeUserBinding.image.setImageURI(uri)
        var openInputStream = activity?.contentResolver?.openInputStream(uri)
        var format = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
        var file = File(activity?.filesDir,"$format.jpg")
        var fileOutputStream = FileOutputStream(file)
        openInputStream?.copyTo(fileOutputStream)
        openInputStream?.close()
        fileOutputStream.close()
        fileAbsolutPath = file.absolutePath

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditeUserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                EditeUserFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}