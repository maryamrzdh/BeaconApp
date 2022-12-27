package com.example.anmol.beacons.mqtt

import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.anmol.beacons.R
import com.example.anmol.beacons.SOLACE_MQTT_HOST
import com.google.android.material.snackbar.Snackbar
import org.altbeacon.beacon.*
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject
import kotlin.math.pow

class PublishFragment : Fragment() , SensorEventListener , BeaconConsumer , AdapterView.OnItemSelectedListener{

    lateinit var textViewMsgPayload:TextView
    private lateinit var spinner:Spinner
    lateinit var editTextMsgPayload:EditText
    private lateinit var sensorManager: SensorManager
    private var speedSensor: Sensor? = null
    var baecons :ArrayList<Beacon>?= null
    var topic:String = "app/beacon"

    private val paths = arrayOf("app/beacon", "app/accelerometer")

    private val mqttClient by lazy {
        MqttClientHelper(requireContext())
    }

    var speed :String = ""

    private var beaconManager: BeaconManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMqttCallBack()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_publish, container, false)

        this.spinner = v.findViewById(R.id.editTextPubTopic)
        setUpSensor()
        setSpinner()

        val btnPub = v.findViewById<Button>(R.id.btnPub)
        val btnSub = v.findViewById<Button>(R.id.btnSub)
        val editTextSubTopic = v.findViewById<EditText>(R.id.editTextSubTopic)

        // pub button
        btnPub.setOnClickListener { view -> publishBeacon(view) }

        // sub button
        btnSub.setOnClickListener { view ->
            var snackbarMsg : String
            val topic = editTextSubTopic.text.toString().trim()
            snackbarMsg = "Cannot subscribe to empty topic!"
            if (topic.isNotEmpty()) {
                snackbarMsg = try {

                    mqttClient.subscribe(topic)
                    "Subscribed to topic '$topic'"
                } catch (ex: MqttException) {
                    "Error subscribing to topic: $topic"
                }
            }
            Snackbar.make(view, snackbarMsg, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
        }

//        Timer("CheckMqttConnection", false).schedule(3000) {
//            if (!mqttClient.isConnected()) {
//                Snackbar.make(textViewNumMsgs, "Failed to connect to: '$SOLACE_MQTT_HOST' within 3 seconds", Snackbar.LENGTH_INDEFINITE)
//                    .setAction("Action", null).show()
//            }
//        }

        return v.rootView
    }

    fun setSpinner(){
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.adapter = adapter
        this.spinner.onItemSelectedListener = this
        this.spinner.setSelection(0)
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                val snackbarMsg = "Connected to host:\n'$SOLACE_MQTT_HOST'."
                Log.w("Debug", snackbarMsg)
                Snackbar.make(requireActivity().findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            override fun connectionLost(throwable: Throwable) {
                val snackbarMsg = "Connection to host lost:\n'$SOLACE_MQTT_HOST'"
                Log.w("Debug", snackbarMsg)
                Snackbar.make(requireActivity().findViewById(android.R.id.content), snackbarMsg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", "Message received from host '$SOLACE_MQTT_HOST': $mqttMessage")
//                val str: String = "------------" +"-------------\n$mqttMessage\n${textViewMsgPayload.text}"

                var jsonMsg = JSONObject(String(mqttMessage.payload))
                textViewMsgPayload.text = "str"
            }

            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$SOLACE_MQTT_HOST'")
            }
        })
    }

    // Declared setupSensor function
    private fun setUpSensor() {
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        speedSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
    }

    private fun publishBeacon(view: View) {

        var snackbarMsg : String
        snackbarMsg = "Cannot publish to empty topic!"
        if (topic.isNotEmpty()) {
            snackbarMsg = try {
//                mqttClient.publish(topic, "message")

                when(topic){
                    "app/beacon" -> {
                        var result =""
                        if (baecons!=null){
                            for (b in baecons!!){
                                result += "${b.id1} \n"
                            }
                            mqttClient.publish(topic, result)
                        }
                    }
                    "app/accelerometer" -> { mqttClient.publish(topic, speed)}
                }

                "Published to topic '$topic'"
            } catch (ex: MqttException) {
                "Error publishing to topic: $topic"
            }
        }
        Snackbar.make(requireView(), snackbarMsg, 300)
            .setAction("Action", null).show()
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val motion = kotlin.math.sqrt(
            event!!.values[0].toDouble().pow(2.0) +
                    event.values[1].toDouble().pow(2.0) +
                    event.values[2].toDouble().pow(2.0)
        )

        speed = (motion * 3.6).toFloat().toString()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, speedSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        mqttClient.destroy()
        super.onDestroy()
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
            baecons = beacons as ArrayList<Beacon>
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

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        when (position) {
            0 -> {topic= "app/beacon"}
            1 -> {topic= "app/accelerometer"}
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}