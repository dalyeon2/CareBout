package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.carebout.R
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.medical.Medicine.MedicineReadActivity
import com.example.carebout.view.medical.Todo.TodoReadActivity
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.DailyTodo
import com.example.carebout.view.medical.db.Medicine
import com.example.carebout.view.medical.db.TodoDao
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MedicalNoteTab : Fragment() {
    private lateinit var db: AppDatabase
    private lateinit var todoDao: TodoDao

    private lateinit var personalInfoDao: PersonalInfoDao
    private lateinit var petRadioGroup: RadioGroup
    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0

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
    private var currentToast: Toast? = null

    fun showCustomToast(context: Context, message: String) {
        currentToast?.cancel()

        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val text = layout.findViewById<TextView>(R.id.custom_toast_text)
        text.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout

        val toastDurationInMilliSeconds: Long = 3000
        toast.duration =
            if (toastDurationInMilliSeconds > Toast.LENGTH_LONG) Toast.LENGTH_LONG else Toast.LENGTH_SHORT

        toast.setGravity(Gravity.BOTTOM, 0, 200)

        currentToast = toast

        toast.show()
    }
    override fun onResume() {
        super.onResume()

        // val application = requireActivity().application as PidApplication
        petId = MyPid.getPid() //application.petId

        updatePetSelection()
        updateFragments()
        updateData()
    }

    private fun updateFragments() {
//        val selectedRadioButton = petRadioGroup.findViewById<RadioButton>(petId ?: -1)
//        val selectedPetId = selectedRadioButton?.tag as? Int

        //val application = requireActivity().application as PidApplication
        //val selectedPetId = application.petId
        val p = MyPid.getPid()
        Log.i("updateFragments", p.toString())
        petId = p

        // 각 탭에 petId 값을 전달
        currentWeight.updatePetId(p)
        mediing.updatePetId(p)

        viewModel.setSelectedPetId(p)
        Log.i("updateFragments", viewModel.getSelectedPetId().value.toString())
    }

    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            if (petId != 0) {
                allTodoList = db?.getTodoDao()!!.getTodoAll() as ArrayList<DailyTodo>
                if (allTodoList.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        dailycareNull.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        dailycareNull.visibility = View.VISIBLE
                    }
                }

                Log.i("update_check_medi", petId.toString())
                allMedicineList = db?.getMedicineDao()!!.getMediWithCheck(petId) as ArrayList<Medicine>
                if (allMedicineList.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        mediingNull.visibility = View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        mediingNull.visibility = View.VISIBLE
                        Log.i("update_check_medi_1", petId.toString())
                    }
                }
            }
            else {
                withContext(Dispatchers.Main) {
                    dailycareNull.visibility = View.VISIBLE
                    mediingNull.visibility = View.VISIBLE
                    Log.i("update_check_medi_2", petId.toString())
                }
            }
        }
    }
