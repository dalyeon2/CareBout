package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.medical.Clinic.ClinicAdapter2
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Clinic
import com.example.carebout.view.medical.db.ClinicDao

class Tab3 : Fragment() {
    private lateinit var db: AppDatabase
    private lateinit var clinicDao: ClinicDao
    private var clinicList: ArrayList<Clinic> = ArrayList<Clinic>()
    private lateinit var adapter: ClinicAdapter2

    private lateinit var recyclerView: RecyclerView
    private lateinit var tag_blood: ToggleButton
    private lateinit var tag_xray: ToggleButton
    private lateinit var tag_ultrasonic: ToggleButton
    private lateinit var tag_ct: ToggleButton
    private lateinit var tag_mri: ToggleButton
    private lateinit var tag_checkup: ToggleButton

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tab3View: View = inflater.inflate(R.layout.tab3, container, false)

        db = AppDatabase.getInstance(requireContext())!!
        clinicDao = db.getClinicDao()

        recyclerView = tab3View.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ClinicAdapter2(requireContext())
        recyclerView.adapter = adapter


        //val application = requireActivity().application as PidApplication
        petId = MyPid.getPid() //application.petId

        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]

        viewModel.mpid.observe(viewLifecycleOwner, Observer { mpid ->
            // mpid가 변경될 때마다 호출되는 콜백
            petId = MyPid.getPid()
            Log.i("petId_tab2", petId.toString())

            //MyPid.setPid(petId)
            //application.petId = mpid
            getClinicList()
        })

        // LiveData를 관찰하여 데이터 변경에 대응
        clinicDao.getAllClinic(petId).observe(viewLifecycleOwner, Observer { clinicList ->
            // LiveData가 변경될 때마다 호출되는 콜백
            adapter.setClinicList(clinicList as ArrayList<Clinic>)
        })

        tag_blood = tab3View.findViewById(R.id.toggleButton1)
        tag_xray = tab3View.findViewById(R.id.toggleButton2)
        tag_ultrasonic = tab3View.findViewById(R.id.toggleButton3)
        tag_ct = tab3View.findViewById(R.id.toggleButton4)
        tag_mri = tab3View.findViewById(R.id.toggleButton5)
        tag_checkup = tab3View.findViewById(R.id.toggleButton6)

        tag_blood.setOnCheckedChangeListener { _, isChecked ->
            getClinicList()
            if (isChecked) {
                val otherTags = listOf(tag_xray, tag_ultrasonic, tag_ct, tag_mri, tag_checkup)
                otherTags.forEach { it.isChecked = false }
                Log.i("check", "true")
                getClinicTagBloodList()
            }
        }

        tag_xray.setOnCheckedChangeListener { _, isChecked ->
            getClinicList()
            if (isChecked) {
                val otherTags = listOf(tag_blood, tag_ultrasonic, tag_ct, tag_mri, tag_checkup)
                otherTags.forEach { it.isChecked = false }
                Log.i("check", "true")
                getClinicTagXRayList()
            }
        }

        tag_ultrasonic.setOnCheckedChangeListener { _, isChecked ->
            getClinicList()
            if (isChecked) {
                val otherTags = listOf(tag_blood, tag_xray, tag_ct, tag_mri, tag_checkup)
                otherTags.forEach { it.isChecked = false }
                Log.i("check", "true")
                getClinicTagUltrasonicList()
            }
        }

        tag_ct.setOnCheckedChangeListener { _, isChecked ->
            getClinicList()
            if (isChecked) {
                val otherTags = listOf(tag_blood, tag_xray, tag_ultrasonic, tag_mri, tag_checkup)
                otherTags.forEach { it.isChecked = false }
                Log.i("check", "true")
                getClinicTagCTList()
            }
        }

        tag_mri.setOnCheckedChangeListener { _, isChecked ->
            getClinicList()
            if (isChecked) {
                val otherTags = listOf(tag_blood, tag_xray, tag_ultrasonic, tag_ct, tag_checkup)
                otherTags.forEach { it.isChecked = false }
                Log.i("check", "true")
                getClinicTagMRIList()
            }
        }


        tag_checkup.setOnCheckedChangeListener { _, isChecked ->
            getClinicList()
            if (isChecked) {
                val otherTags = listOf(tag_blood, tag_xray, tag_ultrasonic, tag_ct, tag_mri)
                otherTags.forEach { it.isChecked = false }
                Log.i("check", "true")
                getClinicTagCheckupList()
            }
        }
        return tab3View
    }

    private fun getClinicList() {
        if(petId != 0){
            val clinicList: ArrayList<Clinic> =
                db?.getClinicDao()!!.getClinicDateAsc(petId) as ArrayList<Clinic>

            if (clinicList.isNotEmpty()) {
                //데이터 적용
                adapter.setClinicList(clinicList)

            } else {
                adapter.setClinicList(ArrayList())
            }
        } else {
            adapter.setClinicList(ArrayList())
        }

    }

    private fun getClinicTagBloodList() {

        val clinicTagBList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagB(petId) as ArrayList<Clinic>

        if (clinicTagBList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagBList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagXRayList() {

        val clinicTagXList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagX(petId) as ArrayList<Clinic>

        if (clinicTagXList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagXList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagUltrasonicList() {

        val clinicTagUList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagU(petId) as ArrayList<Clinic>

        if (clinicTagUList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagUList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagCTList() {

        val clinicTagCList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagC(petId) as ArrayList<Clinic>

        if (clinicTagCList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagCList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagMRIList() {

        val clinicTagMList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagM(petId) as ArrayList<Clinic>

        if (clinicTagMList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagMList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagCheckupList() {

        val clinicTagCheckupList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagCheckup(petId) as ArrayList<Clinic>

        if (clinicTagCheckupList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagCheckupList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    override fun onResume() {
        super.onResume()
        // 다른 화면에서 돌아올 때 토글 버튼을 false로 설정
        val Tags = listOf(tag_blood, tag_xray, tag_ultrasonic, tag_ct, tag_mri, tag_checkup)
        Tags.forEach { it.isChecked = false }
    }
}