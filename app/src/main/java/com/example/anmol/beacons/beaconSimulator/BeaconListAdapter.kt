package com.example.anmol.beacons.beaconSimulator

import android.content.Context
import org.altbeacon.beacon.Beacon
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.anmol.beacons.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View

internal class BeaconListAdapter(var context : Context, var arr: ArrayList<Beacon>) : RecyclerView.Adapter<BeaconListAdapter.ViewHolder>() {
    /*
       View Holder class to instantiate views
     */
    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //UUID
        val uuid: TextView

        //Major
        val major: TextView

        //Minor
        val minor: TextView

        //Distance
        val distance: TextView
        val type: TextView

        //View Holder Class Constructor
        init {

            //Initializing views
            uuid = itemView.findViewById(R.id.uuid)
            major = itemView.findViewById(R.id.major)
            minor = itemView.findViewById(R.id.minor)
            distance = itemView.findViewById(R.id.distance)
            type = itemView.findViewById(R.id.type)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_card_search, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        // Getting Array List within respective position
        val beacon = arr[position]

        // Displaying UUID
        holder.uuid.text = beacon.id1.toString()

        //Displaying Major
        holder.major.text = beacon.id2.toString()

        //Displaying Minor
        holder.minor.text = beacon.id3.toString()

        //Displaying distance
        holder.distance.text = beacon.distance.toString()

        holder.type.text = beaconType(beacon.beaconTypeCode)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    fun addItems(beacons:ArrayList<Beacon>){
//        arr.clear()
        arr.addAll(beacons)
        notifyDataSetChanged()
    }

    private fun beaconType(code:Int):String {
        return when(code){
            48812 -> context.getString(R.string.altbeacon)
            533 -> context.getString(R.string.ibeacon)
            else->""
        }
    }
}