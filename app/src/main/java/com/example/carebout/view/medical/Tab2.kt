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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.carebout.R
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.medical.Inoc.InoculationAdapter
import com.example.carebout.view.medical.Inoc.InoculationAdapter2
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Inoculation
import com.example.carebout.view.medical.db.InoculationDao

class Tab2 : Fragment() {
    private lateinit var db: AppDatabase
    private lateinit var inocDao: InoculationDao
    private lateinit var personalInfoDao: PersonalInfoDao
    private var inocList: ArrayList<Inoculation> = ArrayList<Inoculation>()
    private lateinit var adapter: InoculationAdapter2

    private lateinit var recyclerView: RecyclerView
    private lateinit var tagDHPPL: ToggleButton
    private lateinit var tagC: ToggleButton
    private lateinit var tagKC: ToggleButton
    private lateinit var tagCVRP: ToggleButton
    private lateinit var tagFL: ToggleButton
    private lateinit var tagFID: ToggleButton
    private lateinit var tagR: ToggleButton
    private lateinit var tagH: ToggleButton

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tab2View: View = inflater.inflate(R.layout.tab2, container, false)

        db = AppDatabase.getInstance(requireContext())!!
        inocDao = db.getInocDao()
        personalInfoDao = db.personalInfoDao()

        // RecyclerView 설정
        recyclerView = tab2View.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //UserAdapter 초기화
        adapter = InoculationAdapter2(requireContext())
        //Adapter 적용
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
            getInocList()
            updatePetTag()
        })


        // LiveData를 관찰하여 데이터 변경에 대응
        inocDao.getAllInoculation(petId).observe(viewLifecycleOwner, Observer { inocList ->
            // LiveData가 변경될 때마다 호출되는 콜백
            adapter.setInoculationList(inocList as ArrayList<Inoculation>)
        })

        tagDHPPL = tab2View.findViewById(R.id.toggleButton1)
        tagDHPPL.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagC, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagDHPPLList()
            }
        }

        tagC = tab2View.findViewById(R.id.toggleButton2)
        tagC.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagCList()
            }
        }

        tagKC = tab2View.findViewById(R.id.toggleButton3)
        tagKC.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagKCList()
            }
        }

        tagCVRP = tab2View.findViewById(R.id.toggleButton4)
        tagCVRP.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagCVRPList()
            }
        }

        tagFL = tab2View.findViewById(R.id.toggleButton5)
        tagFL.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagFLList()
            }
        }

        tagFID = tab2View.findViewById(R.id.toggleButton6)
        tagFID.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagFIDList()
            }
        }

        tagR = tab2View.findViewById(R.id.toggleButton7)
        tagR.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagH)
                otherTags.forEach { it.isChecked = false }
                getInocTagRList()
            }
        }

        tagH = tab2View.findViewById(R.id.toggleButton8)
        tagH.setOnCheckedChangeListener { _, isChecked ->
            getInocList()
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagR)
                otherTags.forEach { it.isChecked = false }
                getInocTagHList()
            }
        }

        return tab2View
    }

    private fun updatePetTag() {

        val petList = personalInfoDao.getInfoById(petId)

        if (petId != 0 && petList!!.animal == "dog") {
            tagCVRP.visibility = View.GONE
            tagDHPPL.visibility = View.VISIBLE
        } else {
            tagCVRP.visibility = View.VISIBLE
            tagDHPPL.visibility = View.GONE
        }
    }

    private fun getInocList() {
        if(petId != 0) {
            val inocList: ArrayList<Inoculation> = db?.getInocDao()!!.getInocDateAsc(petId) as ArrayList<Inoculation>
            //.getInoculationAll() as ArrayList<Inoculation>

            if (inocList.isNotEmpty()) {
                //데이터 적용
                adapter.setInoculationList(inocList)

            } else {
                adapter.setInoculationList(ArrayList())
            }
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

    override fun onResume() {
        super.onResume()
        // 다른 화면에서 돌아올 때 토글 버튼을 false로 설정
        val Tags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
        Tags.forEach { it.isChecked = false }
    }

}