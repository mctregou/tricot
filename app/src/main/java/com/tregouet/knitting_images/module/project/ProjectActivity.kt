package com.tregouet.knitting_images.module.project

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
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
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.BaseSliderView
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.tregouet.knitting_images.model.Image
import com.tregouet.knitting_images.module.image.ImageActivity
import com.tregouet.knitting_images.utils.ImageUtils
import kotlinx.android.synthetic.main.popup_picture_option.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ProjectActivity : BaseActivity() {

    var adapter: StepsAdapter? = null
    var project: Project? = null
    var mCurrentPhotoPath : String? = null
    var fileUri : Uri ? = null

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
                    RealmManager().createStepDao().save(Step(index, intent.getIntExtra(Constants.PROJECT_ID, 0), dialog.step_title.text.toString(), dialog.size.text.toString(), 0, Date().time, RealmList(), dialog.description_texte.text.toString()))
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
        project = RealmManager().createProjectDao().loadBy(intent.getIntExtra(Constants.PROJECT_ID, 0))
        RealmManager().close()
        subtitle.text = project?.name

        getImages()

        getSteps()

        settings.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.popup_add_project)
            dialog.findViewById<TextView>(R.id.title).text = getString(R.string.edit_project)
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
                    RealmManager().createRuleDao().removeByProjectId(intent.getIntExtra(Constants.PROJECT_ID, 0))
                    RealmManager().createStepDao().removeByProjectId(intent.getIntExtra(Constants.PROJECT_ID, 0))
                    RealmManager().createProjectDao().removeById(intent.getIntExtra(Constants.PROJECT_ID, 0))
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
        val images = ArrayList(RealmManager().createImageDao().loadAllForElement(Constants.PROJECT_IMAGE, intent.getIntExtra(Constants.PROJECT_ID, 0)).toList())
        RealmManager().close()

        project_images.removeAllSliders()
        for (index in images.indices){
            val sliderView = DefaultSliderView(this)
            sliderView.image(File(images[index].url))
                    .setOnSliderClickListener { openZoomCarousel(index) }
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

    private fun openZoomCarousel(position: Int) {
        val intent = Intent(Intent(this, ImageActivity::class.java))
        intent.putExtra(Constants.IMAGE_TYPE, Constants.PROJECT_IMAGE)
        intent.putExtra(Constants.ELEMENT_ID, project?.id)
        intent.putExtra(Constants.POSITION, position)
        startActivity(intent)
    }

    /**
     * Get steps from the database
     */
    private fun getSteps() {
        RealmManager().open()
        val steps = ArrayList(RealmManager().createStepDao().loadByProjectId(intent.getIntExtra(Constants.PROJECT_ID, 0)).toList())
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
            addPicture(this)
        }

        dialog.choose_picture.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, Constants.CHOOSE_PHOTO_REQUEST)
            }
        }

        dialog.cancel.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addPicture(activity : ProjectActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            //    requestPermission();
            requestAppPermissions(arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    R.string.app_name, 1)

            //this code will be executed on devices running ICS or later
        } else {
            openCameraActivity(this)
        }
    }

    private fun openCameraActivity(activity : ProjectActivity) {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        fileUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        mCurrentPhotoPath = fileUri.toString()
        ImageUtils.openCameraOrFolders(activity, activity.fileUri!!)
    }

    private fun requestAppPermissions(requestedPermissions: Array<String>,
                                      stringId: Int, requestCode: Int) {
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        var shouldShowRequestPermissionRationale = false
        for (permission in requestedPermissions) {
            permissionCheck += ContextCompat.checkSelfPermission(this, permission)
            shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale || ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (!shouldShowRequestPermissionRationale) {
                 ActivityCompat.requestPermissions(this, requestedPermissions, requestCode)
            }
        } else {
            onPermissionsGranted(requestCode)
        }
    }

    fun onPermissionsGranted(requestCode: Int) {
        openCameraActivity(this)
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var permissionCheck = PackageManager.PERMISSION_GRANTED
        for (permission in grantResults) {
            permissionCheck += permission
        }
        if (grantResults.size > 0 && permissionCheck == PackageManager.PERMISSION_GRANTED) {
            onPermissionsGranted(requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == Constants.TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else if (resultCode == Activity.RESULT_OK
                && requestCode == Constants.CHOOSE_PHOTO_REQUEST) {
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

        RealmManager().open()
        RealmManager().createImageDao().save(Image(RealmManager().createImageDao().nextId(), Constants.PROJECT_IMAGE, intent.getIntExtra(Constants.PROJECT_ID, 0), file.absolutePath))
        RealmManager().close()
    }

    private fun processAlbumPhoto(data : Intent?) {
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
        val path = saveImage(bitmap)


        RealmManager().open()
        RealmManager().createImageDao().save(Image(RealmManager().createImageDao().nextId(), Constants.PROJECT_IMAGE, intent.getIntExtra(Constants.PROJECT_ID, 0), path))
        RealmManager().close()
    }

    private fun saveImage(myBitmap : Bitmap) : String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

        try {
            val f = File(filesDir, Calendar.getInstance().timeInMillis.toString() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()

            return f.absolutePath
        } catch (e1 : IOException) {
            e1.printStackTrace()
        }
        return ""
    }
}