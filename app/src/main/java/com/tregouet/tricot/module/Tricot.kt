package com.tregouet.tricot.module

import android.app.Application
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import com.tregouet.tricot.R
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.DynamicRealm
import io.realm.RealmMigration
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


/**
 * Created by mariececile.tregouet on 11/01/2018.
 */
class Tricot : Application() {

    override fun onCreate() {
        super.onCreate()

        initDatabase()

        initInstabug()

        initDefaultFont()
    }

    /**
     * Initialization of the database
     */
    fun initDatabase() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .schemaVersion(0)
                .migration(MyMigration())
                .build()
        Realm.setDefaultConfiguration(config)
    }

    /**
     * Initialization of Instabug
     */
    fun initInstabug() {
        Instabug.Builder(this, "5c5a71eceb2656fd4dd3dda22c03d3d9")
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build()
        Instabug.setIntroMessageEnabled(false)
    }

    /**
     * Initialization of the custom font
     */
    fun initDefaultFont() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Summer_in_December.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())
    }

    inner class MyMigration : RealmMigration {
        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            // DynamicRealm exposes an editable schema
            val schema = realm.schema

            System.out.println("MyMigration " + oldVersion + "," + newVersion)
        }
    }

}