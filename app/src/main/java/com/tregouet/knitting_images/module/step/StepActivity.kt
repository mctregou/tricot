package com.tregouet.knitting_images.module.step

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
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Rule
import com.tregouet.knitting_images.model.Step
import com.tregouet.knitting_images.module.base.BaseActivity
import com.tregouet.knitting_images.module.base.UpdateNotification
import com.tregouet.knitting_images.module.base.UpdateStep
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
import com.tregouet.knitting_images.utils.Utils
import kotlinx.android.synthetic.main.activity_step.*
import kotlinx.android.synthetic.main.popup_add_rule.*
import kotlinx.android.synthetic.main.popup_add_step.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.Log
import com.tregouet.knitting_images.model.ReductionItem
import com.tregouet.knitting_images.module.stitches.StitchesActivity
import java.util.*
import kotlin.collections.ArrayList
import com.tregouet.knitting_images.utils.realm.RealmInt
import kotlinx.android.synthetic.main.popup_add_reduction.*
import com.tregouet.knitting_images.model.Reduction


class StepActivity : BaseActivity() {

    private var adapter: RulesAdapter? = null
    private var reductionAdapter : ReductionItemsAdapter? =null
    private var currentRuleAdapter: CurrentRulesAdapter? = null
    private var step: Step? = null
    private var rules: ArrayList<Rule> = ArrayList()
    private var reduction: Reduction? = null
    private var reductionItems: ArrayList<ReductionItem> = ArrayList()
    private var reductionItemsPopup: ArrayList<ReductionItem> = ArrayList()
    private val REQUEST_CAMERA = 100
    private var reductionDialog : Dialog? = null
    private var reductionItemsAdapter : ReductionItemsForPopupAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)

        minus.setOnClickListener { minus() }
        plus.setOnClickListener { plus() }
        add_rule.setOnClickListener { addRule() }
        add_reduction.setOnClickListener { addReduction() }
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

        getReductions()

        checkReductions()

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
            stitches_title.visibility = View.GONE
        } else {
            stitches_title.visibility = View.VISIBLE
        }

        displayInfoAnimation()
    }

    /**
     * Get reductions for this step
     */
    fun getReductions() {
        RealmManager().open()
        reduction = RealmManager().createReductionDao().loadByStepId(step?.id!!)
        RealmManager().close()

        reductionItems = ArrayList()
        if (reduction != null){
            reductions_description.text = String.format("Tous les %s rangs, à partir du rang %s", reduction?.frequency.toString(), reduction?.offsetRank.toString())

            RealmManager().open()
            val reductionItemsDB = RealmManager().createReductionItemDao().loadByReductionId(reduction?.id!!)
            RealmManager().close()
            if (reductionItemsDB != null){
                reductionItems = ArrayList(reductionItemsDB.toList())
            }
        }

        reductions_recyclerview.layoutManager = LinearLayoutManager(this)
        reductionAdapter = ReductionItemsAdapter(reductionItems)
        reductions_recyclerview.adapter = reductionAdapter

        if (reductionItems.isEmpty()){
            reductions_layout.visibility = View.GONE
        } else {
            reductions_layout.visibility = View.VISIBLE
        }

        displayInfoAnimation()
    }

    fun displayInfoAnimation() {
        if (rules.isEmpty() && reductionItems.isEmpty()) {
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
        fam.close(true)
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_add_rule)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.stitches_recyclerview.layoutManager = LinearLayoutManager(this)
        val allStitches = ArrayList(RealmManager().createRuleDao().loadAll().toList())
        val adapter = AddStitchesAdapter(allStitches, rules)
        dialog.stitches_recyclerview.adapter = adapter

        if (allStitches.isEmpty()) {
            dialog.no_stitch.visibility = View.VISIBLE
            dialog.update_stitches.visibility = View.GONE
            dialog.new_stitch.visibility = View.GONE
            dialog.no_stitch.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(this, StitchesActivity::class.java)) }
        } else {
            dialog.no_stitch.visibility = View.GONE
            dialog.update_stitches.visibility = View.VISIBLE
            dialog.new_stitch.visibility = View.VISIBLE
        }

        dialog.cancel.setOnClickListener { dialog.dismiss() }

        dialog.new_stitch.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, StitchesActivity::class.java)) }

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

    private fun addReduction(){
        fam.close(true)
        reductionDialog = Dialog(this)
        reductionDialog?.setContentView(R.layout.popup_add_reduction)
        reductionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        reductionDialog?.reduction_items_recyclerview?.layoutManager = LinearLayoutManager(this)

        if (reduction != null){
            reductionDialog?.frequency?.setText(reduction?.frequency.toString())
            reductionDialog?.offset?.setText(reduction?.offsetRank.toString())
        }

        reductionItemsPopup = reductionItems
        reductionItemsAdapter = ReductionItemsForPopupAdapter(this, reductionItemsPopup)
        reductionDialog?.reduction_items_recyclerview?.adapter = reductionItemsAdapter

        reductionDialog?.add?.setOnClickListener { addReductionItem() }
        reductionDialog?.cancel_reduction?.setOnClickListener { reductionDialog?.dismiss() }
        reductionDialog?.update_reduction?.setOnClickListener {
            saveReduction()
        }

        reductionDialog?.show()
    }
    private fun addReductionItem() {
        if (reductionDialog?.edittext_times?.text!!.isEmpty() || reductionDialog?.edittext_numberOfMesh?.text!!.isEmpty() || reductionDialog?.edittext_side?.text!!.isEmpty()){
            return
        }

        val index = reductionItems.size + 1
        val reductionItem = ReductionItem(0, null, index, Integer.parseInt(reductionDialog?.edittext_times?.text.toString()), Integer.parseInt(reductionDialog?.edittext_numberOfMesh?.text.toString()), reductionDialog?.edittext_side?.text.toString())
        reductionItemsPopup.add(reductionItem)
        Log.i("TESTMC", reductionItemsPopup.size.toString())
        reductionItemsAdapter?.setReductionItems(reductionItems)
        reductionDialog?.textview_position?.text = (index + 1).toString()
        reductionDialog?.edittext_times?.setText("")
        reductionDialog?.edittext_numberOfMesh?.setText("")
        reductionDialog?.edittext_side?.setText("")

    }

    private fun saveReduction() {
        Log.i("StepActivity", "saveReduction")

        if (reduction == null){
            Log.i("StepActivity", "reduction not null")
            reduction = Reduction()
            val index = RealmManager().createReductionDao().nextId()
            reduction?.id = index
            reduction?.stepId = step?.id
        }

        reduction?.frequency = Integer.parseInt(reductionDialog?.frequency?.text.toString())
        reduction?.offsetRank = Integer.parseInt(reductionDialog?.offset?.text.toString())

        RealmManager().open()
        RealmManager().createReductionDao().save(reduction)
        RealmManager().close()

        RealmManager().open()
        RealmManager().createReductionItemDao().removeByReductionId(reduction?.id!!)
        RealmManager().close()

        Log.v("StepActivity", "size reductionItemsPopup=" + reductionItemsPopup.size +" / " + reductionItems.size)
        for (reductionItem in reductionItemsPopup){
            val reductionItemIndex = RealmManager().createReductionItemDao().nextId()
            reductionItem.reductionId = reduction?.id
            reductionItem.id = reductionItemIndex
            Log.v("StepActivity create ReductionItem", "reductionItemId = " + reductionItemIndex)
            RealmManager().createReductionItemDao().save(reductionItem)
            RealmManager().close()
        }

        getReductions()

        reductionDialog?.dismiss()
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
            checkReductions()

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
        checkReductions()

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

    private fun checkReductions() {
        Log.i("checkReductions", "start")
        if (reduction != null
                && reductionItems.isNotEmpty()
                && step!!.currentRank - reduction?.offsetRank!! >= 0
                && ((step!!.currentRank - reduction?.offsetRank!!) % reduction?.frequency!! == 0)){
            Log.i("checkReductions", "conditions ok")
            val position = (step!!.currentRank - reduction?.offsetRank!!) / reduction?.frequency!!
            Log.i("checkReductions", "position = " + position.toString())
            var count = 0
            for (reductionItem in reductionItems){
                if ( position >= (count + reductionItem.times)){
                    Log.i("checkReductions", "position sup => count =  " + count.toString() + " / size = " + reductionItem.times)
                    count += reductionItem.times
                } else {
                    Log.i("checkReductions", "position not sup => count =  " + count.toString() + " / size = " + reductionItem.times)
                    current_reduction_title.visibility = View.VISIBLE
                    current_reduction_description.visibility = View.VISIBLE
                    current_reduction_description.text = String.format("• %d fois %d mailles du côté %s", reductionItem.times, reductionItem.numberOfMesh, reductionItem.side)
                    break
                }
            }
        } else {
            current_reduction_title.visibility = View.GONE
            current_reduction_description.visibility = View.GONE
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