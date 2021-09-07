package com.e.dreamcardseogu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    lateinit var navi: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var navigation = findViewById<View>(R.id.navigation)
        navi = navigation as BottomNavigationView
        var transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, MapsFragment(), getString(R.string.map_tag))
        transaction.commit()
        transitionNavigationBottomView(navigation as BottomNavigationView, supportFragmentManager)
    }

    private fun transitionNavigationBottomView(bottomView: BottomNavigationView, fragmentManager: FragmentManager){
        bottomView.setOnItemSelectedListener {
            var transaction = fragmentManager.beginTransaction()
            it.isChecked = true

            when(it.itemId){
                R.id.action_home -> {
                    fragmentManager.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    transaction.replace(R.id.frame, MapsFragment(),getString(R.string.map_tag))
                    transaction.addToBackStack("home")
                    transaction.commit()
                }
                R.id.action_func1 -> {
                    fragmentManager.popBackStack("func1", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    transaction.replace(R.id.frame, RankFragment(),getString(R.string.list_tag))
                    transaction.addToBackStack("func1")
                    transaction.commit()
                }
                R.id.action_func2 -> {
                    fragmentManager.popBackStack("func2", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    transaction.replace(R.id.frame, CardFragment(),getString(R.string.card_tag))
                    transaction.addToBackStack("func2")
                    transaction.commit()
                }
                R.id.action_settings -> {
                    fragmentManager.popBackStack("settings",FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    transaction.replace(R.id.frame, SettingFragment(), getString(R.string.settings_tag))
                    transaction.addToBackStack("settings")
                    transaction.commit()
                }
                else -> Log.d("test", "error") == 0
            }
            Log.d("test", "final") == 0
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        updateBottomMenu(navigation)
    }

    fun updateBottomMenu(navigation: BottomNavigationView) {
        val homeTag: Fragment? = supportFragmentManager.findFragmentByTag(getString(R.string.map_tag))
        val func1Tag: Fragment? = supportFragmentManager.findFragmentByTag(getString(R.string.list_tag))
        val func2Tag: Fragment? = supportFragmentManager.findFragmentByTag(getString(R.string.card_tag))
        val settingsTag: Fragment? = supportFragmentManager.findFragmentByTag(getString(R.string.settings_tag))

        if(homeTag != null && homeTag.isVisible) {navigation.menu.findItem(R.id.action_home).isChecked = true }
        if(func1Tag != null && func1Tag.isVisible) {navigation.menu.findItem(R.id.action_func1).isChecked = true }
        if(func2Tag != null && func2Tag.isVisible) {navigation.menu.findItem(R.id.action_func2).isChecked = true }
        if(settingsTag != null && settingsTag.isVisible) {navigation.menu.findItem(R.id.action_settings).isChecked = true }
    }

    fun moveToMap(lat: Float, lng: Float) {
        var bundle = Bundle()
        bundle.putFloat("latitude",lat)
        bundle.putFloat("longitude",lng)
        var newMap = MapsFragment()
        newMap.arguments = bundle
        supportFragmentManager.beginTransaction().replace(R.id.frame, newMap, getString(R.string.map_tag)).commit()
    }

}