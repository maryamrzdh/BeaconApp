package com.example.anmol.beacons.beaconSimulator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
class BeaconSimu : AppCompatActivity() {
    // Edit Text for taking both (Major , Minor)
    private lateinit var major: EditText
    private lateinit var minor: EditText

    // Text View for displaying UUID
    private lateinit var uuid: TextView

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

        // Making of random UUID
        val uuid1 = Math.floor(Math.random() * 8 + 1).toInt()
            .toString() + "f" + Math.floor(Math.random() * (999999 - 100000) + 100000)
            .toInt() + "-" + "cf" + Math.floor(
            Math.random() * 8 + 1
        ).toInt() + "d-4a0f-adf2-f" + Math.floor(Math.random() * 9999 - 1000 + 1000)
            .toInt() + "ba9ffa6"
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
                    .setId2(major.getText().toString())
                    .setId3(minor.getText().toString())
                    .setManufacturer(0x0118)
                    .setTxPower(-69)
                    .setRssi(-66)
                    .setBluetoothName("Hall 1")
                    .setDataFields(Arrays.asList(*arrayOf(0L)))
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

                BeaconNotification.beaconList.add(beacon)

//                val intent = intent
//                intent.putExtra("key", beacon)
//                setResult(RESULT_OK, intent)
            }
        })
    }
}