package com.tregouet.tricot.utils

import com.tregouet.tricot.dao.*
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.exceptions.RealmMigrationNeededException
import java.io.FileNotFoundException

/**
 * Created by mariececile.tregouet on 29/01/2018.
 */
class RealmManager {

    private var mRealm: Realm? = null


    open fun open(): Realm? {
        return try {
            mRealm = Realm.getDefaultInstance()
            mRealm
        } catch (exception: RealmMigrationNeededException) {
            val config = RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build()
            try {
                //Realm.migrateRealm(config)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            open()
        }

    }

    fun close() {
        if (mRealm != null) {
            mRealm!!.close()
        }
    }

    fun createProjectDao(): ProjectDao {
        open()
        checkForOpenRealm()
        return ProjectDao(mRealm!!)
    }

    fun createStepDao(): StepDao {
        open()
        checkForOpenRealm()
        return StepDao(mRealm!!)
    }

    fun createRuleDao(): RuleDao {
        open()
        checkForOpenRealm()
        return RuleDao(mRealm!!)
    }

    fun createReductionDao(): ReductionDao {
        open()
        checkForOpenRealm()
        return ReductionDao(mRealm!!)
    }

    fun createReductionItemDao(): ReductionItemDao {
        open()
        checkForOpenRealm()
        return ReductionItemDao(mRealm!!)
    }

    fun createImageDao(): ImageDao {
        open()
        checkForOpenRealm()
        return ImageDao(mRealm!!)
    }

    fun clear() {
        checkForOpenRealm()
        mRealm!!.executeTransaction { realm ->
            realm.deleteAll()
            //clear rest of your dao classes
        }
    }

    private fun checkForOpenRealm() {
        if (mRealm == null) {
            throw IllegalStateException("RealmManager: Realm is closed, call open() method first")
        } else if (mRealm!!.isClosed) {
            mRealm = Realm.getDefaultInstance()
        }
    }
}