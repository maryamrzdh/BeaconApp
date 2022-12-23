package com.example.anmol.beacons.beaconSimulator

import org.altbeacon.beacon.Beacon
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.anmol.beacons.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View

internal class BeaconListAdapter(var arr: ArrayList<Beacon>) : RecyclerView.Adapter<BeaconListAdapter.ViewHolder>() {
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

        //View Holder Class Constructor
        init {

            //Initializing views
            uuid = itemView.findViewById(R.id.uuid)
            major = itemView.findViewById(R.id.major)
            minor = itemView.findViewById(R.id.minor)
            distance = itemView.findViewById(R.id.distance)
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
    }

    override fun getItemCount(): Int {
//        return if (arr.size == 0) {
//            1
//        } else {
//            arr.size
//        }
        return arr.size
    }

    fun addItems(beacons:ArrayList<Beacon>){
//        arr.clear()
        arr.addAll(beacons)
        notifyDataSetChanged()
    }
}