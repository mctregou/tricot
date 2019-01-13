package com.tregouet.knitting_images.module.project

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Project
import com.tregouet.knitting_images.model.Step
import com.tregouet.knitting_images.module.base.BaseActivity
import com.tregouet.knitting_images.utils.Constants
import com.tregouet.knitting_images.utils.RealmManager
import com.tregouet.knitting_images.utils.Utils
import io.realm.RealmList
import kotlinx.android.synthetic.main.activity_project.*
import kotlinx.android.synthetic.main.popup_add_project.*
import kotlinx.android.synthetic.main.popup_add_step.*
import java.util.*
import kotlin.collections.ArrayList
import com.daimajia.slider.library.Animations.DescriptionAnimation
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.module.image.ImageActivity
import kotlinx.android.synthetic.main.popup_picture_option.*
import java.io.File

class ProjectActivity : BaseActivity() {

    var adapter: StepsAdapter? = null
    var project: Project? = null
    var mCurrentPhotoPath : String? = null

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
                    RealmManager().createStepDao().save(Step(index, intent.getIntExtra(Constants().PROJECT_ID, 0), dialog.step_title.text.toString(), dialog.size.text.toString(), 0, Date().time, RealmList(), dialog.description_texte.text.toString()))
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

        getImages()

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
     * Get project images
     */
    private fun getImages() {
        RealmManager().open()
        val images = ArrayList(RealmManager().createImageDao().loadAllForElement(Constants().PROJECT_IMAGE, intent.getIntExtra(Constants().PROJECT_ID, 0)).toList())
        RealmManager().close()

        project_images.removeAllSliders()
        for (image in images){
            val sliderView = DefaultSliderView(this)
            sliderView.image(File(image.url))
                    .setOnSliderClickListener { openZoomCarousel() }
            sliderView.scaleType = BaseSliderView.ScaleType.CenterInside
            project_images.addSlider(sliderView)
        }

        //Add icon to add image
        val sliderView = DefaultSliderView(this)
        sliderView.image(R.drawable.fab_add)
                .setOnSliderClickListener { openCameraOrFolders() }
        sliderView.scaleType = BaseSliderView.ScaleType.CenterInside
        project_images.addSlider(sliderView)

        project_images.setPresetTransformer(SliderLayout.Transformer.Default)
        project_images.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
    }

    private fun openZoomCarousel() {
        val intent = Intent(Intent(this, ImageActivity::class.java))
        intent.putExtra(Constants().IMAGE_TYPE, Constants().PROJECT_IMAGE)
        intent.putExtra(Constants().ELEMENT_ID, project?.id)
        startActivity(intent)
    }

    /**
     * Get steps from the database
     */
    private fun getSteps() {
        RealmManager().open()
        val steps = ArrayList(RealmManager().createStepDao().loadByProjectId(intent.getIntExtra(Constants().PROJECT_ID, 0)).toList())
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


    override fun onStop() {
        project_images.stopAutoCycle()
        super.onStop()
    }

    private fun openCameraOrFolders() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.popup_picture_option)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.take_picture.setOnClickListener {
            dialog.dismiss()
            validatePermissions()
        }

        dialog.choose_picture.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, Constants().CHOOSE_PHOTO_REQUEST)
            }
        }

        dialog.cancel.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(
                            response: PermissionGrantedResponse?) {
                        launchCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: PermissionRequest?,
                            token: PermissionToken?) {
                        AlertDialog.Builder(this@ProjectActivity)
                                .setTitle(
                                        R.string.storage_permission_rationale_title)
                                .setMessage(
                                        R.string.storage_permition_rationale_message)
                                .setNegativeButton(
                                        android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({
                                    token?.cancelPermissionRequest() })
                                .show()
                    }

                    override fun onPermissionDenied(
                            response: PermissionDeniedResponse?) {
                        Snackbar.make(subtitle,
                                R.string.storage_permission_denied_message,
                                Snackbar.LENGTH_LONG)
                                .show()
                    }
                })
                .check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, Constants().TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants().TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else if (resultCode == Activity.RESULT_OK
                && requestCode == Constants().CHOOSE_PHOTO_REQUEST) {
            processAlbumPhoto(data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
       /* val uri = Uri.fromFile(file)

        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)*/

        RealmManager().open()
        RealmManager().createImageDao().save(Image(RealmManager().createImageDao().nextId(), Constants().PROJECT_IMAGE, intent.getIntExtra(Constants().PROJECT_ID, 0), file.absolutePath))
        RealmManager().close()
    }

    private fun processAlbumPhoto(data : Intent?) {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val newFile = File(data?.data?.path).copyTo(File(fileUri.path))

        RealmManager().open()
        RealmManager().createImageDao().save(Image(RealmManager().createImageDao().nextId(), Constants().PROJECT_IMAGE, intent.getIntExtra(Constants().PROJECT_ID, 0), newFile.absolutePath))
        RealmManager().close()
    }
}