//        allTodoList = db?.getTodoDao()!!.getTodoAll() as ArrayList<DailyTodo>
//        if(allTodoList.isNotEmpty()) {
//            dailycareNull.visibility = View.GONE
//        }else {
//            dailycareNull.visibility = View.VISIBLE
//        }
//
//        allMedicineList = db?.getMedicineDao()!!.getMediWithCheck() as ArrayList<Medicine>
//        if(allMedicineList.isNotEmpty()) {
//            mediingNull.visibility = View.GONE
//        }else {
//            mediingNull.visibility = View.VISIBLE
//        }
//    }

    private fun updatePetSelection() {
        // 라디오 그룹 초기화
        petRadioGroup.removeAllViews()

        Log.i("updatePetSelection", "medical")

        // 펫 목록 가져오기
        val petList = personalInfoDao.getAllInfo()

        // 펫 목록이 비어있지 않을 때
        if (petList.isNotEmpty()) {
            // 라디오 버튼 동적 생성
            for (pet in petList) {
                val radioButton = RadioButton(requireContext())
                radioButton.text = pet.name
                radioButton.tag = pet.pid  // pid 태그에 저장
                radioButton.buttonDrawable = getResources().getDrawable(R.drawable.medical_radiobtn_check)
                radioButton.setPadding(0, 5, 20, 5)
                radioButton.textSize = 20.0f
                petRadioGroup.addView(radioButton)
                Log.i("pid_animal", pet.animal)

                // 가져온 petId 값과 현재 라디오 버튼의 tag가 일치하면 선택
                if (petId != 0 && pet.pid == MyPid.getPid()) {
                    radioButton.isChecked = true
                    Log.i("pet_checked", pet.name)

                    //updateFragments()
                }

                Log.i("pet", pet.name)
            }

//            // 첫 번째 라디오 버튼을 디폴트로 선택
//            val firstRadioButton = petRadioGroup.getChildAt(0) as? RadioButton
//            firstRadioButton?.isChecked = true
        } else {
            petRadioGroup.removeAllViews()
            MyPid.setPid(0)
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
        personalInfoDao = db.personalInfoDao()

        val medicalNoteTabView: View = inflater.inflate(R.layout.medical_note_tab, container, false)

        var tabs = medicalNoteTabView.findViewById<TabLayout>(R.id.tabs)
        var dailycareMenu = medicalNoteTabView.findViewById<ImageView>(R.id.dailycareMenu)
        var medi = medicalNoteTabView.findViewById<LinearLayout>(R.id.mediingFrame)
        dailycareNull = medicalNoteTabView.findViewById<TextView>(R.id.dailycareNull)
        mediingNull = medicalNoteTabView.findViewById<TextView>(R.id.mediingNull)

        petRadioGroup = medicalNoteTabView.findViewById(R.id.radioGroup)
        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]

        // 데이터 읽기
//        val application = requireActivity().application as PidApplication
//        petId = application.petId

        val petList = personalInfoDao.getAllInfo()

        // 펫 목록이 비어있을 때
        if (petList.isEmpty()) {
            petId = 0
        } else {
            petId = MyPid.getPid()
        }

        Log.i("petId_medical", petId.toString())

        var p_view = petId
//        if(viewModel.getSelectedPetId().value != null) {
//            p_view = viewModel.getSelectedPetId().value!!
//            Log.i("p_view", p_view.toString())
//        }

        updatePetSelection()

        // 라디오 버튼 리스너 설정
        petRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            // 선택된 라디오 버튼에 대한 작업을 수행
            val selectedRadioButton = group.findViewById<RadioButton>(checkedId)
            val selectedPetId = selectedRadioButton?.tag as? Int

            petId = (selectedRadioButton?.tag as Int?)!!
            MyPid.setPid(petId)

            //(requireActivity().application as PidApplication).petId = petId
            //Log.i("petId_app", (requireActivity().application as PidApplication).petId.toString())

            // 각 탭에 petId 값을 전달

            currentWeight.updatePetId(petId)
            mediing.updatePetId(petId)

            if (selectedPetId != null) {
                viewModel.setSelectedPetId(petId)
            }

            updateData()

            Log.i("medinote", viewModel.getSelectedPetId().value.toString())
        }

        var selected: Fragment? = tab3
        childFragmentManager.beginTransaction().replace(R.id.frame1, selected!!).commit()
        childFragmentManager.beginTransaction().replace(R.id.dailycareFrame, dailycare!!).commit()
        childFragmentManager.beginTransaction().replace(R.id.currentWeight, currentWeight!!).commit()
        childFragmentManager.beginTransaction().replace(R.id.mediingFrame, mediing!!).commit()

        allTodoList  = db?.getTodoDao()!!.getTodoAll() as ArrayList<DailyTodo>

        if(petId == 0) {
            allMedicineList = ArrayList()
        } else {
            allMedicineList = db?.getMedicineDao()!!.getMediWithCheck(petId) as ArrayList<Medicine>
        }


        updateData()

        if(petId != 0) {
            dailycareMenu.setOnClickListener{
                val intent = Intent(getActivity(), TodoReadActivity::class.java)
                startActivity(intent)
            }

            medi.setOnClickListener{
                val intent = Intent(getActivity(), MedicineReadActivity::class.java)
                intent.putExtra("isTrue", true)
                startActivity(intent)
            }
        } else {
            dailycareMenu.setOnClickListener{
                showCustomToast(requireContext(), "반려동물을 먼저 등록해주세요.")
//                Toast.makeText(
//                    requireContext(),
//                    "반려동물을 먼저 등록해주세요",
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            medi.setOnClickListener{
                showCustomToast(requireContext(), "반려동물을 먼저 등록해주세요.")
//                Toast.makeText(
//                    requireContext(),
//                    "반려동물을 먼저 등록해주세요",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
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