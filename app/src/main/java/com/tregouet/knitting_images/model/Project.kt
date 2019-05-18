package com.tregouet.knitting_images.model

import com.tregouet.knitting_images.utils.RealmManager
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
open class Project (

    /**
     * Id of the project
     */
    @PrimaryKey
    var id: Int?=0,

    /**
     * Name of the project
     */
    var name: String?=null
) : RealmObject() {

    /**
     * Calculate all ranks of the project
     */
    fun getRanksNumber() : Int {
        val steps = RealmManager().createStepDao().loadByProjectId(id!!)
        return steps.sumBy { it.currentRank.minus(1) }
    }
}