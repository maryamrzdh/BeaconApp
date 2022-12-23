package com.example.anmol.beacons.BeaconSearch

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.anmol.beacons.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import org.altbeacon.beacon.Beacon
import java.util.ArrayList
import kotlin.math.roundToInt

/*
     Adapter for Recycler View
*/
internal class RecyclerAdapter     // Constructor
    (var beaconList: ArrayList<Beacon>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    /*
       View Holder class to instantiate views
     */
    internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //UUID
        val uuid: TextView

        //Major
        val major: TextView

        //Minor
        val minor: TextView

        //Distance
        val distance: TextView
        val type: TextView
        val rssi: TextView
        val count: TextView
        val time: TextView

        //View Holder Class Constructor
        init {

            //Initializing views
            uuid = itemView.findViewById(R.id.uuid)
            major = itemView.findViewById(R.id.major)
            minor = itemView.findViewById(R.id.minor)
            distance = itemView.findViewById(R.id.distance)
            type = itemView.findViewById(R.id.type)
            rssi = itemView.findViewById(R.id.rssi)
            count = itemView.findViewById(R.id.count)
            time = itemView.findViewById(R.id.time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_items, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Getting Array List within respective position
        val beacon = beaconList[position]

        val distance = ((beacon.distance * 100.0).roundToInt() / 100.0).toString()
        holder.uuid.text = beacon.id1.toString()
        holder.major.text = beacon.id2.toString()
        holder.minor.text = beacon.id3.toString()
        holder.distance.text = "$distance meters"
        holder.type.text = beacon.beaconTypeCode.toString()
        holder.rssi.text = beacon.rssi.toString()
//            holder.count.text = beacon.co
//            holder.time.text = beacon.id1
    }

    override fun getItemCount(): Int {
        return beaconList.size
    }
}