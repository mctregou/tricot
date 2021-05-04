package com.tregouet.tricot.utils

/**
 * Created by mariececile.tregouet on 11/01/2018.
 */
class Constants {

    companion object {

        val PROJECT_ID = "project_id"
        val STEP_ID = "step_id"
        val TUTO_TEXT = "tuto_text"
        val TUTO_IMAGE_RESOURCE = "tuto_image_resource"

        val ACTION_PLUS = "action_plus"
        val ACTION_MINUS = "action_minus"
        val MAIN_ACTION = "main_action"

        //Images element type
        val IMAGE_TYPE = "image_type"
        val ELEMENT_ID = "element_id"
        val IMAGE = "image"
        val POSITION = "position"
        val CUSTOM_DRAWABLE = "custom_drawable"
        val PROJECT_IMAGE = 1
        val STEP_IMAGE = 2
        val STITCH_IMAGE = 3

        // Images options
        val TAKE_PHOTO_REQUEST = 100
        val CHOOSE_PHOTO_REQUEST = 101

        //Step table columns
        const val TABLE_STEP_ID = "id"
        const val TABLE_STEP_LAST_SEEN_FIELD = "lastSeen"
        const val TABLE_STEP_PROJECT_ID = "projectId"

        //Rule table columns
        const val TABLE_RULE_ID = "id"
        const val TABLE_RULE_STEP_ID = "stepId"
        const val TABLE_RULE_PROJECT_ID = "projectId"

        //Reduction item table columns
        const val TABLE_REDUCTION_ITEM_ID = "id"
        const val TABLE_REDUCTION_ITEM_REDUCTION_ID = "reductionId"

        //Reduction table columns
        const val TABLE_REDUCTION_ID = "id"
        const val TABLE_REDUCTION_STEP_ID = "stepId"

        //Project table columns
        const val TABLE_PROJECT_ID = "id"
        const val TABLE_PROJECT_NAME = "name"

        //Image table columns
        const val TABLE_IMAGE_ID = "id"
        const val TABLE_IMAGE_TYPE = "type"
        const val TABLE_IMAGE_ELEMENT_ID = "elementId"
    }
}