package com.tregouet.tricot.module.project

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.tricot.R
import com.tregouet.tricot.model.Step
import com.tregouet.tricot.module.step.StepActivity
import com.tregouet.tricot.utils.Constants
import kotlinx.android.synthetic.main.item_project.view.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class StepsAdapter(private val context: Context, private var steps: ArrayList<Step>) : RecyclerView.Adapter<StepsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(context, LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent,false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(steps[position])

    override fun getItemCount() = steps.size

    class ViewHolder(private val context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(step: Step) = with(itemView) {
            itemView.title.text = step.name
            setOnClickListener { openStep(step) }
        }

        fun openStep(step:Step?){
            val intent = Intent(Intent(context, StepActivity::class.java))
            intent.putExtra(Constants().STEP_ID, step?.id)
            intent.putExtra(Constants().PROJECT_ID, step?.projectId)
            context.startActivity(intent)
        }
    }

    fun setSteps(steps: ArrayList<Step>) {
        this.steps = steps
        notifyDataSetChanged()
    }
}