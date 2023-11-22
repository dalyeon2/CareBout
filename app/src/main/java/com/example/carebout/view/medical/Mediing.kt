package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.carebout.R
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Medicine
import com.example.carebout.view.medical.db.MedicineDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


val st = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT)


public class Medi(nm: String = "", pr: String = "", ing: Boolean = false) {

    var name: String = ""
    var period: String = ""
    var isIng : Boolean = false

    init {
        this.name = nm
        this.period = pr
        this.isIng = ing
    }

    @JvmName("getString")
    public fun getName() : String {
        return name
    }

    @JvmName("getInt")
    public fun getPeriod() : String {
        return period
    }
}

class Mediing : Fragment() {


    private lateinit var db: AppDatabase
    private lateinit var medicineDao: MedicineDao

    lateinit var allMediList: List<Medicine>
    private lateinit var lay: LinearLayout

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0

    fun setMedicine(md: Medi) : View {
        var mediView = TextView(this.context) // 빈 텍스트뷰 생성
        mediView.text = "\uD83D\uDC8A  ${md.getName()}   ${md.getPeriod()}~" // 텍스트 넣기
        mediView.setTextColor(Color.parseColor("#5A5A5A"))
        mediView.textSize = 16.0f
        mediView.setPadding(0, 0, 0, 5)
        mediView.layoutParams = st // 레이아웃 지정
        mediView.id = ViewCompat.generateViewId() // 아이디 랜덤으로 지정

        return mediView
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }
    private fun updateData() {
        CoroutineScope(Dispatchers.IO).launch {
            allMediList = medicineDao.getMediWithCheck(petId)

            withContext(Dispatchers.Main) {
                lay.removeAllViews()
                if(allMediList.isNotEmpty()){

                    for (medi in allMediList!!) {
                        lay.addView(setMedicine(Medi(medi.title ?: "", medi.start ?: "")))
                    }
                }

            }
        }
    }

    fun updatePetId(newPetId: Int) {
        petId = newPetId
        // petId가 변경되었으므로 Medicine 데이터를 다시 가져오기
        Log.i("petId_mediing", petId.toString())

        updateData()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mediView: View = inflater.inflate(R.layout.mediing, container, false)
        //val lay : LinearLayout = mediView.findViewById(R.id.mediLay)
        lay = mediView.findViewById(R.id.mediLay)

        db = AppDatabase.getInstance(requireContext())!!
        medicineDao = db.getMedicineDao()

        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]

        viewModel.mpid.observe(viewLifecycleOwner, Observer { mpid ->
            // mpid가 변경될 때마다 호출되는 콜백
            petId = MyPid.getPid()
            Log.i("petId_medi", petId.toString())

            updateData()
        })

        //val application = requireActivity().application as PidApplication
        petId = MyPid.getPid() //application.petId

        updateData()

//        lay.addView(setMedicine(medi0))
//        lay.addView(setMedicine(medi3))
// 💊
        return mediView
    }
}