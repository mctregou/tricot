package com.tregouet.tricot.module

import android.app.Application
import android.content.Intent
import android.util.Log
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Image
import com.tregouet.tricot.model.Rule
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.NotificationService
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.realm.*


/**
 * Created by mariececile.tregouet on 11/01/2018.
 */
class Tricot : Application() {

    override fun onCreate() {
        super.onCreate()

        initDatabase()

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
                .initialData {realm ->
                    Log.i("Realm", "initialData");
                    realm.insert(Rule(1, "Point de riz", 1, 0, "Alternez une maille à l'endroit et une maille à l'envers. Pour le deuxième rang, inversez : au lieu de tricoter une maille à l'endroit puis une maille à l'envers, alternez une maille à l'envers et une maille à l'endroit"));
                    realm.insert(Rule(2, "Torsade", 8, 7, "Pour une torsade sur 12 mailles : Tricotez un rang à l'endroit, un rang à l'envers, ...etc jusqu'au 6ème rang inclus. Au 7ème rang, faites passer 6 mailles sur une autre aiguille, devant l'ouvrage. Tricotez les 6 mailles restantes à l'endroit, puis les 6 mailles sur l'aiguille en attente à l'endroit. Tricotez le 8ème rang à l'envers"));
                    realm.insert(Rule(3, "Bambou", 2, 1, "Le point bambou se construit sur deux mailles : Passez le fil devant l'aiguille de droite. Tricotez les deux mailles à l'endroit, faites passer le fil précédemment mis devant l'aiguille par dessus les deux mailles tricotées. Tricoter le deuxième rang à l'envers"));
                    realm.insert(Rule(4, "Point corde", 4, 3, "Tricotez une maille à l'envers, deux mailles à l'endroit et une maille à l'envers. Au 3ème rang, tricotez une maille à l'envers, puis faites passer une maille sur une autre aiguille, sur le devant de l'ouvrage. Tricotez une maille à l'endroit, puis la maille mise en attente. Finissez le point avec une maille à l'envers."));

                    realm.insert(Image(0, Constants.STITCH_IMAGE, 1, "", R.drawable.point_riz));
                    realm.insert(Image(1, Constants.STITCH_IMAGE, 2, "", R.drawable.point_torsade));
                    realm.insert(Image(2, Constants.STITCH_IMAGE, 3, "", R.drawable.point_bambou));
                    realm.insert(Image(3, Constants.STITCH_IMAGE, 4, "", R.drawable.point_corde));
                }
                .build()
        Realm.setDefaultConfiguration(config)
    }

    /**
     * Initialization of the custom font
     */
    private fun initDefaultFont() {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Summer_in_December.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
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