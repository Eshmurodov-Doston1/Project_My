package com.example.registrationapplication.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.registrationapplication.R
import com.example.registrationapplication.databinding.ItemRvBinding
import com.example.registrationapplication.models.User
import io.realm.RealmBasedRecyclerViewAdapter
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.RealmViewHolder

class AdapterRealm(context: Context,var list: RealmResults<User>,var onItemClickListener: OnItemClickListener):
    RealmBasedRecyclerViewAdapter<User,AdapterRealm.Vh>(context,list,true,false) {

    inner class Vh(var itemRvBinding: ItemRvBinding): RealmViewHolder(itemRvBinding.root){
        fun onBind(user: User,position: Int){
            itemRvBinding.imageUser.setImageURI(Uri.parse(user.image))
            itemRvBinding.nameUser.text = user.name
            itemRvBinding.userPhoneNumber.text = user.phone_number
            itemRvBinding.root.animation = AnimationUtils.loadAnimation(context, R.anim.item_anim)
            itemRvBinding.card.setOnClickListener {
                onItemClickListener.onItemOpGroup(user,position)
            }
            itemRvBinding.iconMenu.setOnClickListener {
                onItemClickListener.onItemMenuClick(user,position,itemRvBinding.iconMenu)
            }
        }
    }

    override fun onCreateRealmViewHolder(p0: ViewGroup?, p1: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(p0!!.context),p0,false))
    }

    override fun onBindRealmViewHolder(p0: Vh?, p1: Int) {
        p0!!.onBind(list[p1],p1)
    }
    interface OnItemClickListener{
        fun onItemMenuClick(user: User,position: Int,image: ImageView)
        fun onItemOpGroup(user: User,position: Int)
    }

}