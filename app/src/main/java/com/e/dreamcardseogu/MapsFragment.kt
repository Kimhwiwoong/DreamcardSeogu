package com.e.dreamcardseogu

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import jxl.Workbook
import jxl.read.biff.BiffException
import org.apache.log4j.chainsaw.Main
import java.io.IOException
import java.io.InputStream
import kotlin.math.roundToInt

class MapsFragment : Fragment() {
    private lateinit var map : GoogleMap
    private var location: LatLng? = null
    private val seoguDefault = LatLng(37.50, 126.67)
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var finishCallback: OnBackPressedCallback
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        var ref = database.reference
        var storeList: ArrayList<Stores> = excelToStore(getString(R.string.filename))
        var savedSnapshot : DataSnapshot? = null

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                savedSnapshot = snapshot
                for (store in storeList) {
                    var rating = 4.4F
                    rating = snapshot.child("${store.name}").child("rating").value.toString().toFloat()
                    val currentLatLng = LatLng(store.lat, store.lng)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(currentLatLng)
                    markerOptions.title(store.name)
                    markerOptions.snippet(store.phone + "\n ★ : " + (rating * 100 ).roundToInt() / 100f + " / 5 " +
                            "(${snapshot.child("${store.name}").child("contributor").value.toString()}명)")
                    markerOptions.draggable(true)
                    map.addMarker(markerOptions)
                }
                //Toast.makeText(context,"changed",Toast.LENGTH_LONG).show()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("onCancelled : ","${error.toException()}")
            }
        })
        map.setInfoWindowAdapter(CustomInfoWindow(requireContext()))
        map.setOnInfoWindowClickListener {
            val builder = AlertDialog.Builder(activity)
            val dialogView = layoutInflater.inflate(R.layout.rating_layout,null)
            val dialogRatingBar = dialogView.findViewById<RatingBar>(R.id.dialogRb)
            builder.setView(dialogView).apply {
                setPositiveButton("확인"){
                        dialogInterface,i->
                    var people: Float = savedSnapshot?.child(it.title)?.child("contributor")?.value.toString().toFloat()
                    var newPeople: Float = people + 1
                    var exRating: Float = savedSnapshot?.child(it.title)?.child("rating")?.value.toString().toFloat()
                    var newRating: Float = ((exRating * people) + dialogRatingBar.rating) / (people + 1)
                    ref.child(it.title).child("contributor").setValue(newPeople.toInt())
                    ref.child(it.title).child("rating").setValue(newRating)
                    Toast.makeText(context,"재접속 하면 별점이 적용됩니다.",Toast.LENGTH_LONG).show()
                }
                setNegativeButton("취소"){dialogInterface,i->}
            }.show()
        }
        if ( location != null ) {
            (activity as MainActivity).updateBottomMenu((activity as MainActivity).navi)
            val clickedLocation = CameraUpdateFactory.newLatLngZoom(location, 20f)
            map.moveCamera(clickedLocation)
            map.animateCamera(clickedLocation)
        } else {
            val defaultLocation = CameraUpdateFactory.newLatLngZoom(seoguDefault, 15f)
            map.moveCamera(defaultLocation)
            map.animateCamera(defaultLocation)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val lat = arguments?.getFloat("latitude")
        val lng = arguments?.getFloat("longitude")
        if ( lat != null && lng != null ) {
            var latLng = LatLng(lat!!.toDouble(),lng!!.toDouble())
            location = latLng
        }
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun excelToStore(name: String):ArrayList<Stores> {
        var storeList: ArrayList<Stores> = ArrayList()
        try {
            val `is`: InputStream = requireActivity().resources.assets.open(name)
            val wb: Workbook = Workbook.getWorkbook(`is`)
            if (wb != null) {
                val sheet = wb.getSheet(0) // 시트 불러오기
                if (sheet != null) {
                    val colTotal: ArrayList<Int> = ArrayList() // 전체 컬럼
                    colTotal.apply {
                        add(0)
                        add(7)
                        add(8)
                        add(9)
                    }
                    var rowIndexStart = 1 // row 인덱스 시작
                    val rowTotal = sheet.getColumn(colTotal.size - 1).size
                    for (row in rowIndexStart until rowTotal) {
                        var store: Stores = Stores(
                            sheet.getCell(0,row).contents,
                            sheet.getCell(7, row).contents.toDouble(),
                            sheet.getCell(8, row).contents.toDouble(),
                            sheet.getCell(9, row).contents
                        )
                        storeList.add(store)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: BiffException) {
            e.printStackTrace()
        }
        return storeList
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        finishCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val builder = AlertDialog.Builder(activity)
                builder.apply {
                    setTitle(getString(R.string.finishMessage))
                    setPositiveButton("예") {
                            dialogInterface: DialogInterface, i: Int ->
                        activity?.finish()
                    }
                    setNegativeButton("아니오") {
                            dialogInterface: DialogInterface, i: Int ->
                    }
                }.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, finishCallback)
    }

    override fun onDetach() {
        super.onDetach()
        finishCallback.remove()
    }
}