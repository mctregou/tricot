package com.tregouet.tricot.model

import com.tregouet.tricot.utils.RealmManager
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

    fun getRanksNumber() : Int {
        var ranks = 0
        val steps = RealmManager.createStepDao().loadByProjectId(id!!)
        for (step in steps){
            ranks = step.currentRank.minus(1)
        }
        return ranks
    }
}