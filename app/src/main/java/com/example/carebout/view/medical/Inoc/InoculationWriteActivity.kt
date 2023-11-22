package com.example.carebout.view.medical.Inoc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import com.example.carebout.R
import com.example.carebout.databinding.ActivityInoculationWriteBinding
import com.example.carebout.view.home.db.PersonalInfoDao
import com.example.carebout.view.medical.MedicalViewModel
import com.example.carebout.view.medical.MyPid
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Inoculation
import com.example.carebout.view.medical.db.InoculationDao
import java.text.SimpleDateFormat
import java.util.Locale

class InoculationWriteActivity : AppCompatActivity() {

    lateinit var binding: ActivityInoculationWriteBinding
    lateinit var db: AppDatabase
    lateinit var inocDao: InoculationDao
    lateinit var personalInfoDao: PersonalInfoDao

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0
    private var save: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInoculationWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBarOuter.activityTitle.text = "접종/구충"

        // 뒤로가기 버튼 클릭시
        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }

        // 저장 클릭리스너
        binding.topBarOuter.CompleteBtn.setOnClickListener {
            insertInoc()
            if(save != 0){
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        db = AppDatabase.getInstance(applicationContext)!!
        inocDao = db.getInocDao()
        personalInfoDao = db.personalInfoDao()

//        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]
//        petId = viewModel.getSelectedPetId().value

        petId = MyPid.getPid()
            //(application as PidApplication).petId
        Log.i("petId_app", petId.toString())

        updatePetTag()

        val editTextDate: EditText = findViewById(R.id.editTextDate)
        val editTextDue: EditText = findViewById(R.id.editTextDue)
        val editTextH: EditText = findViewById(R.id.editTextH)
        val editTextMultiLine: TextView = findViewById(R.id.editTextMultiLine)
//        val btn1: Button = findViewById(R.id.button)

//        val tagDHPPL: ToggleButton = findViewById(R.id.toggleButton1)
//        val tagC: ToggleButton = findViewById(R.id.toggleButton2)
//        val tagKC: ToggleButton = findViewById(R.id.toggleButton3)
//        val tagCVRP: ToggleButton = findViewById(R.id.toggleButton4)
//        val tagFL: ToggleButton = findViewById(R.id.toggleButton5)
//        val tagFID: ToggleButton = findViewById(R.id.toggleButton6)
//        val tagR: ToggleButton = findViewById(R.id.toggleButton7)
//        val tagH: ToggleButton = findViewById(R.id.toggleButton8)

        val tagDHPPL = binding.toggleButton1
        val tagC = binding.toggleButton2
        val tagKC = binding.toggleButton3
        val tagCVRP = binding.toggleButton4
        val tagFL = binding.toggleButton5
        val tagFID = binding.toggleButton6
        val tagR = binding.toggleButton7
        val tagH = binding.toggleButton8

        val NowTime = System.currentTimeMillis()
        val DF = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

        val result = DF.format(NowTime)
        editTextDate.setText(result)


//        btn1.setOnClickListener {
//            insertInoc()
//        }

//        tag1.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                if (tag2.isChecked) {
//                    tag2.isChecked = false
//                }
//            }
//        }

        tagDHPPL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagC, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagKC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagCVRP, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagCVRP.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagFL, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagFL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFID, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagFID.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagR, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagR.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagH)
                otherTags.forEach { it.isChecked = false }
            }
        }

        tagH.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val otherTags = listOf(tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagR)
                otherTags.forEach { it.isChecked = false }
            }
        }

        // 숫자 입력 시 대시 "-" 자동 추가
        setupDateEditText(binding.editTextDate)
        setupDateEditText(binding.editTextDue)
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

    private fun insertInoc() {
        val inocDate = binding.editTextDate.text.toString()
        val inocDue = binding.editTextDue.text.toString()
        val inocH = binding.editTextH.text.toString()
        val inocEtc = binding.editTextMultiLine.text.toString()

        val tagDHPPL = binding.toggleButton1.isChecked
        val tagC = binding.toggleButton2.isChecked
        val tagKC = binding.toggleButton3.isChecked
        val tagCVRP = binding.toggleButton4.isChecked
        val tagFL = binding.toggleButton5.isChecked
        val tagFID = binding.toggleButton6.isChecked
        val tagR = binding.toggleButton7.isChecked
        val tagH = binding.toggleButton8.isChecked

        // Date validation
        if (!isValidDate(inocDate) || (!inocDue.isBlank() && !isValidDate(inocDue))) {
            showCustomToast("유효하지 않은 날짜 형식입니다. 항목을 다시 확인해주세요.")
//            Toast.makeText(
//                this,
//                "유효하지 않은 날짜 형식입니다. 항목을 다시 확인해주세요.",
//                Toast.LENGTH_SHORT
//            ).show()
            return
        }

        // 현재 날짜를 가져옴
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())

        // inocDate와 currentDate 비교
        if (inocDate > currentDate) {
            showCustomToast("미래 날짜를 접종/구충일에 입력할 수 없습니다.")
//            Toast.makeText(
//                this,
//                "접종/구충 날짜에 미래 날짜는 입력할 수 없습니다.",
//                Toast.LENGTH_SHORT
//            ).show()
            return
        }
        val Inoc = Inoculation(null, petId, tagDHPPL, tagC, tagKC, tagCVRP, tagFL, tagFID, tagR, tagH, inocDate, inocDue, inocH, inocEtc)

        if ((!tagDHPPL && !tagC && !tagKC && !tagCVRP && !tagFL && !tagFID && !tagR && !tagH) || inocDate.isBlank()) {
            showCustomToast("필수 항목을 채워주세요.")
//            Toast.makeText(this, "필수 항목을 채워주세요", Toast.LENGTH_SHORT).show()
        } else {
            Thread {
                inocDao.insertInoculation(Inoc)
                runOnUiThread {
                    save = 1
                    showCustomToast("추가되었습니다.")
                    moveToAnotherPage()
                }
            }.start()
        }
    }
    private var currentToast: Toast? = null
    private fun showCustomToast(message: String) {
        currentToast?.cancel()

        val inflater = layoutInflater
        val layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_layout))

        val text = layout.findViewById<TextView>(R.id.custom_toast_text)
        text.text = message

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout

//        val toastDurationInMilliSeconds: Long = 3000
//        toast.duration =
//            if (toastDurationInMilliSeconds > Toast.LENGTH_LONG) Toast.LENGTH_LONG else Toast.LENGTH_SHORT

        toast.setGravity(Gravity.BOTTOM, 0, 200)

        currentToast = toast

        toast.show()
    }
    private fun moveToAnotherPage() {
        val intent = Intent(this, InoculationReadActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Date validation function
    private fun isValidDate(dateString: String): Boolean {
        if (dateString.length != 10) {
            return false
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(dateString)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun setupDateEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            @SuppressLint("SuspiciousIndentation")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if the input exceeds 10 characters and remove extra characters
                if (s?.length ?: 0 > 10) {
                    val truncatedText = s?.subSequence(0, 10)
                    editText.setText(truncatedText)
                    editText.setSelection(10)
                    return
                }

                // Add dashes to the date input when necessary
                if (count == 1 && (start == 4 || start == 7)) {
                    s?.let {
                        val updatedText = StringBuilder(it)
                        //  if (start == 4|| start == 7) {
                        updatedText.insert(start, '-')
                        //  }
//                        else {
//                            updatedText.insert(start - 1, '-')
//                        }
                        editText.setText(updatedText)
                        editText.setSelection(updatedText.length) // 커서를 항상 마지막으로 이동
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}