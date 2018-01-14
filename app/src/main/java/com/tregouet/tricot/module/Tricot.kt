package com.tregouet.tricot.module

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.FieldAttribute
import io.realm.RealmSchema
import io.realm.DynamicRealm
import io.realm.RealmMigration





/**
 * Created by mariececile.tregouet on 11/01/2018.
 */
class Tricot : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .schemaVersion(0)
                .migration(MyMigration())
                .build()
        Realm.setDefaultConfiguration(config)
    }

    inner class MyMigration : RealmMigration {
        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            // DynamicRealm exposes an editable schema
            val schema = realm.schema

            System.out.println("MyMigration " + oldVersion + "," + newVersion)
        }
    }

}