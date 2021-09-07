package com.e.dreamcardseogu

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindow(context: Context): GoogleMap.InfoWindowAdapter {
    var mContext = context
    var window = (context as Activity).layoutInflater.inflate(R.layout.markerinfo, null)
    fun initWindowText(marker: Marker, view: View){

        val title = view.findViewById<TextView>(R.id.title)
        val snippet = view.findViewById<TextView>(R.id.snippet)

        title.text = marker.title
        snippet.text = marker.snippet

    }

    override fun getInfoContents(marker: Marker): View {
        initWindowText(marker, window)
        return window
    }

    override fun getInfoWindow(marker: Marker): View? {
        initWindowText(marker, window)
        return window
    }
}