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
import com.example.carebout.databinding.ActivityMedicineUpdateBinding
import com.example.carebout.view.medical.MedicalViewModel
import com.example.carebout.view.medical.MyPid
import com.example.carebout.view.medical.db.AppDatabase
import com.example.carebout.view.medical.db.Medicine
import com.example.carebout.view.medical.db.MedicineDao
import java.text.SimpleDateFormat
import java.util.Locale

class MedicineUpdateActivity : AppCompatActivity() {

    lateinit var binding : ActivityMedicineUpdateBinding
    lateinit var db : AppDatabase
    lateinit var mediDao: MedicineDao
    var id: Int = 0

    private lateinit var viewModel: MedicalViewModel
    private var petId: Int = 0
    private var save: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMedicineUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topBarOuter.activityTitle.text = "약 처방"

        db = AppDatabase.getInstance(applicationContext)!!
        mediDao = db.getMedicineDao()

//        viewModel = ViewModelProvider(this, SingleViewModelFactory.getInstance())[MedicalViewModel::class.java]
//        petId = viewModel.getSelectedPetId().value

        petId = MyPid.getPid()
            //(application as PidApplication).petId
        Log.i("petId_app", petId.toString())

        val mediText: TextView = findViewById(R.id.editTextM)
        val editTextStartD: TextView = findViewById(R.id.editTextStartD)
        val editTextEndD: TextView = findViewById(R.id.editTextEndD)
        val checkBox: CheckBox = findViewById(R.id.checkBox)
        val editTextMultiLine: TextView = findViewById(R.id.editTextMultiLine)

//        val updateBtn: Button = findViewById(R.id.updateBtn)
//        val deleteBtn: Button = findViewById(R.id.deleteBtn)

        // 수정 페이지로 전달된 아이템 정보를 가져옴
        val mediId = intent.getIntExtra("mediId", -1)
        id = mediId
        Log.i("id", mediId.toString())
        if (mediId != -1) {
            // todoId를 사용하여 데이터베이스에서 해당 아이템 정보를 가져와서 수정 페이지에서 사용할 수 있음
            // 수정 기능을 구현하는 코드 추가
            //넘어온 데이터 변수에 담기
            var uTitle: String? = intent.getStringExtra("uTitle")
            var uStart: String? = intent.getStringExtra("uStart")
            var uEnd: String? = intent.getStringExtra("uEnd")
            var uEtc: String? = intent.getStringExtra("uEtc")
            var uCurrent = intent.getBooleanExtra("uCurrent", false)
            // uCurrent 값이 true이면 체크박스를 체크하고, false이면 체크박스를 체크하지 않습니다.
            if (uCurrent) {
                // 체크박스를 체크
                checkBox.isChecked = true
                Log.i("check", "true")
                editTextEndD.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
                editTextEndD.isEnabled = false
                editTextEndD.isFocusable = false
            } else {
                // 체크박스를 체크하지 않음
                checkBox.isChecked = false
            }

            //화면에 값 적용
            mediText.setText(uTitle)
            editTextStartD.setText(uStart)
            editTextEndD.setText(uEnd)
            editTextMultiLine.setText(uEtc)

            Log.i("in", uTitle.toString())
        }

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

        // 뒤로가기 버튼 클릭시
        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }

        // 저장 클릭리스너
        binding.topBarOuter.CompleteBtn.setOnClickListener {
            updateMedi()
            if(save != 0){
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        // 삭제 클릭리스너
        binding.topBarOuter.DeleteBtn.setOnClickListener {
            deletMedi()
            if(save != 0){
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

//        updateBtn.setOnClickListener{
//            updateMedi()
//        }
//
//        deleteBtn.setOnClickListener {
//            deletMedi()
//        }

        // 숫자 입력 시 대시 "-" 자동 추가
        setupDateEditText(binding.editTextStartD)
        setupDateEditText(binding.editTextEndD)

    }

    private fun updateMedi() {

        val mediTitle = binding.editTextM.text.toString()
            //.TodoEditText.text.toString() // 할일 제목
        val mediStart = binding.editTextStartD.text.toString()
        val mediEnd = binding.editTextEndD.text.toString()
        val medicheckBox = binding.checkBox.isChecked
        //.toIntOrNull() ?: 0 // 숫자로 변환하거나 변환 실패 시 기본값 설정
        val mediEtc = binding.editTextMultiLine.text.toString()

        // Date validation
        if (!isValidDate(mediStart) || (!mediEnd.isBlank() && !isValidDate(mediEnd))) {
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

        // mediStart와 currentDate 비교
        if (mediStart > currentDate) {
            showCustomToast("미래 날짜를 복용 시작일에 입력할 수 없습니다.")
//            Toast.makeText(
//                this,
//                "복용 시작일에 미래 날짜는 입력할 수 없습니다.",
//                Toast.LENGTH_SHORT
//            ).show()
            return
        }

        val Medi = Medicine(id, petId, mediTitle, mediStart, mediEnd, medicheckBox, mediEtc)

        if(mediTitle.isBlank() || mediStart.isBlank()) {
//            Toast.makeText(
//                this, "항목을 채워주세요",
//                Toast.LENGTH_SHORT
//            ).show()
            showCustomToast("필수 항목을 채워주세요.")
        } else {
            Thread {
                mediDao.updateMedi(Medi)
                Log.i("id", Medi.toString())
                runOnUiThread { //아래 작업은 UI 스레드에서 실행해주어야 합니다.
//                    Toast.makeText(
//                        this, "수정되었습니다.",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    save = 1
                    showCustomToast("수정되었습니다.")
                    moveToAnotherPage()
                }
            }.start()
        }

    }

    private fun deletMedi() {

        //Log.i("size", todoList.size.toString())
        // 삭제 기능 구현
        // 해당 ID에 해당하는 할 일 데이터를 삭제하도록 todoDao를 사용합니다.
        Thread {
            val mediToDelete = mediDao.getMediById(id, petId)
            if (mediToDelete != null) {
                mediDao.deleteMedi(mediToDelete)
                runOnUiThread {
                    save = 1
                    showCustomToast("삭제되었습니다.")
                    //Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    moveToAnotherPage()
                }
            }
        }.start()
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
        val intent = Intent(applicationContext, MedicineReadActivity::class.java)
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