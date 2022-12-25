package com.example.anmol.beacons

import android.Manifest
import android.app.Application
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import org.altbeacon.beacon.powersave.BackgroundPowerSaver
import android.bluetooth.BluetoothAdapter
import android.widget.Toast
import android.content.Intent
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import com.example.anmol.beacons.BeaconNotification
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import java.lang.Exception

/*
    This is the Application class that implements BootstrapNotifier Interface
 */
class BeaconNotification : Application(), BootstrapNotifier {
    private var regionBootstrap: RegionBootstrap? = null
    private var backgroundPowerSaver: BackgroundPowerSaver? = null
    override fun onCreate() {
        super.onCreate()

        // Getting the bluetooth adapter object
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        // Checking if bluetooth is supported by device or not
        if (mBluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Bluetooth Not Supported", Toast.LENGTH_LONG).show()
        } else {
            // if bluetooth is supported but not enabled then enable it
            if (!mBluetoothAdapter.isEnabled) {
                val bluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bluetoothIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
//                    return

//                    ActivityCompat.requestPermissions(this , arrayOf(Manifest.permission.) , 1000)

                } else startActivity(bluetoothIntent)
            }
        }

        // getting beaconManager instance (object) for Application class which implements BootstrapNotifier interface.
        beaconManager = BeaconManager.getInstanceForApplication(this)

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager!!.beaconParsers.add(
            BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        )

        //Changes defaults scanning periods when ranging is performed.
        // Scan period times may be adjusted by internal algorithms or operating system.
        beaconManager!!.foregroundScanPeriod = 1100L
        beaconManager!!.foregroundBetweenScanPeriod = 0L

        //Allows disabling use of Android L BLE Scanning APIs on devices with API 21+
        // If set to false (default), devices with API 21+ will use the Android L APIs to scan for beacons
        BeaconManager.setAndroidLScanningDisabled(true)

        //Sets the duration in milliseconds spent not scanning between each Bluetooth LE scan cycle
        // when no ranging/monitoring clients are in the foreground
        beaconManager!!.backgroundBetweenScanPeriod = 1

        //Sets the duration in milliseconds of each Bluetooth LE scan cycle to look for beacons.
        beaconManager!!.backgroundScanPeriod = 1100L
        try {
            //Updates an already running scan
            beaconManager!!.updateScanPeriods()
        } catch (e: Exception) {
        }
        // wake up the app when a beacon is seen
        region1 = Region(
            "backgroundRegion",
            null, null, null
        )
        regionBootstrap = RegionBootstrap(this, region1)

        // simply constructing this class and holding a reference to it in your custom Application
        // class will automatically cause the BeaconLibrary to save battery whenever the application
        // is not visible.  This reduces bluetooth power usage by about 60%.
        backgroundPowerSaver = BackgroundPowerSaver(this)
    }

    /*
        This override method is runned when some beacon will come under the range of device.
     */
    override fun didEnterRegion(region: Region) {
        try {

            // TODO:
            // Starting the BeaconService class that extends Service
            val i = Intent(applicationContext, BeaconService::class.java)
//            startService(i)
        } catch (e: Exception) {
        }
    }

    /*
        This override method is runned when beacon that comes in the range of device
        ,now been exited from the range of device.
     */
    override fun didExitRegion(region: Region) {
        try {
            // Starting the BeaconService class that extends Service
            val k = Intent(applicationContext, BeaconService::class.java)
//            startService(k)
        } catch (e: Exception) {
        }
    }

    /*
      This override method will Determine the state for the device , whether device is in range
      of beacon or not , if yes then i = 1 and if no then i = 0
     */
    override fun didDetermineStateForRegion(i: Int, region: Region) {
        try {
            // Starting the BeaconService class that extends Service
            val k = Intent(applicationContext, BeaconService::class.java)
//            startService(k)
        } catch (e: Exception) {
        }
    }

    companion object {
        var beaconManager: BeaconManager? = null
        var region1: Region? = null
        var beaconList = ArrayList<Beacon>()
    }
}