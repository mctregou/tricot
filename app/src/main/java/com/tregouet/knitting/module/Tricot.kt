package com.tregouet.knitting.module

import android.app.Application
import com.instabug.library.Instabug
import com.instabug.library.invocation.InstabugInvocationEvent
import com.tregouet.knitting.R
import com.tregouet.knitting.utils.NotificationService
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.DynamicRealm
import io.realm.RealmMigration
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import android.content.Intent




/**
 * Created by mariececile.tregouet on 11/01/2018.
 */
class Tricot : Application() {

    override fun onCreate() {
        super.onCreate()

        initDatabase()

        initInstabug()

        initDefaultFont()

        startNotificationService()
    }

    /**
     * Initialization of the database
     */
    private fun initDatabase() {
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
    private fun initInstabug() {
        Instabug.Builder(this, "5c5a71eceb2656fd4dd3dda22c03d3d9")
                .setInvocationEvent(InstabugInvocationEvent.SHAKE)
                .build()
        Instabug.setIntroMessageEnabled(false)
    }

    /**
     * Initialization of the custom font
     */
    private fun initDefaultFont() {
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Summer_in_December.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build())
    }

    /**
     * Start notification service
     */
    private fun startNotificationService() {
        startService(Intent(this, NotificationService::class.java))
    }

    /**
     * Database migration
     */
    inner class MyMigration : RealmMigration {
        override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
            // DynamicRealm exposes an editable schema
            val schema = realm.schema

            System.out.println("MyMigration " + oldVersion + "," + newVersion)
        }
    }

    /**
     * OnTerminate
     * Stop notification service
     */
    override fun onTerminate() {
        if (NotificationService.isRunning){
            stopService(Intent(this, NotificationService::class.java))
        }

        super.onTerminate()
    }

}