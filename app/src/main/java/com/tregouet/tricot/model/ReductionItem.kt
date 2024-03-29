package com.tregouet.tricot.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
open class ReductionItem(

    /**
     * Id of the reduction
     */
    @PrimaryKey
    var id: Int?=0,

    /**
     * Id of the reduction
     */
    var reductionId:Int?=0,
    /**
     * Order of the reduction item
     */
    var order:Int=1,

    /**
     * Number of times of the reduction item
     */
    var times:Int=1,

    /**
     * Number of mesh of the reduction item
     */
    var numberOfMesh:Int=1,

    /**
     * Side of the réduction
     */
    var side:String? =null

) : RealmObject()