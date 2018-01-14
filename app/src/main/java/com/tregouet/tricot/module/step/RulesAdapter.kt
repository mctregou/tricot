package com.tregouet.tricot.module.step

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Rule
import com.tregouet.tricot.utils.RealmManager
import kotlinx.android.synthetic.main.item_rule.view.*
import kotlinx.android.synthetic.main.popup_confirmation.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class RulesAdapter(private val stepActivity : StepActivity, private val projects: ArrayList<Rule>) : RecyclerView.Adapter<RulesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent,false), stepActivity)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(projects[position])

    override fun getItemCount() = projects.size

    class ViewHolder(itemView: View, private val stepActivity : StepActivity) : RecyclerView.ViewHolder(itemView) {
        fun bind(rule: Rule) = with(itemView) {
            System.out.println("Test " + rule.stitch + "/" + rule.id)
            itemView.title.text = rule.stitch
            itemView.frequency.text = rule.frequency.toString() + " rangs"
            itemView.description.text = rule.description

            itemView.setOnClickListener {
                if (itemView.description.visibility == View.VISIBLE){
                    itemView.description.visibility = View.GONE
                } else {
                    itemView.description.visibility = View.VISIBLE
                }
            }

            itemView.setOnLongClickListener {
                val dialog = Dialog(stepActivity)
                dialog.setContentView(R.layout.popup_confirmation)
                dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.ok.setOnClickListener {
                    RealmManager.open()
                    RealmManager.createRuleDao().removeById(rule.id!!)
                    RealmManager.close()
                    stepActivity.getRules()
                    dialog.dismiss()
                }
                dialog.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                true
            }
        }
    }
}