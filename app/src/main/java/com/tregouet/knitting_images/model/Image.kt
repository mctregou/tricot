package com.tregouet.knitting_images.model

import com.tregouet.knitting_images.utils.RealmManager
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

/**
 * Created by mariececile.tregouet on 13/01/2018.
 */
open class Image(

    /**
     * Id of the image
     */
    @PrimaryKey
    var id: Int?=0,

    /**
    * Type of element
    */
    var type: Int?=0,

    /**
     * Id of the element
     */
    var elementId: Int?=0,

    /**
     * Url of the image
     */
    var url: String?=null
) : RealmObject(), Serializable {

    /**
     * Calculate all ranks of the project
     */
    fun getRanksNumber() : Int {
        var ranks = 0
        val steps = RealmManager().createStepDao().loadByProjectId(id!!)
        for (step in steps){
            ranks = step.currentRank.minus(1)
        }
        return ranks
    }
}