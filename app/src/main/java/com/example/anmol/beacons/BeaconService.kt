package com.example.anmol.beacons

import android.R
import android.app.*
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.MonitorNotifier
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import org.altbeacon.beacon.Region

/*
    This class is a service which will run in background and will notify the user through notification
    message whether he enters the beacon region or exited the beacon region.
 */
class BeaconService : Service(), BeaconConsumer, MonitorNotifier {
    override fun onCreate() {
        super.onCreate()
        // Binding the BeaconNotification Application Class BeaconManager to BeaconService.
        BeaconNotification.beaconManager?.bind(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Printing to check whether service called inside logcat
        println("SERVICE CALLED ------------------------------------------------->")
        return START_STICKY
    }

    override fun onBeaconServiceConnect() {
        //Specifies a class that should be called each time
        // the BeaconService sees or stops seeing a Region of beacons.
        BeaconNotification.beaconManager?.addMonitorNotifier(this)
    }

    /*
      This override method is runned when some beacon will come under the range of device.
    */
    override fun didEnterRegion(region: Region) {

        // Showing Notification that beacon is found
        showNotification("Found Beacon in the range", "For more info go the app")
    }

    /*
        This override method is runned when beacon that comes in the range of device
        ,now been exited from the range of device.
     */
    override fun didExitRegion(region: Region) {

        // Showing Notification that beacon is exited from region
        showNotification("Founded Beacon Exited", "For more info go the app")
    }

    /*
      This override method will Determine the state for the device , whether device is in range
       of beacon or not , if yes then i = 1 and if no then i = 0
    */
    override fun didDetermineStateForRegion(i: Int, region: Region) {}

    // Method for Showing Notifications
    fun showNotification(title: String?, message: String?) {
        val notifyIntent = Intent(this, MainActivity::class.java)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivities(
            this,
            0,
            arrayOf(notifyIntent),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notification.defaults = notification.defaults or Notification.DEFAULT_SOUND
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    // Method to start Broadcasting to restart the service
    // (BeaconBroadcast class)
    fun startBroadcasting() {
        val broadcastIntent = Intent("com.example.anmol.beacons.RestartBeaconService")
        sendBroadcast(broadcastIntent)
    }

    // Override onDestroy method
    override fun onDestroy() {
        super.onDestroy()
        // if by chance service gets destroyed then start broadcasting to again start the service.
        startBroadcasting()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.action = ""
        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartServicePendingIntent
    }
}