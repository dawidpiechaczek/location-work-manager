package com.appsirise.locationworkmanager

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val workManager = WorkManager.getInstance(this)
        val workRequest = OneTimeWorkRequest.from(LocationWorkManager::class.java)

        workManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    val progress = workInfo.progress

                    if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                        Toast.makeText(applicationContext, "SUCCEED", Toast.LENGTH_LONG).show()
                    }
                }
            }
        workManager.enqueue(workRequest)
    }
}