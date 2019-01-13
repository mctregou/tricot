package com.tregouet.knitting_images.module.stitches

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.TranslateAnimation
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Rule
import com.tregouet.knitting_images.module.base.BaseActivity
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
import kotlinx.android.synthetic.main.activity_stitches.*
import kotlinx.android.synthetic.main.popup_create_rule.*


class StitchesActivity : BaseActivity() {

    private var adapter: StitchesAdapter? = null
    private var stitches: ArrayList<Rule> = ArrayList()

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
}