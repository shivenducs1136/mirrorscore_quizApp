package com.bitwisor.mirrorquiz.repo

import com.bitwisor.mirrorquiz.data.Quiz
import com.bitwisor.mirrorquiz.reterofitinstance.RetrofitInstance

class Repository {
    suspend fun getQuiz(amount:Int,category:Int,difficulty:String):Quiz{
        val it = RetrofitInstance.api.getQuiz("$amount","$category",difficulty)
        return it
    }
}