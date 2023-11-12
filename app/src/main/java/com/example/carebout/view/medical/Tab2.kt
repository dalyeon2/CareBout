package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.Inoc.InoculationAdapter
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Inoculation
import com.example.carebout.view.medical.db.InoculationDao

class Tab2 : Fragment() {
   // private val st = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
/*
    fun setInoculation(date: String, name: String, expectedDate: String) : View { // 약 정보 입력
        st.setMargins(0,30,0,30)
        var tablerow: TableRow = TableRow(this.context)     // 넣을 새 row 생성
        var innoDate: TextView = TextView(this.context)     // 넣을 약 이름 text view
        var innoName: TextView = TextView(this.context)   // 넣을 약 복용 기간 text view
        var innoEDate: TextView = TextView(this.context)     // 넣을 약 비고 text view
        tablerow.layoutParams = st                          // 레이아웃 적용

        innoDate.text = date
        innoName.text = name
        innoEDate.text = expectedDate
        innoDate.textSize = 16f
        innoName.textSize = 16f
        innoEDate.textSize = 16f
        innoEDate.setGravity(Gravity.CENTER)
        innoDate.setGravity(Gravity.CENTER)
        innoName.setGravity(Gravity.CENTER)

        tablerow.addView(innoDate)
        tablerow.addView(innoName)
        tablerow.addView(innoEDate)

        return tablerow
    }*/

    private lateinit var db: AppDatabase
    private lateinit var inocDao: InoculationDao
    private var inocList: ArrayList<Inoculation> = ArrayList<Inoculation>()
    private lateinit var adapter: InoculationAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var tag1: ToggleButton
    private lateinit var tag2: ToggleButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tab2View: View = inflater.inflate(R.layout.tab2, container, false)

        db = AppDatabase.getInstance(requireContext())!!
        inocDao = db.getInocDao()

        // RecyclerView 설정
        recyclerView = tab2View.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //UserAdapter 초기화
        adapter = InoculationAdapter(requireContext())
        //Adapter 적용
        recyclerView.adapter = adapter

        // LiveData를 관찰하여 데이터 변경에 대응
        inocDao.getAllInoculation().observe(viewLifecycleOwner, Observer { inocList ->
            // LiveData가 변경될 때마다 호출되는 콜백
            adapter.setInoculationList(inocList as ArrayList<Inoculation>)
        })

        tag1 = tab2View.findViewById(R.id.toggle1)
        tag1.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                if (tag2.isChecked) {
                    tag2.isChecked = false
                }
                Log.i("check", "true")
                getInocTag1List()
            }
        }

        tag2 = tab2View.findViewById(R.id.toggle2)
        tag2.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                if (tag1.isChecked) {
                    tag1.isChecked = false
                }
                Log.i("check", "true")
                getInocTag2List()
            }
        }

        return tab2View
    }

    private fun getInocList() {

        val inocList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocDateAsc() as ArrayList<Inoculation>
        //.getInoculationAll() as ArrayList<Inoculation>

        if (inocList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTag1List() {

        val inocTag1List: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTag1() as ArrayList<Inoculation>

        if (inocTag1List.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTag1List)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTag2List() {

        val inocTag2List: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTag2() as ArrayList<Inoculation>

        if (inocTag2List.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTag2List)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

}