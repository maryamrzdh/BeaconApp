package com.example.anmol.beacons

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*
    This is class that inherits BroadcastReceiver
    This BroadcastReceiver Class is used to restart the service whenever
    by chance our Service get Destroyed.
 */
class BeaconBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("Again Starting the service")
        //Again starting the service
        context.startService(Intent(context, BeaconService::class.java))
    }
}