package com.tregouet.knitting.module.step

import android.Manifest
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.TranslateAnimation
import com.tregouet.knitting.R
import com.tregouet.knitting.model.Rule
import com.tregouet.knitting.model.Step
import com.tregouet.knitting.module.base.BaseActivity
import com.tregouet.knitting.module.base.UpdateNotification
import com.tregouet.knitting.module.base.UpdateStep
import com.tregouet.knitting.utils.Constants
import com.tregouet.knitting.utils.RealmManager
import com.tregouet.knitting.utils.Utils
import kotlinx.android.synthetic.main.activity_step.*
import kotlinx.android.synthetic.main.popup_add_rule.*
import kotlinx.android.synthetic.main.popup_add_step.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.tregouet.knitting.module.stitches.StitchesActivity
import io.realm.RealmList
import java.util.*
import kotlin.collections.ArrayList
import com.tregouet.knitting.utils.realm.RealmInt




class StepActivity : BaseActivity() {

    private var adapter: RulesAdapter? = null
    private var currentRuleAdapter: CurrentRulesAdapter? = null
    private var step: Step? = null
    private var rules: ArrayList<Rule> = ArrayList()
    private val REQUEST_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)

        minus.setOnClickListener { minus() }
        plus.setOnClickListener { plus() }
        add_rule.setOnClickListener { addRule() }
        showNotification()

        updateVisibilityDescription()
    }

    override fun onResume() {
        super.onResume()

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }

        updateStep()

        settings.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_step)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.step_title.setText(step?.name)
            dialog.size.setText(step?.end)
            dialog.description_texte.setText(step?.description)
            dialog.update_step.setOnClickListener {
                if (dialog.step_title.text.toString() != "" && dialog.size.text.toString() != "") {
                    step?.name = dialog.step_title.text.toString()
                    step?.end = dialog.size.text.toString()
                    step?.description = dialog.description_texte.text.toString()
                    RealmManager().open()
                    RealmManager().createStepDao().save(step)
                    RealmManager().close()
                    title = step?.name
                    end.text = getString(R.string.step_length, step?.end.toString())
                    description.text = step?.description
                }
                dialog.dismiss()
            }
            dialog.update_buttons.visibility = View.VISIBLE
            dialog.validate_step.visibility = View.GONE
            dialog.delete_step.setOnClickListener {
                dialog.dismiss()

                val confirmationDialog = Utils().showDialog(this, R.string.warning, R.string.step_delete_confirmation, View.OnClickListener {
                    RealmManager().open()
                    RealmManager().createRuleDao().removeByStepId(intent.getIntExtra(Constants().STEP_ID, 0))
                    RealmManager().createStepDao().removeById(intent.getIntExtra(Constants().STEP_ID, 0))
                    RealmManager().close()
                    onBackPressed()
                })
                confirmationDialog.show()
            }
            dialog.show()
        }
    }

    override fun onStop() {
        super.onStop()

        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }

    fun updateStep() {
        RealmManager().open()
        step = RealmManager().createStepDao().loadBy(intent.getIntExtra(Constants().STEP_ID, 0))
        step?.lastSeen = Date().time
        RealmManager().createStepDao().save(step)
        RealmManager().close()

        //toolbar.title = step?.name
        step_name.text = getString(R.string.step, step?.name)
        current_rank.text = step!!.currentRank.toString()
        end.text = getString(R.string.step_length, step?.end.toString())
        description.text = step!!.description

        getRules()

        checkRule()

        rules_recyclerview.isFocusable = false
        constraint_layout.requestFocus()
    }

    @Subscribe
    fun onUpdateStepEventReceived(event: UpdateStep) {
        updateStep()
    }

    /**
     * Show or hide notification
     */
    fun showNotification(){
        notification.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                EventBus.getDefault().post(UpdateNotification(true, intent.getIntExtra(Constants().PROJECT_ID, 0), step?.id!!))
            } else {
                EventBus.getDefault().post(UpdateNotification(false))
            }
        }
    }

    fun getRules() {
        RealmManager().open()
        rules = ArrayList(RealmManager().createRuleDao().loadByIds(step?.stitches?.toList()).toList())
        RealmManager().close()

        rules_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RulesAdapter(this, rules)
        rules_recyclerview.adapter = adapter

        if (rules.isEmpty()) {
            no_step_layout.visibility = View.VISIBLE
            startAnimation()
        } else {
            no_step_layout.visibility = View.GONE
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
        arrow.startAnimation(animation)
    }

    private fun addRule() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_add_rule)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.stitches_recyclerview.layoutManager = LinearLayoutManager(this)
        val allStitches = ArrayList(RealmManager().createRuleDao().loadAll().toList())
        val adapter = AddStitchesAdapter(allStitches, rules)
        dialog.stitches_recyclerview.adapter = adapter

        if (allStitches.isEmpty()) {
            dialog.no_stitch.visibility = View.VISIBLE
            dialog.no_stitch.setOnClickListener { startActivity(Intent(this, StitchesActivity::class.java)) }
        } else {
            dialog.no_stitch.visibility = View.GONE
        }

        dialog.cancel.setOnClickListener { dialog.dismiss() }

        dialog.update_stitches.setOnClickListener {
                RealmManager().open()
                step?.stitches?.clear()
                for (id in adapter.getSelectedStitchIds()){
                    step?.stitches?.add(RealmInt(id))
                }
                RealmManager().createStepDao().save(step)
                RealmManager().close()

                getRules()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
        } else {
            launchCamera()
        }
    }

    private fun launchCamera() {

    }

    private fun minus() {
        if (step?.currentRank!! > 1) {
            RealmManager().open()
            step!!.currentRank = (step!!.currentRank - 1)
            RealmManager().createStepDao().save(step)
            RealmManager().close()
            current_rank.text = step!!.currentRank.toString()

            checkRule()

            EventBus.getDefault().post(UpdateNotification(true, intent.getIntExtra(Constants().PROJECT_ID, 0), step?.id!!))
        }
    }

    private fun plus() {
        RealmManager().open()
        step?.currentRank = (step?.currentRank!! + 1)
        RealmManager().createStepDao().save(step)
        RealmManager().close()
        current_rank.text = step!!.currentRank.toString()

        checkRule()

        EventBus.getDefault().post(UpdateNotification(true, intent.getIntExtra(Constants().PROJECT_ID, 0), step?.id!!))
    }

    private fun updateVisibilityDescription() {
        description_layout.setOnClickListener {
            if (description.visibility == View.VISIBLE) {
                description.visibility = View.GONE
            } else {
                description.visibility = View.VISIBLE
            }
        }
    }

    private fun checkRule() {
        val currentRules = ArrayList<Rule>()

        rules.filterTo(currentRules) { (step?.currentRank!! - it.offset!!) % it.frequency!! == 0 }

        current_rules_recyclerview.layoutManager = LinearLayoutManager(this)
        currentRuleAdapter = CurrentRulesAdapter(currentRules)
        current_rules_recyclerview.adapter = currentRuleAdapter

        if (currentRules.isEmpty()) {
            special_title.visibility = View.GONE
            current_rules_recyclerview.visibility = View.GONE
        } else {
            special_title.visibility = View.VISIBLE
            current_rules_recyclerview.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera()
                }
                return
            }
        }
    }

}