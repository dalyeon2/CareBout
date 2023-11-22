package com.example.carebout.view.medical.Medicine

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.carebout.R
import com.example.carebout.databinding.ActivityMedicineWriteBinding
import com.example.carebout.view.medical.MedicalViewModel
import com.example.carebout.view.medical.MyPid
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Medicine
import com.example.carebout.view.medical.db.MedicineDao
import java.text.SimpleDateFormat
import java.util.Locale

class MedicineWriteActivity : AppCompatActivity() {

    lateinit var binding: ActivityMedicineWriteBinding
    lateinit var db: AppDatabase
    lateinit var medicineDao: MedicineDao

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0
    private var save: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMedicineWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(applicationContext)!!
        medicineDao = db.getMedicineDao()

        binding.topBarOuter.activityTitle.text = "약 처방"

        // 뒤로가기 버튼 클릭시
        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }

        // 저장 클릭리스너
        binding.topBarOuter.CompleteBtn.setOnClickListener {
            insertMedi()
            if(save != 0){
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

//        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]
//        petId = viewModel.getSelectedPetId().value

        petId = MyPid.getPid()
            //(application as PidApplication).petId
        Log.i("petId_app", petId.toString())

        val editTextM: EditText = findViewById(R.id.editTextM)
        val editTextStartD: EditText = findViewById(R.id.editTextStartD)
        val editTextEndD: EditText = findViewById(R.id.editTextEndD)
        val checkBox: CheckBox = findViewById(R.id.checkBox)
        val editTextMultiLine: TextView = findViewById(R.id.editTextMultiLine)
//        val btn1: Button = findViewById(R.id.button)

        val NowTime = System.currentTimeMillis()
        val DF = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

        val result = DF.format(NowTime)

        editTextStartD.setText(result)

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("check", "true")
                editTextEndD.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
                editTextEndD.isEnabled = false
                editTextEndD.isFocusable = false
            } else {
                editTextEndD.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
                editTextEndD.isEnabled = true
                editTextEndD.isFocusableInTouchMode = true
                editTextEndD.isFocusable = true
            }
        }

//        btn1.setOnClickListener {
//            insertMedi()
//        }
        // 숫자 입력 시 대시 "-" 자동 추가
        setupDateEditText(binding.editTextStartD)
        setupDateEditText(binding.editTextEndD)
        //editTextStartD.addTextChangedListener(dateTextWatcher)
        //editTextEndD.addTextChangedListener(dateTextWatcher)
    }

    private fun insertMedi() {

        val mediTitle = binding.editTextM.text.toString() // 할일 제목
        val mediStartD = binding.editTextStartD.text.toString()
        val mediEndD = binding.editTextEndD.text.toString()
        val medicheckBox = binding.checkBox.isChecked
        val mediEtc = binding.editTextMultiLine.text.toString()

        // Date validation
        if (!isValidDate(mediStartD) || (!mediEndD.isBlank() && !isValidDate(mediEndD))) {
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

        // mediStartD와 currentDate 비교
        if (mediStartD > currentDate) {
            showCustomToast("미래 날짜를 복용 시작일에 입력할 수 없습니다.")
//            Toast.makeText(
//                this,
//                "복용 시작일에 미래 날짜는 입력할 수 없습니다.",
//                Toast.LENGTH_SHORT
//            ).show()
            return
        }

        val Medi = Medicine(null, petId, mediTitle, mediStartD, mediEndD, medicheckBox, mediEtc)

        if (mediTitle.isBlank() || mediStartD.isBlank()) {
            showCustomToast("필수 항목을 채워주세요.")
            //Toast.makeText(this, "항목을 채워주세요", Toast.LENGTH_SHORT).show()
        } else {
            Thread {
                medicineDao.insertMedi(Medi)
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
        val intent = Intent(this, MedicineReadActivity::class.java)
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