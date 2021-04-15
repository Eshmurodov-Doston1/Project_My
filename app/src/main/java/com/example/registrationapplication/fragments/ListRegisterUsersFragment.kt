package com.example.registrationapplication.fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListPopupWindow
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentContainer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.example.registrationapplication.R
import com.example.registrationapplication.adapters.AdapterRealm
import com.example.registrationapplication.databinding.FragmentListRegisterUsersBinding
import com.example.registrationapplication.databinding.ItemBottomsheetdialogBinding
import com.example.registrationapplication.models.User
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.realm.Realm

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListRegisterUsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListRegisterUsersFragment : Fragment() {
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
    lateinit var fragmentListRegisterUsersBinding: FragmentListRegisterUsersBinding
    lateinit var root:View
    lateinit var realm: Realm
    lateinit var adapterRealm:AdapterRealm
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       fragmentListRegisterUsersBinding = FragmentListRegisterUsersBinding.inflate(inflater,container,false)
        root = fragmentListRegisterUsersBinding.root
        realm = Realm.getDefaultInstance()
        var calback = object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,calback)

        return root
    }

    override fun onResume() {
        super.onResume()
        realm = Realm.getDefaultInstance()
        val findAll = realm.where(User::class.java).findAll()
        findAll.sortedBy { it.name }
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        adapterRealm = AdapterRealm(root.context,findAll,object:AdapterRealm.OnItemClickListener{
            override fun onItemMenuClick(user: User, position: Int, image: ImageView) {
                var listItems = arrayOf("O'zgartirish","O`chirish")
                var popUpAdapter:ArrayAdapter<String> = ArrayAdapter(
                        requireContext(),
                        R.layout.popup_menu,
                        R.id.text1,
                        listItems
                )
                var albumPopup = ListPopupWindow(requireContext())
                albumPopup.setContentWidth(500)
                albumPopup.setAdapter(popUpAdapter)
                albumPopup.height = ListPopupWindow.WRAP_CONTENT
                albumPopup.anchorView = image
                var backGraund = ContextCompat.getDrawable(root.context,R.drawable.popup_menu)
                albumPopup.setBackgroundDrawable(backGraund)
                albumPopup.isModal=true
                albumPopup.setDropDownGravity(Gravity.END)
                albumPopup.setOnItemClickListener { parent, view, position, id ->
                    when(position){
                        0->{
                            var bundle = Bundle()
                            bundle.putSerializable("user",user)
                            var navOptions = NavOptions.Builder()
                            navOptions.setEnterAnim(R.anim.enter)
                            navOptions.setExitAnim(R.anim.exite)
                            navOptions.setPopEnterAnim(R.anim.pop_enter)
                            navOptions.setPopExitAnim(R.anim.pop_exite)
                            findNavController().navigate(R.id.editeUserFragment,bundle,navOptions.build())
                            albumPopup.dismiss()
                        }
                        1->{
                            realm.executeTransaction {
                                user.deleteFromRealm()
                                adapterRealm.notifyItemRemoved(position)
                                adapterRealm.notifyItemRangeChanged(position,findAll.size)
                            }
                            albumPopup.dismiss()
                        }
                    }
                }
                albumPopup.show()
            }

            override fun onItemOpGroup(user: User, position: Int) {
                var bottomSheetDialog = BottomSheetDialog(root.context,R.style.BottomSheetDialogThem)
                var itemBottomsheetdialogBinding = ItemBottomsheetdialogBinding.inflate(
                    LayoutInflater.from(root.context),
                    null,
                    false
                )
                itemBottomsheetdialogBinding.imageUser.setImageURI(Uri.parse(user.image))
                itemBottomsheetdialogBinding.nameUser.text = user.name
                itemBottomsheetdialogBinding.userCountry.text = "${user.address},${user.country}"
                itemBottomsheetdialogBinding.phoneCall.setOnClickListener {
                    askPermission(Manifest.permission.CALL_PHONE){
                        var intent = Intent(Intent.ACTION_CALL)
                        intent.setData(Uri.parse("tel:${user.phone_number}"))
                        startActivity(intent)
                        bottomSheetDialog.dismiss()
                    }.onDeclined {
                        if (it.hasDenied()){
                            AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                                .setPositiveButton("Ok"){ dialog,which->
                                    it.askAgain()
                                }.setNegativeButton("No"){ dialog,which->
                                    dialog.dismiss()
                                }
                        }
                        if (it.hasForeverDenied()){
                            it.goToSettings()
                        }
                    }
                }
                itemBottomsheetdialogBinding.phoneCallSms.setOnClickListener {
                    askPermission(Manifest.permission.SEND_SMS){
                        var bundle = Bundle()
                        bundle.putString("name",user.name)
                        bundle.putString("phone_number",user.phone_number)
                        var navOptions = NavOptions.Builder()
                        navOptions.setEnterAnim(R.anim.enter)
                        navOptions.setExitAnim(R.anim.exite)
                        navOptions.setPopEnterAnim(R.anim.pop_enter)
                        navOptions.setPopExitAnim(R.anim.pop_exite)
                        findNavController().navigate(R.id.SMSFragment,bundle,navOptions.build())
                        bottomSheetDialog.dismiss()
                    }.onDeclined {
                        if (it.hasDenied()){
                            AlertDialog.Builder(root.context).setMessage("Iltimos dostup bering yo`qsa sizga yordam bera olmayman")
                                .setPositiveButton("Ok"){ dialog,which->
                                    it.askAgain()
                                }.setNegativeButton("No"){ dialog,which->
                                    dialog.dismiss()
                                }
                        }
                        if (it.hasForeverDenied()){
                            it.goToSettings()
                        }
                    }
                }
                bottomSheetDialog.setContentView(itemBottomsheetdialogBinding.root)
                bottomSheetDialog.show()
            }

        })
        fragmentListRegisterUsersBinding.realmRecyclerView.setAdapter(adapterRealm)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListRegisterUsersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ListRegisterUsersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}