package com.example.anmol.beacons.BeaconSearch

import android.app.Dialog
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anmol.beacons.AdapterMsg
import com.example.anmol.beacons.R
import com.example.anmol.beacons.beaconSimulator.count
import com.example.anmol.beacons.mqtt.MqttClientHelper
import com.google.android.material.snackbar.Snackbar
import org.altbeacon.beacon.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Math.sqrt
import kotlin.collections.ArrayList
import kotlin.math.pow


/*
    This Fragment will display all the beacons detected by device with their details in the list
 */
class BeaconSearch : Fragment(), BeaconConsumer  {

    private val mqttClient by lazy {
        MqttClientHelper(requireContext())
    }



    var beaconList = arrayListOf<Beacon>()
    //Relative Layout
    var rl: RelativeLayout? = null

    //Recycler View
    private var rv: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<*>? = null

    //Beacon Manager
    private var beaconManager: BeaconManager? = null

    private var pb: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setMqttCallBack()
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

        rl = v.findViewById(R.id.Relative_One)
        rv = v.findViewById(R.id.search_recycler)
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
//            beaconList.clear()
//            beaconList.addAll(beacons)
            showBeacons(beacons as ArrayList<Beacon>)
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

    private fun showBeacons(beacons: ArrayList<Beacon>){

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
//                b.count = 5
//                arrayList.add(b)
            }
            try {
                requireActivity().runOnUiThread { // Setting Up the Adapter for Recycler View
                    adapter = RecyclerAdapter(requireContext() , beacons)
                    rv!!.adapter = adapter
                    adapter?.notifyDataSetChanged()
                }
            } catch (e: Exception) {
            }
        } else if (beacons.isEmpty()) {
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



//    private fun setMqttCallBack() {
//        mqttClient.setCallback(object : MqttCallbackExtended {
//            override fun connectComplete(b: Boolean, s: String) {
////                val snackbarMsg = "Connected to host:\n'$SOLACE_MQTT_HOST'."
////                Log.w("Debug", snackbarMsg)
////                Snackbar.make(requireActivity().findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
////                    .setAction("Action", null).show()
//            }
//            override fun connectionLost(throwable: Throwable) {
////                val snackbarMsg = "Connection to host lost:\n'$SOLACE_MQTT_HOST'"
////                Log.w("Debug", snackbarMsg)
////                Snackbar.make(requireActivity().findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
////                    .setAction("Action", null).show()
//            }
//            @Throws(Exception::class)
//            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
//                Log.w("Debug", "Message received from host '': $mqttMessage")
////                textViewNumMsgs.text = ("${textViewNumMsgs.text.toString().toInt() + 1}")
////                val str: String = "------------"+ Calendar.getInstance().time +"-------------\n$mqttMessage\n${textViewMsgPayload.text}"
////                textViewMsgPayload.text = str
//
////                showDialog(topic,mqttMessage)
//
//                Snackbar.make(requireActivity().findViewById(android.R.id.content), "$mqttMessage",
//                    Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//            }
//
//            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
////                Log.w("Debug", "Message published to host '$SOLACE_MQTT_HOST'")
//            }
//        })
//    }

//    private fun showDialog(topic: String, msg: ArrayList<String>) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setCancelable(false)
//        dialog.setContentView(R.layout.app_dialog)
//        dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
//
//        val becons = dialog.findViewById(R.id.top) as RecyclerView
//        becons.layoutManager = LinearLayoutManager(requireContext())
//        val adapter = AdapterMsg(requireContext() , msg)
//        becons.adapter = adapter
//
//        val body = dialog.findViewById(R.id.ok_dialog_title) as TextView
//        body.text = topic
////
////        val yesBtn = dialog.findViewById(R.id.ok_button_dialog) as Button
////        yesBtn.setOnClickListener {
////            dialog.dismiss()
////        }
//        dialog.show()
//    }


}