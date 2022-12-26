package com.example.anmol.beacons.beaconSimulator

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.anmol.beacons.BeaconNotification
import com.example.anmol.beacons.R
import com.github.johnpersano.supertoasts.library.Style
import com.github.johnpersano.supertoasts.library.SuperActivityToast
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils
import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.BeaconTransmitter
import java.util.*


/*
   This Fragment makes one Bluetooth Supported device to act as beacon
 */
class BeaconSimu : AppCompatActivity() , AdapterView.OnItemSelectedListener{
    // Edit Text for taking both (Major , Minor)
    private lateinit var major: EditText
    private lateinit var minor: EditText

    // Text View for displaying UUID
    private lateinit var uuid: TextView

    private lateinit var spinner : Spinner
    private val paths = arrayOf("ibeacon", "altbeacon")

//    var beaconTypeCode = 0x4c000215
    var beaconTypeCode = 533
    var beaconManufacture = 0x004C

    var lastSeen =Date().time.toString()

    var Beacon.lastSeen : String
    get() = lastSeen
    set(value:String) {lastSeen = value}

    // Button for making device Beacon
    private lateinit var b1: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.beacon_simu)

        // EditText (Major)
        major = findViewById(R.id.major_simu)

        // EditText (Minor)
        minor = findViewById(R.id.minor_simu)

        //TextView (UUID)
        uuid = findViewById(R.id.uuid_simu)

        //Button
        b1 = findViewById(R.id.make_beacon)

        spinner = findViewById(R.id.spinner)
        setSpinner()

        //// Making of random UUID
//        val uuid1 = Math.floor(Math.random() * 8 + 1).toInt()
//            .toString() + "f" + Math.floor(Math.random() * (999999 - 100000) + 100000)
//            .toInt() + "-" + "cf" + Math.floor(
//            Math.random() * 8 + 1
//        ).toInt() + "d-4a0f-adf2-f" + Math.floor(Math.random() * 9999 - 1000 + 1000)
//            .toInt() + "ba9ffa6"

        val uuid1 =UUID.randomUUID().hashCode().toString()
        // Displaying the random UUID to TextView
        uuid.setText(uuid1)

        // Clicking the Make Beacon Button
        b1.setOnClickListener(View.OnClickListener {
            // Checking if user has entered the values of major and minor or not.

            // if user has not entered then display him a toast message to enter respective fields.
            if (major.getText().toString() == "" || minor.getText().toString() == "") {
                SuperActivityToast.create(this, Style(), Style.TYPE_BUTTON)
                    .setText("Please fill Major , Minor")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                    .setAnimations(Style.ANIMATIONS_POP).show()
            } else {

                /* Building up a beacon object
                               Setting up to make this beacon following things :

                               1. UUID
                               2. Major
                               3. Minor
                               4. Manufacturer
                               5. TxPower
                               6. RSSI
                               7. BluetoothName
                               8. DataFields
                             */
                val beacon = Beacon.Builder()
                    .setId1(uuid1)
                    .setId2(major.text.toString())
                    .setId3(minor.text.toString())
//                    .setBeaconTypeCode(beaconTypeCode)
                    .setManufacturer(0x0118)
                    .setTxPower(-69)
                    .setRssi(-66)
                    .setBluetoothName("Hall 1")
                    .setDataFields(listOf(*arrayOf(0L)))
                    .build()
                val beaconParser = BeaconParser()
                    .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25")

                // Making the instance of BeaconTransmitter
                val beaconTransmitter = BeaconTransmitter(this, beaconParser)

                // Start Advertising the above Beacon Object
                beaconTransmitter.startAdvertising(beacon)

                // Toasting the message that beacon succesfully made
                SuperActivityToast.create(this, Style(), Style.TYPE_BUTTON)
                    .setText("Successfully Started")
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_PURPLE))
                    .setAnimations(Style.ANIMATIONS_POP).show()


                // Making some animation and changing the text of button
//                val alphaAnimation = AlphaAnimation(1, 0)
//                alphaAnimation.duration = 2000
//                alphaAnimation.isFillEnabled = true
//                alphaAnimation.interpolator = BounceInterpolator()
//                b1.startAnimation(alphaAnimation)
                b1.setText("Beacon Successfully Made")

//                beacon.lastSeen = Date().time.toString()
                BeaconNotification.beaconList.add(beacon)

//                val intent = intent
//                intent.putExtra("key", beacon)
//                setResult(RESULT_OK, intent)
            }
        })
    }

    fun setSpinner(){
        val adapter = ArrayAdapter(
            this@BeaconSimu,
            android.R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        spinner.setSelection(0)
    }

    override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        when (position) {
//            0 -> {beaconTypeCode = 0x4c000215}
//            1 -> {beaconTypeCode = 0xBEAC}

//            0 -> {beaconTypeCode = 533}
//            1 -> {beaconTypeCode = 48812}

            0 -> {beaconManufacture = 0x004C}
            1 -> {beaconManufacture = 0x0118}
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}