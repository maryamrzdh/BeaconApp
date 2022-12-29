package com.example.anmol.beacons

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.anmol.beacons.BeaconSearch.BeaconSearch
import com.example.anmol.beacons.beaconSimulator.BeaconListFragment
import com.example.anmol.beacons.mqtt.PublishFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    // ToolBar
    private var toolbar: Toolbar? = null

    // TabLayout
    private var tabLayout: TabLayout? = null

    // ViewPager
    private var viewPager: ViewPager? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting up of view
        setContentView(R.layout.activity_main)

        // Intializing of Layout Views
        initializedLayout()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askPermissions(true)
        }
    }

    //Intializing of Layout Views
    fun initializedLayout() {

        // Setting up of customized toolbar
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // Setting up of View Pager
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        setupViewPager(viewPager)

        //Setting up of TabLayout
        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)
    }


private fun askPermissions(isForOpen: Boolean) {
    isRationale = false
    val permissionsRequired: MutableList<String> = ArrayList()
    val permissionsList: MutableList<String> = ArrayList()
    if (!checkPermission(
            permissionsList,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    ) permissionsRequired.add("Write External Storage")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        if (!checkPermission(
                permissionsList,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) permissionsRequired.add("Write Contact")
    }
    if (permissionsList.size > 0 && !isRationale) {
//            if (permissionsRequired.size > 0) {
//            }
        if (isForOpen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    this, permissionsList.toTypedArray(),
                    permissionRequest
                )
            }
        }
    } else if (isRationale) {
        if (isForOpen) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Permission Alert")
                .setMessage("You need to grant permissions manually. Go to permission and grant all permissions.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts(
                        "package",
                        packageName, null
                    )
                    intent.data = uri
                    startActivityForResult(intent, permissionRequestDialog)
                }
                .show()
        }
    } else {
//            startActivity(Intent(this@PermissionsActivity, SplashActivity::class.java))
//            finish()
//            removeContact()
    }
}

private fun checkPermission(permissionsList: MutableList<String>, permission: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permission)
            // Check for Rationale Option
            if (!isFirst) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    isRationale = true
                    return false
                }
            }
        }
    }
    return true
}


override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    when (requestCode) {
        permissionRequest -> {
            val perms: MutableMap<String, Int> = HashMap()
            // Initial
            perms[Manifest.permission.ACCESS_COARSE_LOCATION] = PackageManager.PERMISSION_GRANTED
            perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] = PackageManager.PERMISSION_GRANTED
            // Fill with results
            var i = 0
            while (i < permissions.size) {
                perms[permissions[i]] = grantResults[i]
                i++
            }
            // Check for ACCESS_FINE_LOCATION
            if (perms[Manifest.permission.ACCESS_COARSE_LOCATION] == PackageManager.PERMISSION_GRANTED &&
                perms[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == PackageManager.PERMISSION_GRANTED
            ) {
                // All Permissions Granted
//                    startActivity(Intent(this@PermissionsActivity, SplashActivity::class.java))
//                    finish()
//                    removeContact()
            } else {
                // Permission Denied
                Toast.makeText(this, "Some Permission is Denied.", Toast.LENGTH_SHORT)
                    .show()
                isFirst = false
                askPermissions(true)
            }
        }
        else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(BeaconSearch(), "Search")
        adapter.addFragment(BeaconListFragment(), "Simulator")
        adapter.addFragment(PublishFragment(), "Publish")
        viewPager!!.adapter = adapter
    }

    // Setting up the ViewPagerAdapter
    internal inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(
        manager!!
    ) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_COARSE_LOCATION = 1
        private const val PERMISSION_REQUEST_COARSE_LOCATION_BACK = 2

        private val permissionRequest=11
        private val permissionRequestDialog=123

        private var isRationale:Boolean=false
        private var isFirst:Boolean=true

    }
}