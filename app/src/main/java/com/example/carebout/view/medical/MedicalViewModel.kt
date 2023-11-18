package com.example.carebout.view.medical

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MedicalViewModel : ViewModel() {

    companion object {
        private var instance: MedicalViewModel? = null
        fun getInstance() = instance ?: synchronized(MedicalViewModel::class.java) {
            instance ?: MedicalViewModel().also { instance = it }
        }
    }

    private val selectedPetId = MutableLiveData<Int>()
    val mpid: LiveData<Int> get() = selectedPetId

    fun getSelectedPetId(): LiveData<Int> {
        return selectedPetId
    }

    fun setSelectedPetId(newPetId: Int) {
        Log.i("MedicalViewModel", "Setting petId: $newPetId")
        selectedPetId.value = newPetId
    }
}