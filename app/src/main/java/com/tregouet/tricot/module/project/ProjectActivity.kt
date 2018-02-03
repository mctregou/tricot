package com.tregouet.tricot.module.project

import android.animation.ValueAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.TranslateAnimation
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.model.Step
import com.tregouet.tricot.module.base.BaseActivity
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.RealmManager
import com.tregouet.tricot.utils.Utils
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.popup_add_project.*
import kotlinx.android.synthetic.main.popup_add_step.*

class ProjectActivity : BaseActivity() {

    var adapter: StepsAdapter? = null
    var project: Project? = null
    var steps: ArrayList<Step> = ArrayList()

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)

        fab.setOnClickListener { _ ->
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_step)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.validate_step.setOnClickListener {
                if (dialog.step_title.text.toString() != "" && dialog.size.text.toString() != "") {
                    RealmManager().open()
                    val index = RealmManager().createStepDao().nextId()
                    RealmManager().createStepDao().save(Step(index, intent.getIntExtra(Constants().PROJECT_ID, 0), dialog.step_title.text.toString(), dialog.size.text.toString()))
                    RealmManager().close()
                    getSteps()
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()

        RealmManager().open()
        project = RealmManager().createProjectDao().loadBy(intent.getIntExtra(Constants().PROJECT_ID, 0))
        RealmManager().close()
        subtitle.text = project?.name

        getSteps()

        settings.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_project)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.project_title.setText(project?.name)
            dialog.update_project_buttons.visibility = View.VISIBLE
            dialog.validate_project.visibility = View.GONE
            dialog.update_project.setOnClickListener {
                if (dialog.project_title.text.toString() != "") {
                    project?.name = dialog.project_title.text.toString()
                    RealmManager().open()
                    RealmManager().createProjectDao().save(project)
                    RealmManager().close()
                    subtitle.text = project?.name
                    getSteps()
                }
                dialog.dismiss()
            }
            dialog.update_project_buttons.visibility = View.VISIBLE
            dialog.validate_project.visibility = View.GONE
            dialog.delete_project.setOnClickListener {
                dialog.dismiss()

                val confirmationPopup = Utils().showDialog(this, R.string.warning, R.string.delete_project_confirmation, View.OnClickListener {
                    RealmManager().open()
                    RealmManager().createRuleDao().removeByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0))
                    RealmManager().createStepDao().removeByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0))
                    RealmManager().createProjectDao().removeById(intent.getIntExtra(Constants().PROJECT_ID, 0))
                    RealmManager().close()
                    onBackPressed()
                })
                confirmationPopup.show()
            }
            dialog.show()
        }
    }

    /**
     * Get steps from the database
     */
    private fun getSteps() {
        RealmManager().open()
        steps = ArrayList(RealmManager().createStepDao().loadByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0)).toList())
        RealmManager().close()

        steps_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = StepsAdapter(this, steps)
        steps_recyclerview.adapter = adapter

        if (steps.isEmpty()){
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
}