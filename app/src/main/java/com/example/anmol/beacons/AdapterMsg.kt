package com.example.anmol.beacons

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

internal class AdapterMsg     // Constructor
    (var context : Context , var beaconList: ArrayList<String>) : RecyclerView.Adapter<AdapterMsg.ViewHolder>() {
    /*
       View Holder class to instantiate views
     */
    internal open inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //UUID
        val uuid: TextView

        //View Holder Class Constructor
        init {
            uuid = itemView.findViewById(R.id.txt_uuid)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.search_items, parent, false)
        )
    }

     override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val beacon = beaconList[position]

        holder.uuid.text = beacon
    }


    override fun getItemCount(): Int {
        return beaconList.size
    }

}