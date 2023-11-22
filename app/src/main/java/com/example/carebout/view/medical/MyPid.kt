package com.example.carebout.view.medical

import android.util.Log

class MyPid {
    companion object {
        var petId = 0
        fun getPid(): Int {
            return petId
        }

        fun setPid(pid : Int) {
            Log.i("MyPid", "setPid: $pid")
            petId = pid
        }
    }
}