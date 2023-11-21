package com.example.carebout.view.home

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.carebout.R
import com.example.carebout.databinding.ActivityEditPetBinding
import com.example.carebout.view.home.db.PersonalInfo
import com.example.carebout.view.medical.db.AppDatabase
import java.util.Calendar

class EditPetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPetBinding

    private lateinit var db: AppDatabase

    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)!!
        val pid = intent.getIntExtra("pid", 0)
        
        // 액티비티에 표시할 정보 그리기
        setTextAll(pid)
        binding.topBarOuter.activityTitle.text = "정보 수정"

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
            this@EditPetActivity.let { it ->
                DatePickerDialog(it, { _, year, month, day ->
                    run {
                        binding.editBirth.setText(String.format("%04d-%02d-%02d", year, month + 1, day)
                        )
                    }
                }, year, month, day)
            }?.show()
        }

        //뒤로가기 버튼
        binding.topBarOuter.backToActivity.setOnClickListener {
            finish()
        }

        //
        binding.topBarOuter.CompleteBtn.setOnClickListener {
            //입력된 데이터의 유효성 검사
            if(!isValid())
                return@setOnClickListener

            var fileName = ""

            if(::imageUri.isInitialized)
                fileName = ImageUtil().save(this@EditPetActivity, imageUri)

            val p = PersonalInfo(
                name = binding.editName.text.toString(),
                sex = getSex(),
                birth = binding.editBirth.text.toString(),
                breed = binding.editBreed.text.toString(),
                animal = getAnimal(),
                image = fileName
            )
            p.pid = pid
            db.personalInfoDao().updateInfo(p)

            val home = HomeActivity()
            home?.finish()
            home?.startActivity(Intent(this@EditPetActivity, HomeActivity::class.java))
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

    private fun setTextAll(pid: Int) {
        val info = db.personalInfoDao().getInfoById(pid)
        val weight = db.weightDao().getWeightById(pid).last().weight
        
        if (info?.image == "") {
            if (info?.animal == "dog")
                binding.profileImage.setImageResource(R.drawable.temp_dog)
            else
                binding.profileImage.setImageResource(R.drawable.temp_cat)
        }else {
            binding.profileImage.setImageURI(ImageUtil().get(this@EditPetActivity, info?.image!!))
        }

        binding.editName.setText(info?.name)
        binding.editBirth.setText(info?.birth)
        binding.editWeight.setText(weight.toString())
        binding.editWeight.isEnabled = false

        if (info?.sex == "male")
            binding.maleRadio.isChecked = true
        else
            binding.femaleRadio.isChecked = true

        binding.editBreed.setText(info?.breed)

        if (info?.animal == "dog")
            binding.dogRadio.isChecked = true
        else
            binding.catRadio.isChecked = true
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

    fun isValid(): Boolean{
        val name = binding.editName
        val birth = binding.editBirth
        val breed = binding.editBreed

        if(name.text.isNullOrBlank())
            name.error = ""
        else if(birth.text.isNullOrBlank())
            birth.error = ""
        else if(breed.text.isNullOrBlank())
            breed.error = "모르면 '모름'이라고 입력 해주세요"
        else
            return true

        return false
    }
}