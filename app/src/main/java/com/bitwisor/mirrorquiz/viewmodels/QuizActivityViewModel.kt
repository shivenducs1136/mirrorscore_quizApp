package com.bitwisor.mirrorquiz.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitwisor.mirrorquiz.data.Quiz
import com.bitwisor.mirrorquiz.repo.Repository
import kotlinx.coroutines.launch

class QuizActivityViewModel(repo : Repository):ViewModel() {
    var quizList= MutableLiveData<Quiz>()
    var repository = repo
    fun getQuizList(amount:Int , category: Int, difficulty:String){
        Log.e("Called69","sdf")
        viewModelScope.launch {
            val quizdata = repository.getQuiz(amount,category,difficulty)
            quizList.value = quizdata
            Log.e("Mydata",quizdata.toString())
        }
    }


    fun getQuestionA(s:String):String{
        return s
    }
    fun getQuestionB(s:String):String {
        return s
    }
}