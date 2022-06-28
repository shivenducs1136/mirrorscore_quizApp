package com.bitwisor.mirrorquiz.myinterface

import com.bitwisor.mirrorquiz.data.Quiz
import retrofit2.http.GET
import retrofit2.http.Query

interface ContentInterface {
    @GET("api.php")
    suspend fun getQuiz(
        @Query("amount") amount: String,
        @Query("category") category:String,
        @Query("difficulty") difficulty:String
    ):Quiz

}