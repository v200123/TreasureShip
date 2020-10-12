package com.jzz.treasureship.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.jzz.treasureship.model.bean.Questionnaire
import com.jzz.treasureship.model.bean.QuestionnaireResponseVo
import com.jzz.treasureship.receiver.RewardReceiver
import com.jzz.treasureship.utils.out


/**
 *@date: 2020/9/10
 *@describe:
 *@Auth: 29579
 **/
class RewardService : Service() {
    private var count: Int = 0
    companion object{
        const val TestQuestions = "com.TestQuestions"
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        "我定时器开始启动啦".out(true)
        val parcelableExtra = intent.getParcelableExtra<Questionnaire>(TestQuestions)
        val systemService: AlarmManager = getSystemService(Service.ALARM_SERVICE) as AlarmManager
        val startTime = intent.getLongExtra("TIme", 0L)
        if (startTime != 0L) {
            val l = startTime - System.currentTimeMillis()
            if (l >= 0L) {
                val pi =
                    PendingIntent.getBroadcast(
                        this,
                        count++,
                        Intent(this, RewardReceiver::class.java).apply {
                            putExtra("Answer",parcelableExtra)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                systemService.set(AlarmManager.RTC_WAKEUP, startTime, pi)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
