package com.example.carebout.view.medical.Inoc

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.databinding.ActivityInoculationReadBinding
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.medical.MedicalActivity
import com.example.carebout.view.medical.MedicalViewModel
import com.example.carebout.view.medical.MyPid
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Inoculation
import com.example.carebout.view.medical.db.InoculationDao
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InoculationReadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInoculationReadBinding

    private lateinit var db: AppDatabase
    private lateinit var inocDao: InoculationDao
    private lateinit var personalInfoDao: PersonalInfoDao

    private var inocList: ArrayList<Inoculation> = ArrayList<Inoculation>()
    private lateinit var adapter: InoculationAdapter

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0

    override fun onResume() {
        super.onResume()
        // 다른 화면에서 돌아올 때 토글 버튼을 false로 설정
        val toggleButtons = arrayOf(
            binding.toggleButton1, binding.toggleButton2, binding.toggleButton3,
            binding.toggleButton4, binding.toggleButton5, binding.toggleButton6,
            binding.toggleButton7, binding.toggleButton8)
        toggleButtons.forEach { it.isChecked = false }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInoculationReadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //db 인스턴스를 가져오고 db작업을 할 수 있는 dao를 가져옵니다.
        db = AppDatabase.getInstance(this)!!
        inocDao = db.getInocDao()
        personalInfoDao = db.personalInfoDao()

        binding.topBarOuter.activityTitle.text = "접종/구충"

        binding.topBarOuter.backToActivity.setOnClickListener {
            val intent = Intent(applicationContext, MedicalActivity::class.java)
            startActivity(intent)
            finish()
        }

//        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]
//        petId = viewModel.getSelectedPetId().value

        petId = MyPid.getPid()
            //(application as PidApplication).petId
        Log.i("petId_app", petId.toString())

        updatePetTag()

        val insertBtn: FloatingActionButton = findViewById(R.id.insert_btn)

        insertBtn.setOnClickListener {
            val intent: Intent = Intent(this, InoculationWriteActivity::class.java)
            activityResult.launch(intent)
        }

        //RecyclerView 설정
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //UserAdapter 초기화
        adapter = InoculationAdapter(this)

        //Adapter 적용
        recyclerView.adapter = adapter

        //조회
        getInocList()

        val tagDHPPL = binding.toggleButton1
        val tagC = binding.toggleButton2
        val tagKC = binding.toggleButton3
        val tagCVRP = binding.toggleButton4
        val tagFL = binding.toggleButton5
        val tagFID = binding.toggleButton6
        val tagR = binding.toggleButton7
        val tagH = binding.toggleButton8

//        tag1.setOnCheckedChangeListener { _, isChecked ->
//            getInocList()
//            if (isChecked) {
//                if (tag2.isChecked) {
//                    tag2.isChecked = false
//                }
//                Log.i("check", "true")
//                getInocTag1List()
//            }
//        }
//
//        tag2.setOnCheckedChangeListener { _, isChecked ->
//            getInocList()
//            if (isChecked) {
//                if (tag1.isChecked) {
//                    tag1.isChecked = false
//                }
//                Log.i("check", "true")
//                getInocTag2List()
//            }
//        }

        tagDHPPL.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagC, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagDHPPLList()
            }
        }

        tagC.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagCList()
            }
        }

        tagKC.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagKCList()
            }
        }

        tagCVRP.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagCVRPList()
            }
        }

        tagFL.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagFLList()
            }
        }

        tagFID.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagFIDList()
            }
        }

        tagR.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagRList()
            }
        }

        tagH.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagR)
                otherTags.forEach { it.isChecked = false }
                getInocTagHList()
            }
        }
    }

    //액티비티가 백그라운드에 있는데 호출되면 실행 /수정화면에서 호출시 실행
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        //사용자 조회
        getInocList()
    }

    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            //들어온 값이 OK라면
            //리스트 조회
            getInocList()
        }
    }

    private fun updatePetTag() {
        val tagDHPPL = binding.toggleButton1
        val tagCVRP = binding.toggleButton4

        val petList = personalInfoDao.getInfoById(petId)

        if (petList!!.animal == "dog") {
            tagCVRP.visibility = View.GONE
            tagDHPPL.visibility = View.VISIBLE
        } else {
            tagCVRP.visibility = View.VISIBLE
            tagDHPPL.visibility = View.GONE
        }
    }

    //리스트 조회
    private fun getInocList() {

        val inocList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocDateAsc(petId) as ArrayList<Inoculation>
        //.getInoculationAll() as ArrayList<Inoculation>

        if (inocList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagDHPPLList() {

        val inocTagDHPPLList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagDHPPL(petId) as ArrayList<Inoculation>

        if (inocTagDHPPLList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagDHPPLList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagCList() {

        val inocTagCList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagC(petId) as ArrayList<Inoculation>

        if (inocTagCList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagCList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagKCList() {

        val inocTagKCList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagKC(petId) as ArrayList<Inoculation>

        if (inocTagKCList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagKCList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagCVRPList() {

        val inocTagCVRPList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagCVRP(petId) as ArrayList<Inoculation>

        if (inocTagCVRPList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagCVRPList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagFLList() {

        val inocTagFLList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagFL(petId) as ArrayList<Inoculation>

        if (inocTagFLList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagFLList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagFIDList() {

        val inocTagFIDList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagFID(petId) as ArrayList<Inoculation>

        if (inocTagFIDList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagFIDList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagRList() {

        val inocTagRList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagR(petId) as ArrayList<Inoculation>

        if (inocTagRList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagRList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }

    private fun getInocTagHList() {

        val inocTagHList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocWithTagH(petId) as ArrayList<Inoculation>

        if (inocTagHList.isNotEmpty()) {
            //데이터 적용
            adapter.setInoculationList(inocTagHList)

        } else {
            adapter.setInoculationList(ArrayList())
        }
    }
}