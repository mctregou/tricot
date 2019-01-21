package com.tregouet.knitting_images.module.stitches

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.model.Rule
import com.tregouet.knitting_images.module.base.BaseActivity
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.ImageUtils
import com.tregouet.knitting_images.utils.RealmManager
import kotlinx.android.synthetic.main.activity_stitches.*
import kotlinx.android.synthetic.main.popup_create_rule.*


class StitchesActivity : BaseActivity() {

    private var adapter: StitchesAdapter? = null
    private var stitches: ArrayList<Rule> = ArrayList()
    var fileUri : Uri ? = null
    var stitchId : Int ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stitches)

        add_stitch.setOnClickListener { addStitch() }
    }

    override fun onResume() {
        super.onResume()

        getStitches()
    }

    fun getStitches() {
        RealmManager().open()
        stitches = ArrayList(RealmManager().createRuleDao().loadAll().toList())
        RealmManager().close()

        stitches_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = StitchesAdapter(this, stitches)
        stitches_recyclerview.adapter = adapter

        if (stitches.isEmpty()) {
            no_stitch_layout.visibility = View.VISIBLE
            startAnimation()
        } else {
            no_stitch_layout.visibility = View.GONE
        }
    }

    /**
     * Start the arrow animation
     */
    private fun startAnimation() {
        val animation = TranslateAnimation(-100f, 100f, 0f, 0f)
        animation.duration = 700
        animation.fillAfter = true
        animation.repeatCount = ValueAnimator.INFINITE
        arrow_new_stitch.startAnimation(animation)
    }

    private fun addStitch() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_create_rule)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.validate_rule.setOnClickListener {
            if (dialog.rule_title.text.toString() != ""
                    && dialog.rule_frequence.text.toString() != ""
                    && dialog.rule_description.text.toString() != "") {
                RealmManager().open()
                val index = RealmManager().createRuleDao().nextId()
                RealmManager().createRuleDao().save(Rule(index,
                        intent.getIntExtra(Constants().STEP_ID, 0),
                        intent.getIntExtra(Constants().PROJECT_ID, 0),
                        dialog.rule_title.text.toString(),
                        dialog.rule_frequence.text.toString().toInt(),
                        dialog.rule_offset.text.toString().toInt(),
                        dialog.rule_description.text.toString()
                ))
                RealmManager().close()

                getStitches()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        Log.i("StitchActivity-stitchId", stitchId.toString())
        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants().TAKE_PHOTO_REQUEST) {
            var file = ImageUtils.processCapturedPhoto(this, fileUri.toString())
            Log.i("StitchActivity-appareil", file.absolutePath)
            RealmManager().open()
            RealmManager().createImageDao().save(Image(RealmManager().createImageDao().nextId(), Constants().STITCH_IMAGE, stitchId, file.absolutePath))
            RealmManager().close()
        } else if (resultCode == Activity.RESULT_OK
                && requestCode == Constants().CHOOSE_PHOTO_REQUEST) {
            var path = ImageUtils.processAlbumPhoto(this, data)
            Log.i("StitchActivity - folder", path)
            RealmManager().open()
            RealmManager().createImageDao().save(Image(RealmManager().createImageDao().nextId(), Constants().STITCH_IMAGE, stitchId, path))
            RealmManager().close()
        } else {
            Log.i("StitchActivity-appareil", "other")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}