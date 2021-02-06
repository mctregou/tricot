package com.tregouet.knitting_images.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.tregouet.knitting_images.R
import com.tregouet.knitting_images.model.Rule
import com.tregouet.knitting_images.module.base.UpdateNotification
import com.tregouet.knitting_images.module.base.UpdateStep
import com.tregouet.knitting_images.module.step.StepActivity
import kotlinx.android.synthetic.main.activity_step.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * Created by mariececile.tregouet on 12/02/2018.
 */
class NotificationService : Service() {

    var projectId: Int = -1
    var stepId: Int = -1


    override fun onCreate() {
        Log.d("NotificationService", "Create")
        super.onCreate()
        isRunning = true
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        isRunning = false
        hideNotification()
        Log.d("NotificationService", "Stopped")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null || intent.action == null) {
            return START_STICKY
        }
        if (intent.action == Constants.ACTION_MINUS) {
            Log.i("PlayerService", "Clicked Previous")
            minus()

        } else if (intent.action == Constants.ACTION_PLUS) {
            Log.i("PlayerService", "Clicked Play")
            plus()
        }
        return START_STICKY
    }

    private fun plus() {
        RealmManager().open()
        val step = RealmManager().createStepDao().loadBy(stepId)
        step?.currentRank = (step?.currentRank!! + 1)
        RealmManager().createStepDao().save(step)
        RealmManager().close()
        showNotification()
        EventBus.getDefault().post(UpdateStep())
    }

    private fun minus() {
        val step = RealmManager().createStepDao().loadBy(stepId)
        if (step?.currentRank!! > 1) {
            RealmManager().open()
            step.currentRank = (step.currentRank - 1)
            RealmManager().createStepDao().save(step)
            RealmManager().close()
            showNotification()
            EventBus.getDefault().post(UpdateStep())
        }
    }

    @Subscribe
    fun onUpdateNotificationEventReceived(event: UpdateNotification) {
        if (event.mustShowNotification) {
            projectId = event.projectId
            stepId = event.stepId
            showNotification()
        } else {
            hideNotification()
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun showNotification() {
        val views = RemoteViews(packageName, R.layout.notification_ranks)
        val bigViews = RemoteViews(packageName, R.layout.big_notification_ranks)
        val step = RealmManager().createStepDao().loadBy(stepId)
        val project = RealmManager().createProjectDao().loadBy(projectId)

        val notificationIntent = Intent(this, StepActivity::class.java)
        notificationIntent.putExtra(Constants.STEP_ID, step?.id)
        notificationIntent.putExtra(Constants.PROJECT_ID, step?.projectId)
        notificationIntent.action = Constants.MAIN_ACTION
        notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0)

        val minusIntent = Intent(this, NotificationService::class.java)
        minusIntent.action = Constants.ACTION_MINUS
        val pminusIntent = PendingIntent.getService(this, 0,
                minusIntent, 0)

        val plusIntent = Intent(this, NotificationService::class.java)
        plusIntent.action = Constants.ACTION_PLUS
        val pplusIntent = PendingIntent.getService(this, 0,
                plusIntent, 0)

        views.setOnClickPendingIntent(R.id.plus, pplusIntent)
        views.setImageViewResource(R.id.plus, R.drawable.ic_add)
        bigViews.setOnClickPendingIntent(R.id.plus, pplusIntent)
        bigViews.setImageViewResource(R.id.plus, R.drawable.ic_add)

        views.setOnClickPendingIntent(R.id.minus, pminusIntent)
        views.setImageViewResource(R.id.minus, R.drawable.ic_remove)
        bigViews.setOnClickPendingIntent(R.id.minus, pminusIntent)
        bigViews.setImageViewResource(R.id.minus, R.drawable.ic_remove)

        views.setTextViewText(R.id.title, project.name)
        bigViews.setTextViewText(R.id.title, project.name)

        views.setTextViewText(R.id.current_rank, step.currentRank.toString())
        bigViews.setTextViewText(R.id.current_rank, step.currentRank.toString())

        // Check rules
        val currentRules = ArrayList<Rule>()
        val rules = ArrayList(RealmManager().createRuleDao().loadByIds(step?.stitches?.toList()).toList())
        System.out.println("rules size = " + rules.size)
        System.out.println("stepId = " + stepId)
        rules.filterTo(currentRules) { (step?.currentRank!! - it.offset!!) % it.frequency!! == 0 }
        System.out.println("currentRules size = " + currentRules.size)

        val mustDoRule = !currentRules.isEmpty()
        var mustDoReduction = false

        // Check reductions
        val reduction = RealmManager().createReductionDao().loadByStepId(step?.id!!)
        val reductionItems = RealmManager().createReductionItemDao().loadByReductionId(reduction?.id!!)
        if (reductionItems.isNotEmpty()
                && step.currentRank - reduction.offsetRank!! >= 0
                && ((step.currentRank - reduction.offsetRank!!) % reduction.frequency!! == 0)){
            val position = (step.currentRank - reduction.offsetRank!!) / reduction.frequency!!
            var count = 0
            for (reductionItem in reductionItems){
                if ( position >= (count + reductionItem.times)){
                    count += reductionItem.times
                } else {
                    mustDoReduction = true
                    break
                }
            }
        }

        if (!mustDoRule && !mustDoReduction) {
            views.setViewVisibility(R.id.warning, View.GONE)
            bigViews.setViewVisibility(R.id.warning, View.GONE)
        } else {
            views.setViewVisibility(R.id.warning, View.VISIBLE)
            views.setImageViewResource(R.id.warning, R.drawable.ic_warning)
            bigViews.setViewVisibility(R.id.warning, View.VISIBLE)
            bigViews.setImageViewResource(R.id.warning, R.drawable.ic_warning)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // The id of the channel.
            val id = "count"
            // The user-visible name of the channel.
            val name: CharSequence = "Compteur"
            // The user-visible description of the channel.
            val description = "Description"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = R.color.colorPrimary

            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            mNotificationManager.createNotificationChannel(mChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, "test")
        notificationBuilder.setSmallIcon(R.drawable.tricot)
        notificationBuilder.setCustomContentView(views)
        notificationBuilder.setCustomBigContentView(bigViews)
        notificationBuilder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("count")
        }
        notificationBuilder.setOngoing(true)

        val status = notificationBuilder.build()
        status.flags = Notification.FLAG_ONGOING_EVENT
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(0, notificationBuilder.build())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun hideNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }


    override fun onTaskRemoved(rootIntent: Intent) {
        println("onTaskRemoved")
        hideNotification()
    }

    companion object {
        var isRunning = false
    }
}