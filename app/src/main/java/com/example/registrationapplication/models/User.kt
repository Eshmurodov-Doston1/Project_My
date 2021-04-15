package com.example.registrationapplication.models

import io.realm.RealmObject
import io.realm.annotations.Required
import java.io.Serializable

open class User:RealmObject(),Serializable{
    companion object{
        var NAME_SURNAME="name"
        var PHONE_NUMBER="phone_number"
        var COUNTRY="country"
        var ADDRESS="address"
        var PASSWORD="password"
    }
    @Required
    var name:String?=null
    @Required
    var phone_number:String?=null
    @Required
    var country:String?=null
    @Required
    var address:String?=null
    @Required
    var password:String?=null
    @Required
    var image:String?=null
}