package com.tregouet.knitting_images.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
open class Rule(

    /**
     * Id of the rule
     */
    @PrimaryKey
    var id: Int?=0,

    /**
     * Id of the step
     */
    var stepId:Int?=0,

    /**
     * Id of the project
     */
    var projectId:Int?=0,

    /**
     * Stitch of the rule
     */
    var stitch: String?=null,

    /**
     * Frequency of the rule
     */
    var frequency: Int?=0,

    /**
     * Offset of the rule
     */
    var offset: Int?=0,

    /**
     * Description of the rule
     */
    var description:String?=null

) : RealmObject()