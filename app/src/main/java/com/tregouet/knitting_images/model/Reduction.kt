package com.tregouet.knitting_images.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
open class Reduction(

    /**
     * Id of the reduction
     */
    @PrimaryKey
    var id: Int?=0,

    /**
     * Id of the step
     */
    var stepId:Int?=0,

    /**
     * Offset rank
     */
    var offsetRank:Int?=1,

    /**
     * Frequency of the reduction
     */
    var frequency:Int?=1

) : RealmObject()