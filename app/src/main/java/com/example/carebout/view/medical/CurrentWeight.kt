package com.example.carebout.view.medical

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.carebout.R
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.home.db.WeightDao
import com.example.carebout.view.medical.db.AppDatabase

var weight: Float = 0.0f
var paperCupFeed: Float = 0.0f
var paperCup: Float = 80.0f
var feedGram: Float = 0.0f

fun getAmount(weight: Float) : Float {
    feedGram = weight*20
    paperCupFeed = feedGram / paperCup

    return paperCupFeed
}

class CurrentWeight : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var weightDao: WeightDao
    private lateinit var personalInfoDao: PersonalInfoDao

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0

    private lateinit var feedAmount: TextView
    private lateinit var cWeight: TextView

    override fun onResume() {
        super.onResume()
        updateData()
    }

    fun updatePetId(newPetId: Int) {
        petId = newPetId
        // petId가 변경되었으므로 Medicine 데이터를 다시 가져오기
        Log.i("petId_weight", petId.toString())

        updateData()
    }

    private fun updateData() {
        val petList = weightDao.getWeightById(petId)
        val allList = personalInfoDao.getAllInfo()

        // 펫 목록이 비어있지 않을 때
        if (petList.isNotEmpty() && allList.isNotEmpty()) {
            for (pet in petList) {
                weight = pet.weight
            }
        }else{
            weight = 0f
        }

        feedAmount.text = "종이컵\n%.1f".format(getAmount(weight))
        cWeight.text = weight.toString()

        Log.i("weight", weight.toString())
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val weightView: View = inflater.inflate(R.layout.currentweight, container, false)
        feedAmount = weightView.findViewById(R.id.feedAmount)
        cWeight = weightView.findViewById(R.id.weight)

        db = AppDatabase.getInstance(requireContext())!!
        weightDao = db.weightDao()
        personalInfoDao = db.personalInfoDao()

        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]

        viewModel.mpid.observe(viewLifecycleOwner, Observer { mpid ->
            // mpid가 변경될 때마다 호출되는 콜백
            petId = MyPid.getPid()
            Log.i("petId_weight", petId.toString())

            updateData()
        })

        // val application = requireActivity().application as PidApplication
        petId = MyPid.getPid() //application.petId

        updateData()

        return weightView
    }
}
