package com.bitwisor.mirrorquiz.reterofitinstance

import com.bitwisor.mirrorquiz.model.Constants.Companion.BaseUrl
import com.bitwisor.mirrorquiz.myinterface.ContentInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ContentInterface by lazy {
        retrofit.create(ContentInterface::class.java)
    }

}