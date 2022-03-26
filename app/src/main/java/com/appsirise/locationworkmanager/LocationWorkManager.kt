package com.appsirise.locationworkmanager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.*
import javax.inject.Inject

const val TAG = "LocationWorkManager"

@HiltWorker
class LocationWorkManager(
    context: Context,
    parameters: WorkerParameters
) : Worker(context, parameters) {

    @Inject
    lateinit var locationRepository: LocationRepository

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location = locationResult.lastLocation
            Log.d(TAG, "Location saved successfully")
            saveLocation(location)
        }
    }

    private fun saveLocation(location: Location) {
        locationRepository.insert(
            LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override fun doWork(): Result {
        // Mark the Worker as important
        setForegroundAsync(createForegroundInfo())
        verifyPermissionsAndUpdateLocation()
        return Result.success()
    }

    private fun verifyPermissionsAndUpdateLocation() {
        val permissionFine =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        val permissionCoarse =
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

        if (permissionFine == PackageManager.PERMISSION_GRANTED && permissionCoarse == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permissions granted")
            requestFusedLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestFusedLocationUpdates() {
        val request = LocationRequest.create().apply {
            interval = 4 * 60 * 1000
            fastestInterval = 1 * 60 * 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val client: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)
        client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notificationId = 2
        val id: String = applicationContext.getString(R.string.notification_location_foreground_id)
        val title: String = applicationContext.getString(R.string.notification_title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(id)
        }
        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
        } else {
            ForegroundInfo(notificationId, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(channelId: String) {
        val channelName = applicationContext.getString(R.string.notification_location_channel_name)
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }
        notificationManager.createNotificationChannel(channel)
    }
}