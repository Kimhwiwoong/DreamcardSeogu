package com.e.dreamcardseogu

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.dreamcardseogu.databinding.FragmentRankBinding
import com.google.firebase.database.*
import jxl.Workbook
import jxl.read.biff.BiffException
import org.apache.log4j.chainsaw.Main
import java.io.IOException
import java.io.InputStream

class RankFragment : Fragment() {
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var storeitems: MutableList<Data> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRankBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CustomAdapter()
        var ref = database.reference
        var query: Query = ref.orderByChild("rating").limitToLast(20)
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot1: DataSnapshot) {
                if ( activity != null) {
                    storeitems = rankStore((activity as MainActivity).getString(R.string.filename),snapshot1)
                    adapter.listData = storeitems
                    adapter.setItemClickListener(object:CustomAdapter.OnItemClickListener{
                        override fun onClick(v: View, position: Int) {
                            var lat = storeitems[position].lat.toFloat()
                            var lng = storeitems[position].lon.toFloat()
                            (activity as MainActivity).moveToMap(lat,lng)
                        }
                    })
                    var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView_List)
                    recyclerView.adapter = adapter
                    var myLayoutManager = LinearLayoutManager(activity)
                    myLayoutManager.apply {
                        reverseLayout = true
                        stackFromEnd = true
                    }
                    recyclerView.layoutManager = myLayoutManager
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun rankStore(name: String, snapshot: DataSnapshot?):MutableList<Data> {
        var storeList: MutableList<Data> = mutableListOf()
        try {
            val `is`: InputStream? = activity?.resources?.assets?.open(name)
            val wb: Workbook = Workbook.getWorkbook(`is`)
            if (wb != null && snapshot != null) {
                val sheet = wb.getSheet(0) // 시트 불러오기
                if (sheet != null) {
                    val colTotal: ArrayList<Int> = ArrayList() // 전체 컬럼
                    colTotal.apply {
                        add(0)
                        add(9)
                        add(8)
                        add(7)
                        add(5)
                    }
                    var rowIndexStart = 1 // row 인덱스 시작
                    val rowTotal = sheet.getColumn(colTotal.size - 1).size
                    for( child in snapshot.children) {
                        var childname = child.key
                        for (row in rowIndexStart until rowTotal) {
                            var storename = sheet.getCell(0, row).contents
                            if ( childname == storename) {
                                var store = Data(
                                    storename,
                                    sheet.getCell(9, row).contents,
                                    sheet.getCell(8, row).contents,
                                    sheet.getCell(7, row).contents,
                                    sheet.getCell(5, row).contents,
                                    snapshot.child("${sheet.getCell(0, row).contents}").child("rating").value.toString().toFloat()
                                )
                                storeList.add(store)
                            }
                        }
                    }
                    Log.d("데이터","만들기시작")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: BiffException) {
            e.printStackTrace()
        }
        return storeList
    }
}