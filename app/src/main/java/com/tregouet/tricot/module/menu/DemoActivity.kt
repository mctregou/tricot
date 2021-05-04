package com.tregouet.tricot.module.menu

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.tregouet.tricot.R
import com.tregouet.tricot.module.base.BaseActivity

import com.instabug.library.Instabug
import com.tregouet.tricot.BuildConfig
import com.tregouet.tricot.model.Project
import com.tregouet.tricot.model.Step
import com.tregouet.tricot.utils.RealmManager
import com.tregouet.tricot.utils.realm.RealmInt
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_demos.*
import kotlinx.android.synthetic.main.activity_infos.*
import kotlinx.android.synthetic.main.popup_add_project.*
import kotlinx.android.synthetic.main.popup_message.*
import java.util.*

class DemoActivity : BaseActivity() {

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demos)

        demos_add_project.setOnClickListener {
            val projectDemo = RealmManager().createProjectDao().loadByName("Démo : Headband");
            if (projectDemo == null) {
                val index = RealmManager().createProjectDao().nextId()
                RealmManager().createProjectDao().save(Project(index, "Démo : Headband"));
                RealmManager().createStepDao().save(Step(
                        0,
                        0,
                        "Pièce principale",
                        "Mesurez votre tour de tête, et retirer 10cm. Vous aurez ainsi la longueur totale à réaliser",
                        1,
                        Date().time,
                        RealmList(RealmInt(2)),
                        "Montez 12 mailles et tricotez 2m endroit, 8m torsade, 2m endroit."
                ));
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_message)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.popup_title.setText(R.string.popup_message_title_confirmation)
                dialog.popup_text.setText(R.string.popup_message_text_confirmation)
                dialog.close_popup?.setOnClickListener{
                    dialog.dismiss()
                    finish()
                }
                dialog.show()
            } else {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.popup_message)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.close_popup?.setOnClickListener{
                    dialog.dismiss()
                }
                dialog.show()
            }
        }
    }
}
