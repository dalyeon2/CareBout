package com.example.carebout.view.home

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.carebout.databinding.ActivityAddPetBinding
import com.example.carebout.view.home.db.PersonalInfo
import com.example.carebout.view.home.db.Weight
import com.example.carebout.view.medical.db.AppDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPetBinding
    private lateinit var db: AppDatabase
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!

        setClickListener()
    }

    private fun getSex(): String {
        if (binding.maleRadio.isChecked)
            return "male"
        return "female"
    }

    private fun getAnimal(): String {
        if (binding.dogRadio.isChecked)
            return "dog"
        return "cat"
    }

    private fun setClickListener(){
        val galleryVariable: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data != null) {
                    binding.profileImage.setImageURI(it.data?.data)
                    imageUri = it.data?.data!!
                }
            }

        // 날짜 입력 editText 클릭 시 캘린더 뜨도록
        binding.editBirth.setOnClickListener {
            var calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)
            this@AddPetActivity.let { it ->
                DatePickerDialog(it, { _, year, month, day ->
                    run {
                        binding.editBirth.setText(String.format("%04d-%02d-%02d", year, month + 1, day)
                        )
                    }
                }, year, month, day)
            }?.show()
        }

        // 뒤로가기 버튼 클릭시
        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }

        // 등록 버튼 클릭시
        binding.topBarOuter.CompleteBtn.setOnClickListener {
            //입력된 데이터의 유효성 검사
            if(!isValid())
                return@setOnClickListener

            var fileName = ""

            if(::imageUri.isInitialized)
                fileName = ImageUtil().save(this@AddPetActivity, imageUri)


            val pid = db.personalInfoDao().insertInfo(PersonalInfo(
                binding.editName.text.toString(),
                getSex(),
                binding.editBirth.text.toString(),
                binding.editBreed.text.toString(),
                getAnimal(),
                fileName
            )).toInt()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            val currentDate = sdf.format(Date())

            db.weightDao().insertInfo(Weight(
                pid,
                binding.editWeight.text.toString().toFloat(),
                currentDate
            ))

            finish()
        }

        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            galleryVariable.launch(intent)

        }
    }

    fun isValid(): Boolean{
        val name = binding.editName
        val birth = binding.editBirth
        val breed = binding.editBreed
        val weight = binding.editWeight

        if(name.text.isNullOrBlank())
            name.error = ""
        else if(birth.text.isNullOrBlank())
            birth.error = ""
        else if(breed.text.isNullOrBlank())
            breed.error = "모르면 '모름'이라고 입력 해주세요"
        else if(weight.text.isNullOrBlank())
            weight.error = ""
        else {
            return true
        }

        return false
    }
}