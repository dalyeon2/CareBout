package com.example.carebout.view.medical.Clinic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.databinding.ActivityClinicReadBinding
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Clinic
import com.example.carebout.view.medical.db.ClinicDao
import com.example.carebout.view.medical.db.Inoculation
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ClinicReadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClinicReadBinding

    private lateinit var db: AppDatabase
    private lateinit var clinicDao: ClinicDao
    private var clinicList: ArrayList<Clinic> = ArrayList<Clinic>()
    private lateinit var adapter: ClinicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClinicReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db 인스턴스를 가져오고 db작업을 할 수 있는 dao를 가져옵니다.
        db = AppDatabase.getInstance(this)!!
        clinicDao = db.getClinicDao()

        val insertBtn: FloatingActionButton = findViewById(R.id.insert_btn)
        val toggleButtons = arrayOf(
            binding.toggleButton1, binding.toggleButton2, binding.toggleButton3,
            binding.toggleButton4, binding.toggleButton5, binding.toggleButton6
        )

        insertBtn.setOnClickListener {
            toggleButtons.forEach { it.isChecked = false }
            val intent = Intent(this, ClinicWriteActivity::class.java)
            activityResult.launch(intent)
        }

        //RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //UserAdapter 초기화
        adapter = ClinicAdapter(this)

        //Adapter 적용
        recyclerView.adapter = adapter


        //조회
        getClinicList()

        val tag_blood = binding.toggleButton1
        val tag_xray = binding.toggleButton2
        val tag_ultrasonic = binding.toggleButton3
        val tag_ct = binding.toggleButton4
        val tag_mri = binding.toggleButton5
        val tag_checkup = binding.toggleButton6

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

    }

    //액티비티가 백그라운드에 있는데 호출되면 실행 /수정화면에서 호출시 실행
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //사용자 조회
        getClinicList()
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            //들어온 값이 OK라면
            //리스트 조회
            getClinicList()
        }
    }

    //리스트 조회
    private fun getClinicList() {

        val clinicList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicAll() as ArrayList<Clinic>

        if (clinicList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicList)

        } else {

        }
    }

    private fun getClinicTagBloodList() {

        val clinicTagBList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagB() as ArrayList<Clinic>

        if (clinicTagBList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagBList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagXRayList() {

        val clinicTagXList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagX() as ArrayList<Clinic>

        if (clinicTagXList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagXList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagUltrasonicList() {

        val clinicTagUList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagU() as ArrayList<Clinic>

        if (clinicTagUList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagUList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagCTList() {

        val clinicTagCList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagC() as ArrayList<Clinic>

        if (clinicTagCList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagCList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagMRIList() {

        val clinicTagMList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagM() as ArrayList<Clinic>

        if (clinicTagMList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagMList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

    private fun getClinicTagCheckupList() {

        val clinicTagCheckupList: ArrayList<Clinic> =
            db?.getClinicDao()!!.getClinicWithTagCheckup() as ArrayList<Clinic>

        if (clinicTagCheckupList.isNotEmpty()) {
            //데이터 적용
            adapter.setClinicList(clinicTagCheckupList)

        } else {
            adapter.setClinicList(ArrayList())
        }
    }

}
