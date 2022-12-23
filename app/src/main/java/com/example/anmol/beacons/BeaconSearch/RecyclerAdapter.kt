package com.example.anmol.beacons.BeaconSearch

import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.anmol.beacons.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import java.util.ArrayList

/*
     Adapter for Recycler View
*/
internal class RecyclerAdapter     // Constructor
    (var arr: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
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
        val arrayList = arr[position]

        // Checking if arrayList size > 0
        if (arrayList.size > 0) {

            // Displaying UUID
            holder.uuid.text = arrayList[0]

            //Displaying Major
            holder.major.text = arrayList[1]

            //Displaying Minor
            holder.minor.text = arrayList[2]

            //Displaying distance
            holder.distance.text = arrayList[3]
        }
    }

    override fun getItemCount(): Int {
        return arr.size
    }
}