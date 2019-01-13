package com.tregouet.knitting_images.model

import com.tregouet.knitting_images.utils.realm.RealmInt
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

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
    var currentRank:Int=1,

    /**
     * Timestamp of the last opening date
     */
    var lastSeen:Long=Date().time,

    /**
     * Array of ids of stitches
     */
    var stitches:RealmList<RealmInt> = RealmList(),

    /**
     * Description of the step
     */
    var description: String?=null

) : RealmObject()