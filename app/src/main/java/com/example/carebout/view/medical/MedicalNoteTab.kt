package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.carebout.R
import com.example.carebout.databinding.ActivityMedicalBinding
import com.example.carebout.databinding.MedicalNoteTabBinding
import com.example.carebout.view.medical.Medicine.MedicineReadActivity
import com.example.carebout.view.medical.Todo.TodoReadActivity
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.DailyTodo
import com.example.carebout.view.medical.db.Medicine
import com.example.carebout.view.medical.db.TodoDao
import com.google.android.material.tabs.TabLayout


class MedicalNoteTab : Fragment() {
    private lateinit var db: AppDatabase
    private lateinit var todoDao: TodoDao

    var tab1 = Tab1()
    var tab2 = Tab2()
    var tab3 = Tab3()
    var dailycare = Dailycare()
    val currentWeight = CurrentWeight()
    val mediing = Mediing()

    lateinit var mediingNull: TextView
    lateinit var dailycareNull: TextView

    lateinit var allTodoList: ArrayList<DailyTodo>
    lateinit var allMedicineList: ArrayList<Medicine>

    fun subUnselectBorn() {
        dailycare.unSelectBorn()
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    private fun updateData() {
        allTodoList = db?.getTodoDao()!!.getTodoAll() as ArrayList<DailyTodo>
        if(allTodoList.isNotEmpty()) {
            dailycareNull.visibility = View.GONE
        }else {
            dailycareNull.visibility = View.VISIBLE
        }

        allMedicineList = db?.getMedicineDao()!!.getMediWithCheck() as ArrayList<Medicine>
        if(allMedicineList.isNotEmpty()) {
            mediingNull.visibility = View.GONE
        }else {
            mediingNull.visibility = View.VISIBLE
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = AppDatabase.getInstance(requireContext())!!
        todoDao = db.getTodoDao()

        val medicalNoteTabView: View = inflater.inflate(R.layout.medical_note_tab, container, false)

        var tabs = medicalNoteTabView.findViewById<TabLayout>(R.id.tabs)
        var dailycareMenu = medicalNoteTabView.findViewById<ImageView>(R.id.dailycareMenu)
        var medi = medicalNoteTabView.findViewById<RelativeLayout>(R.id.mediing)
        dailycareNull = medicalNoteTabView.findViewById<TextView>(R.id.dailycareNull)
        mediingNull = medicalNoteTabView.findViewById<TextView>(R.id.mediingNull)

        var selected: Fragment? = tab3
        childFragmentManager.beginTransaction().replace(R.id.frame1, selected!!).commit()
        childFragmentManager.beginTransaction().replace(R.id.dailycareFrame, dailycare!!).commit()
        childFragmentManager.beginTransaction().replace(R.id.currentWeight, currentWeight!!).commit()
        childFragmentManager.beginTransaction().replace(R.id.mediing, mediing!!).commit()

        allTodoList  = db?.getTodoDao()!!.getTodoAll() as ArrayList<DailyTodo>
        allMedicineList = db?.getMedicineDao()!!.getMediWithCheck() as ArrayList<Medicine>

        updateData()

        dailycareMenu.setOnClickListener{
            val intent = Intent(getActivity(), TodoReadActivity::class.java)
            startActivity(intent)
        }

        medi.setOnClickListener{
            val intent = Intent(getActivity(), MedicineReadActivity::class.java)
            intent.putExtra("isTrue", true)
            startActivity(intent)
        }

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position

                if (position == 0) {
                    selected = tab3
                } else if (position == 1) {
                    selected = tab2
                } else if (position == 2) {
                    selected = tab1
                }
                childFragmentManager.beginTransaction().replace(R.id.frame1, selected!!).commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return medicalNoteTabView
    }
}