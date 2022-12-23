package com.example.anmol.beacons.BeaconSearch

import android.content.Context
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import android.widget.ProgressBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.anmol.beacons.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.anmol.beacons.BeaconSearch.RecyclerAdapter
import android.content.ServiceConnection
import android.content.Intent
import android.os.RemoteException
import android.view.View
import androidx.fragment.app.Fragment
import org.altbeacon.beacon.*
import java.lang.Exception
import java.util.ArrayList

/*
    This Fragment will display all the beacons detected by device with their details in the list
 */
class BeaconSearc : Fragment(), BeaconConsumer {
    //Relative Layout
    var rl: RelativeLayout? = null

    //Recycler View
    private var rv: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    //Beacon Manager
    private var beaconManager: BeaconManager? = null

    // Progress bar
    private var pb: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //getting beaconManager instance (object) for Main Activity class
        beaconManager = BeaconManager.getInstanceForApplication(requireActivity())

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager!!.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))

        //Binding MainActivity to the BeaconService.
        beaconManager!!.bind(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.beacon_search, container, false)

        // Intializing the Layout

        //Relative Layout
        rl = v.findViewById(R.id.Relative_One)

        // Recycler View
        rv = v.findViewById(R.id.search_recycler)

        //Progress Bar
        pb = v.findViewById(R.id.pb)
        return v
    }

    override fun onBeaconServiceConnect() {

        //Constructing a new Region object to be used for Ranging or Monitoring
        val region = Region("myBeaons", null, null, null)

        //Specifies a class that should be called each time the BeaconService sees or stops seeing a Region of beacons.
        beaconManager!!.addMonitorNotifier(object : MonitorNotifier {
            /*
            This override method is runned when some beacon will come under the range of device.
        */
            override fun didEnterRegion(region: Region) {
                println("ENTER ------------------->")
                try {

                    //Tells the BeaconService to start looking for beacons that match the passed Region object
                    // , and providing updates on the estimated mDistance every seconds while beacons in the Region
                    // are visible.
                    beaconManager!!.startRangingBeaconsInRegion(region)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            /*
             This override method is runned when beacon that comes in the range of device
             ,now been exited from the range of device.
         */
            override fun didExitRegion(region: Region) {
                println("EXIT----------------------->")
                try {

                    //Tells the BeaconService to stop looking for beacons
                    // that match the passed Region object and providing mDistance
                    // information for them.
                    beaconManager!!.stopRangingBeaconsInRegion(region)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }

            /*
                 This override method will Determine the state for the device , whether device is in range
               of beacon or not , if yes then i = 1 and if no then i = 0
            */
            override fun didDetermineStateForRegion(state: Int, region: Region) {
                println("I have just switched from seeing/not seeing beacons: $state")
            }
        })


        //Specifies a class that should be called each time the BeaconService gets ranging data,
        // which is nominally once per second when beacons are detected.
        beaconManager!!.addRangeNotifier { beacons, region ->

            /*
                   This Override method tells us all the collections of beacons and their details that
                   are detected within the range by device
                 */
            // Checking if the Beacon inside the collection (ex. list) is there or not

            // if Beacon is detected then size of collection is > 0
            if (beacons.isNotEmpty()) {
                try {
                    requireActivity().runOnUiThread { // Make ProgressBar Invisible
                        pb!!.visibility = View.INVISIBLE

                        // Make Relative Layout to be Gone
                        rl!!.visibility = View.GONE

                        //Make RecyclerView to be visible
                        rv!!.visibility = View.VISIBLE

                        // Setting up the layout manager to be linear
                        layoutManager = LinearLayoutManager(activity)
                        rv!!.layoutManager = layoutManager
                    }
                } catch (e: Exception) {
                }
                val arrayList = ArrayList<Beacon>()

                // Iterating through all Beacons from Collection of Beacons
                for (b in beacons) {

//                    //UUID
//                    val uuid = b.id1.toString()
//                    //Major
//                    val major = b.id2.toString()
//                    //Minor
//                    val minor = b.id3.toString()
//                    //Distance
//                    val distance1 = b.distance
//                    val distance = (Math.round(distance1 * 100.0) / 100.0).toString()
//                    val arr = ArrayList<String>()
//                    arr.add(uuid)
//                    arr.add(major)
//                    arr.add(minor)
//                    arr.add("$distance meters")
                    arrayList.add(b)
                }
                try {
                    requireActivity().runOnUiThread { // Setting Up the Adapter for Recycler View
                        adapter = RecyclerAdapter(arrayList)
                        rv!!.adapter = adapter
                        adapter?.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                }
            } else if (beacons.size == 0) {
                try {
                    requireActivity().runOnUiThread { // Setting Progress Bar InVisible
                        pb!!.visibility = View.INVISIBLE

                        // Setting RelativeLayout to be Visible
                        rl!!.visibility = View.VISIBLE

                        // Setting RecyclerView to be Gone
                        rv!!.visibility = View.GONE
                    }
                } catch (e: Exception) {
                }
            }
        }
        try {

            //Tells the BeaconService to start looking for beacons that match the passed Region object.
            beaconManager!!.startMonitoringBeaconsInRegion(region)
        } catch (e: RemoteException) {
        }
    }

    /*
         If we are implementing the BeaconConsumer interface in a Fragment
        (and not an Activity, Service or Application instance),
         we need to chain all of the methods.
     */
    override fun getApplicationContext(): Context {
        return requireActivity().applicationContext
    }

    override fun unbindService(serviceConnection: ServiceConnection) {
        requireActivity().unbindService(serviceConnection)
    }

    override fun bindService(
        intent: Intent,
        serviceConnection: ServiceConnection,
        i: Int
    ): Boolean {
        return requireActivity().bindService(intent, serviceConnection, i)
    }

    // Override onDestroy Method
    override fun onDestroy() {
        super.onDestroy()
        //Unbinds an Android Activity or Service to the BeaconService to avoid leak.
        beaconManager!!.unbind(this)
    }
}