package com.example.anmol.beacons.beaconSimulator

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.anmol.beacons.BeaconNotification
import com.example.anmol.beacons.BeaconSearch.RecyclerAdapter
import com.example.anmol.beacons.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.altbeacon.beacon.Beacon

class BeaconListFragment : Fragment() {

    private lateinit var adapter:BeaconListAdapter
    lateinit var rec :RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_beacon_list, container, false)
        rec = view.findViewById(R.id.beacon_list)
        rec.layoutManager = LinearLayoutManager(activity)
        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btn_add)
        btnAdd.setOnClickListener {

            openAddFragment()
        }
        initAdapter()
        return view.rootView
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
//        adapter.addItems(BeaconNotification.beaconList)
//        adapter.notifyDataSetChanged()

        initAdapter()
    }

    fun initAdapter(){
        adapter = BeaconListAdapter(BeaconNotification.beaconList)
        rec.adapter = adapter
        adapter.notifyDataSetChanged()

    }

    fun openAddFragment(){

        val intent = Intent(requireActivity() , BeaconSimu::class.java)
        startActivity(intent)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode ==100 && resultCode == RESULT_OK){
//            adapter.addItem(data?.getSerializableExtra("key") as Beacon)
//        }
//    }
}