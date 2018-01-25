package com.tregouet.tricot.module.project

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.model.Step
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.RealmManager
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.popup_add_project.*
import kotlinx.android.synthetic.main.popup_add_step.*

class ProjectActivity : AppCompatActivity() {

    var adapter: StepsAdapter? = null
    var project: Project? = null
    var steps: ArrayList<Step> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_step)
            dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.validate_step.setOnClickListener {
                if (dialog.step_title.text.toString() != "" && dialog.size.text.toString() != "") {
                    RealmManager.open()
                    var index = RealmManager.createStepDao().nextId()
                    RealmManager.createStepDao().save(Step(index, intent.getIntExtra(Constants().PROJECT_ID, 0), dialog.step_title.text.toString(), dialog.size.text.toString()))
                    RealmManager.close()
                    getSteps()
                }
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun onResume() {
        super.onResume()

        RealmManager.open()
        project = RealmManager.createProjectDao().loadBy(intent.getIntExtra(Constants().PROJECT_ID, 0))
        RealmManager.close()
        toolbar.title = project?.name

        getSteps()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_project, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_rename -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_add_project)
                dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.project_title.setText(project?.name)
                dialog.validate_project.setOnClickListener {
                    if (dialog.project_title.text.toString() != "") {
                        project?.name = dialog.project_title.text.toString()
                        RealmManager.open()
                        RealmManager.createProjectDao().save(project)
                        RealmManager.close()
                        title = project?.name
                    }
                    dialog.dismiss()
                }
                dialog.show()
                true
            }
            R.id.action_delete -> {
                RealmManager.open()
                RealmManager.createRuleDao().removeByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0))
                RealmManager.createStepDao().removeByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0))
                RealmManager.createProjectDao().removeById(intent.getIntExtra(Constants().PROJECT_ID, 0))
                RealmManager.close()

                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getSteps() {
        RealmManager.open()
        steps = ArrayList(RealmManager.createStepDao().loadByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0)).toList())
        RealmManager.close()

        steps_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = StepsAdapter(this, steps)
        steps_recyclerview.adapter = adapter
    }
}