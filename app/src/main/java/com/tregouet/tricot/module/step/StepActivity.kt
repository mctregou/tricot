package com.tregouet.tricot.module.step

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Rule
import com.tregouet.tricot.model.Step
import com.tregouet.tricot.utils.Constants
import com.tregouet.tricot.utils.RealmManager
import kotlinx.android.synthetic.main.activity_step.*
import kotlinx.android.synthetic.main.popup_add_rule.*
import kotlinx.android.synthetic.main.popup_add_step.*

class StepActivity : AppCompatActivity() {

    private var adapter: RulesAdapter? = null
    private var currentRuleAdapter: CurrentRulesAdapter? = null
    private var step: Step? = null
    private var rules: ArrayList<Rule> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step)
        setSupportActionBar(toolbar)

        minus.setOnClickListener { minus() }
        plus.setOnClickListener { plus() }
    }

    override fun onResume() {
        super.onResume()

        RealmManager.open()
        step = RealmManager.createStepDao().loadBy(intent.getIntExtra(Constants().STEP_ID, 0))
        RealmManager.close()

        title = step?.name
        current_rank.text = step!!.currentRank.toString()
        end.text = "Longueur de l'étape : " + step!!.end.toString()

        add_rule.setOnClickListener { _ ->
            addRule()
        }

        getRules()

        checkRule()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_step, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_rename_step -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_add_step)
                dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.step_title.setText(step?.name)
                dialog.size.setText(step?.end)
                dialog.validate_step.setOnClickListener {
                    if (dialog.step_title.text.toString() != "" && dialog.size.text.toString() != "") {
                        step?.name = dialog.step_title.text.toString()
                        step?.end = dialog.size.text.toString()
                        RealmManager.open()
                        RealmManager.createStepDao().save(step)
                        RealmManager.close()
                        title = step?.name
                        end.text = "Longueur de l'étape : " + step?.end.toString()
                    }
                    dialog.dismiss()
                }
                dialog.show()
                true
            }
            R.id.action_delete_step -> {
                RealmManager.open()
                RealmManager.createRuleDao().removeByStepId(intent.getIntExtra(Constants().STEP_ID, 0))
                RealmManager.createStepDao().removeById(intent.getIntExtra(Constants().STEP_ID, 0))
                RealmManager.close()
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getRules() {
        RealmManager.open()
        rules = ArrayList(RealmManager.createRuleDao().loadByStepId(intent.getIntExtra(Constants().STEP_ID, 0)).toList())
        RealmManager.close()

        rules_recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RulesAdapter(this, rules)
        rules_recyclerview.adapter = adapter
    }

    private fun addRule() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_add_rule)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.validate_rule.setOnClickListener {
            if (dialog.rule_title.text.toString() != ""
                    && dialog.rule_frequence.text.toString() != ""
                    && dialog.rule_description.text.toString() != "") {
                RealmManager.open()
                System.out.println("test = " + RealmManager.createRuleDao().nextId())
                var index = RealmManager.createRuleDao().nextId()
                RealmManager.createRuleDao().save(Rule(index,
                        intent.getIntExtra(Constants().STEP_ID,0),
                        intent.getIntExtra(Constants().PROJECT_ID,0),
                        dialog.rule_title.text.toString(),
                        dialog.rule_frequence.text.toString().toInt(),
                        dialog.rule_offset.text.toString().toInt(),
                        dialog.rule_description.text.toString()
                        ))
                RealmManager.close()

                getRules()
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun minus() {
        if (step?.currentRank!! > 1) {
            RealmManager.open()
            step!!.currentRank = (step!!.currentRank - 1)
            RealmManager.createStepDao().save(step)
            RealmManager.close()
            current_rank.text = step!!.currentRank.toString()

            checkRule()
        }
    }

    private fun plus() {
        RealmManager.open()
        step?.currentRank = (step?.currentRank!! + 1)
        RealmManager.createStepDao().save(step)
        RealmManager.close()
        current_rank.text = step!!.currentRank.toString()

        checkRule()
    }

    private fun checkRule() {
        val currentRules = ArrayList<Rule>()

        rules.filterTo(currentRules) { (step?.currentRank!! - it.offset!!) % it.frequency!! == 0 }

        Log.i("StepActivity", "checkRule rules=" + rules.size + ", currentRules=" + currentRules.size)
        current_rules_recyclerview.layoutManager = LinearLayoutManager(this)
        currentRuleAdapter = CurrentRulesAdapter(currentRules)
        current_rules_recyclerview.adapter = currentRuleAdapter

        if (currentRules.isEmpty()){
            special_title.visibility = View.GONE
            current_rules_recyclerview.visibility = View.GONE
        } else {
            special_title.visibility = View.VISIBLE
            current_rules_recyclerview.visibility = View.VISIBLE
        }
    }

}