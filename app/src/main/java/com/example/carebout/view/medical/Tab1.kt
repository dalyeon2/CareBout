package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.Medicine.MedicineAdapter
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Medicine
import com.example.carebout.view.medical.db.MedicineDao

class Tab1 : Fragment() {
    //private val st = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    /*
    fun setMedi(name: String, peri: String, memo: String, tag: String) : View { // 약 정보 입력
        st.setMargins(0,30,0,30)
        var tablerow: TableRow = TableRow(this.context)     // 넣을 새 row 생성
        var mediName: TextView = TextView(this.context)     // 넣을 약 이름 text view
        var mediPeriod: TextView = TextView(this.context)   // 넣을 약 복용 기간 text view
        var mediMemo: TextView = TextView(this.context)     // 넣을 약 비고 text view
        var mediTag: String = tag
        tablerow.layoutParams = st                          // 레이아웃 적용

        mediName.text = name
        mediPeriod.text = peri
        mediMemo.text = memo
        mediName.textSize = 16f
        mediPeriod.textSize = 16f
        mediMemo.textSize = 16f
        mediMemo.setGravity(Gravity.CENTER)
        mediName.setGravity(Gravity.CENTER)
        mediPeriod.setGravity(Gravity.CENTER)

        tablerow.addView(mediName)
        tablerow.addView(mediPeriod)
        tablerow.addView(mediMemo)

        return tablerow
    }
    */

    fun setTagButtonOnClick(tagButton: ToggleButton) {
        tagButton.setOnClickListener {
            if (tagButton.isChecked) {
//                tagButton.setBackgroundDrawable(resources.getDrawable(R.drawable.fillborn))
            } else {
//                tagButton.setBackgroundDrawable(resources.getDrawable(R.drawable.bornn))
                // sql 문으로
            }
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var medicineDao: MedicineDao
    private var mediList: ArrayList<Medicine> = ArrayList<Medicine>()
    private lateinit var adapter: MedicineAdapter
    private lateinit var checkTag: ToggleButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tab1View: View = inflater.inflate(R.layout.tab1, container, false)
        //inflater.inflate(R.layout.activity_medicine_read, container, false)

        db = AppDatabase.getInstance(requireContext())!!
        medicineDao = db.getMedicineDao()

        // RecyclerView 설정
        recyclerView = tab1View.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // UserAdapter 초기화
        adapter = MedicineAdapter(requireContext())
        recyclerView.adapter = adapter

        // LiveData를 관찰하여 데이터 변경에 대응
        medicineDao.getAllMedicine().observe(viewLifecycleOwner, Observer { medicineList ->
            // LiveData가 변경될 때마다 호출되는 콜백
            if (checkTag.isChecked) {
                // 토글 버튼이 체크된 상태이면
                getMediCheckList()
            } else {
                adapter.setMediList(medicineList as ArrayList<Medicine>)
            }
        })

        // ToggleButton 설정
        checkTag = tab1View.findViewById(R.id.toggle)
        checkTag.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMediCheckList()
            } else {
                // 토글 버튼이 체크 해제된 경우 모든 데이터를 보여줌
                getMedicineList()
            }
        }

        return tab1View
    }

    private fun getMedicineList() {

        val mediList: ArrayList<Medicine> = db?.getMedicineDao()!!.getMediAll() as ArrayList<Medicine>

        if (mediList.isNotEmpty()) {
            //데이터 적용
            adapter.setMediList(mediList)

        } else {

        }
    }

    private fun getMediCheckList() {

        val mediCheckList: ArrayList<Medicine> = db?.getMedicineDao()!!.getMediWithCheck() as ArrayList<Medicine>

        if (mediCheckList.isNotEmpty()) {
            //데이터 적용
            adapter.setMediList(mediCheckList)

        } else {
            adapter.setMediList(ArrayList())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MEDICINE_WRITE_REQUEST && resultCode == AppCompatActivity.RESULT_OK) {
            // MedicineWriteActivity에서 돌아왔고, 성공적으로 저장된 경우
            // 데이터를 업데이트하여 새로운 약 정보를 표시
            getMedicineList()
        }
    }

    companion object {
        const val MEDICINE_WRITE_REQUEST = 1
    }

}