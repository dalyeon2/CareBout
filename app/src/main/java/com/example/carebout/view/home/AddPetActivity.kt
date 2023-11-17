package com.example.carebout.view.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.carebout.databinding.ActivityAddPetBinding
import com.example.carebout.view.home.db.PersonalInfo
import com.example.carebout.view.home.db.PersonalInfoDB
import com.example.carebout.view.home.db.Weight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPetActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPetBinding
    private lateinit var db: PersonalInfoDB
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = PersonalInfoDB.getInstance(this)!!

        setClickListener()
    }

    private fun setClickListener(){
        val galleryVariable: ActivityResultLauncher<Intent> =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data != null) {
                    binding.profileImage.setImageURI(it.data?.data)
                    imageUri = it.data?.data!!
                }
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

            var fileName = ImageUtil().save(this@AddPetActivity, imageUri)

            val pid = db.personalInfoDao().insertInfo(PersonalInfo(
                binding.editName.text.toString(),
                binding.editSex.text.toString(),
                binding.editBirth.text.toString(),
                binding.editBreed.text.toString(),
                binding.editAnimal.text.toString(),
                fileName
            )).toInt()

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
            val currentDate = sdf.format(Date())

            db.weightDao().insertInfo(Weight(
                pid,
                binding.editWeight.text.toString().toFloat(),
                currentDate
            ))

            var empty = EmptyActivity.emptyActivity

            if (!intent.getBooleanExtra("addedPet", true)) {
                empty?.startActivity(Intent(empty, HomeActivity::class.java))
                empty?.finish()
            }

            var home = HomeActivity.homeActivity

            home?.finish()
            home?.startActivity(Intent(this@AddPetActivity, HomeActivity::class.java))
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
        val sex = binding.editSex
        val birth = binding.editBirth
        val weight = binding.editWeight
        val breed = binding.editBreed
        val animal = binding.editAnimal
        val image = binding.profileImage

        if(name.text.isNullOrBlank())
            name.error = ""
        else if(sex.text.isNullOrBlank())
            sex.error = ""
        else if(birth.text.isNullOrBlank())
            birth.error = ""
        else if(weight.text.isNullOrBlank())
            weight.error = ""
        else if(breed.text.isNullOrBlank())
            breed.error = ""
        else if(animal.text.isNullOrBlank())
            animal.error = ""
        else
            return true

        return false
    }
}