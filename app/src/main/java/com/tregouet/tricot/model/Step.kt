package com.tregouet.tricot.model

import com.tregouet.tricot.utils.RealmManager
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
open class Step (

    /**
     * Id of the step
     */
    @PrimaryKey
    var id: Int?=0,

    /**
     * Id of the project
     */
    var projectId:Int?=0,

    /**
     * Name of the step
     */
    var name: String?=null,

    /**
     * End of the step
     */
    var end: String?=null,

    /**
     * Current rank
     */
    var currentRank:Int=1

) : RealmObject()