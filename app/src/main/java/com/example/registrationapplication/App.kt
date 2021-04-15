package com.example.registrationapplication

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App():Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        var realmConfiguration = RealmConfiguration.Builder()
            .name("registration.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }
}