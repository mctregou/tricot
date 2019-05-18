package com.tregouet.knitting_images.module.stitches

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.model.Rule
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
import kotlinx.android.synthetic.main.item_rule.view.*
import kotlinx.android.synthetic.main.popup_create_rule.*

/**
 * Created by mariececile.tregouet on 06/01/2018.
 */
class StitchesAdapter(private val stitchesActivity: StitchesActivity, private val projects: ArrayList<Rule>) : RecyclerView.Adapter<StitchesAdapter.ViewHolder>() {

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent,false), stitchesActivity)

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(projects[position])

    /**
     * getItemCount
     */
    override fun getItemCount() = projects.size

    /**
     * ViewHolder
     */
    class ViewHolder(itemView: View, private val stitchesActivity: StitchesActivity) : RecyclerView.ViewHolder(itemView) {

        /**
         * Bind the rule with the ViewHolder
         */
        fun bind(rule: Rule) = with(itemView) {
            itemView.title.text = rule.stitch
            itemView.frequency.text = stitchesActivity.getString(R.string.frequence_ranks, rule.frequency)
            itemView.description.text = rule.description

            getStitches(rule)

            itemView.setOnClickListener {
                if (itemView.hidden_options.visibility == View.VISIBLE){
                    itemView.hidden_options.visibility = View.GONE
                } else {
                    itemView.hidden_options.visibility = View.VISIBLE
                }
            }

            itemView.setOnLongClickListener {
                val dialog = Dialog(context)
                dialog.setContentView(R.layout.popup_create_rule)
                dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.validate_rule.visibility = View.GONE
                dialog.update_rule_buttons.visibility = View.VISIBLE
                dialog.rule_title.setText(rule.stitch)
                dialog.rule_frequence.setText(rule.frequency.toString())
                dialog.rule_offset.setText(rule.offset.toString())
                dialog.rule_description.setText(rule.description)
                dialog.update_rule.setOnClickListener {
                    if (dialog.rule_title.text.toString() != ""
                            && dialog.rule_frequence.text.toString() != ""
                            && dialog.rule_description.text.toString() != "") {
                        rule.stitch = dialog.rule_title.text.toString()
                        rule.frequency = dialog.rule_frequence.text.toString().toInt()
                        rule.offset = dialog.rule_offset.text.toString().toInt()
                        rule.description = dialog.rule_description.text.toString()
                        RealmManager().open()
                        RealmManager().createRuleDao().save(rule)
                        RealmManager().close()

                        stitchesActivity.getStitches()
                    }
                    dialog.dismiss()
                }
                dialog.delete_rule.setOnClickListener {
                    RealmManager().open()
                    RealmManager().createRuleDao().removeById(rule.id!!)
                    RealmManager().close()
                    stitchesActivity.getStitches()
                    dialog.dismiss()
                }
                dialog.show()
                true
            }
        }

        fun getStitches(rule : Rule) {
            RealmManager().open()
            val images = ArrayList(RealmManager().createImageDao().loadAllForElement(Constants.STITCH_IMAGE, rule.id!!).toList())
            RealmManager().close()
            images.add(Image(-1))

            itemView.stitch_images.layoutManager = LinearLayoutManager(stitchesActivity, LinearLayoutManager.HORIZONTAL, false)
            val adapter = StitchImagesAdapter(stitchesActivity, rule.id!!, images)
            itemView.stitch_images.adapter = adapter
        }
    }
}