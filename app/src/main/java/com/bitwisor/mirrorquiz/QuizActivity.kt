package com.bitwisor.mirrorquiz

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bitwisor.mirrorquiz.data.Result
import com.bitwisor.mirrorquiz.databinding.ActivityQuizBinding
import com.bitwisor.mirrorquiz.repo.Repository
import com.bitwisor.mirrorquiz.viewmodels.HomeViewModel
import com.bitwisor.mirrorquiz.viewmodels.HomeViewModelFactory
import com.bitwisor.mirrorquiz.viewmodels.QuizActivityViewModel
import com.bitwisor.mirrorquiz.viewmodels.QuizActivityViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var binding : ActivityQuizBinding
    var category:String?=null
    var amount :Int = 0
    var difficulty:String?=null
    var score :Int = 0
    lateinit var auth:FirebaseAuth
    lateinit var quizViewModel : QuizActivityViewModel
    lateinit var repo:Repository
    var categoryInt =0
    var questionList = ArrayList<Result>()
    var correct_answer: String? = null
    var correct_answerInt = 0
    val question: String? = null
    val type: String? = null
    var currentPosition = 1
    var selectedOptionPosition = 0
    var options = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = Repository()
        quizViewModel = ViewModelProvider(this, QuizActivityViewModelFactory(repo)).get(QuizActivityViewModel::class.java)
        binding.mprogress.max = 10
        auth = FirebaseAuth.getInstance()
        Glide.with(this).load(auth.currentUser!!.photoUrl).into(binding.circularimgview)
        category = intent.getStringExtra("category")
        amount = intent.getIntExtra("amount",0)
        difficulty = intent.getStringExtra("difficulty")
        score = intent.getIntExtra("score",0)
        categoryInt = intent.getIntExtra("categoryInt",0)
        binding.categorytxt.text = "$category"
        binding.yourscore.text = "$score"
        binding.difficulty.text ="$difficulty"
        binding.mprogress.visibility = View.VISIBLE

        Log.e("DATA1","${amount} ${categoryInt} + ${category} ${difficulty}")
        if(difficulty == "Any Difficulty"){
            difficulty = ""
        }
        quizViewModel.getQuizList(amount,categoryInt,difficulty.toString())

        quizViewModel.quizList.observe(this, androidx.lifecycle.Observer {
            if(it!=null){
                Log.e("IT",it.results.toString())
                Log.e("ITfqwjf",it.toString())
                binding.mprogress.visibility = View.GONE
                questionList = it.results as ArrayList<Result>
                setQuestion()
                amount = questionList.size
                Log.e("OPTIONS",options.toString())

            }
        })
        binding.optionAbox.setOnClickListener (this)
        binding.optionBbox.setOnClickListener (this)
        binding.optionCbox.setOnClickListener (this)
        binding.optionDbox.setOnClickListener (this)
        binding.submitbtn.setOnClickListener (this)

    }
    private fun setQuestion(){
        if(currentPosition>amount){
            Snackbar.make(binding.qwew,"Error while loading the questions", Snackbar.LENGTH_SHORT).show()
            val i = Intent(this,MainActivity::class.java)
                startActivity(i)
                finishAffinity()
        }else{
            val questions = questionList[currentPosition -1]
            defaultOptionsView()
            binding.submitbtn.text = "Submit"
            options = questions.incorrect_answers as ArrayList<String>
            options.add( questions.correct_answer.toString())
            options.shuffle()
            correct_answer = questions.correct_answer
            for(c in 1..4){
                if (options[c-1] == correct_answer){
                    correct_answerInt = c
                }
            }
            if (currentPosition == amount){
                binding.submitbtn.text = "Finish"
            }
            else{
                binding.submitbtn.text = "Go to next question"
            }
            binding.progressBar1.progress = currentPosition
            binding.questiontxt.text =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml( questions.question, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(questions.question)
            }
            binding.optionAtxt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml( options[0], Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml( options[0])
            }
            binding.optionBtxt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml( options[1], Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml( options[1])
            }
            binding.optionCtxt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml( options[2], Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml( options[2])
            }
            binding.optionDtxt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml( options[3], Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml( options[3])
            }
        }


    }
    private fun defaultOptionsView(){
        val opt = ArrayList<MaterialCardView>()
        opt.add(0,binding.optionAbox)
        opt.add(1,binding.optionBbox)
        opt.add(2,binding.optionCbox)
        opt.add(3,binding.optionDbox)

        for(o in opt){
           o.strokeColor = ContextCompat.getColor(this,R.color.gray)
               o.background = ContextCompat.getDrawable(this,R.drawable.defaultwhite)
        }

    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.optionAbox->{
                selectedOptionView(binding.optionAbox,1)
                selectedOptionPosition = 1
            }
            R.id.optionBbox->{
                selectedOptionView(binding.optionBbox,2)
                selectedOptionPosition = 2
            }
            R.id.optionCbox->{
                selectedOptionView(binding.optionCbox,3)
                selectedOptionPosition = 3
            }
            R.id.optionDbox->{
                selectedOptionView(binding.optionDbox,4)
                selectedOptionPosition = 4
            }
            R.id.submitbtn->{

                if(selectedOptionPosition == 0){
                    currentPosition++
                    when{
                        currentPosition <= amount ->{
                            setQuestion()
                        }else->{
                            Toast.makeText(this,"You have completed the quiz",Toast.LENGTH_SHORT).show()
                            val i = Intent(this,MainActivity2::class.java)
                            i.putExtra("score",score)
                            startActivity(i)
                            finish()
                        }
                    }
                }
                else{
                    if(correct_answerInt == selectedOptionPosition){
                        score+=10
                    }
                    if(correct_answerInt != selectedOptionPosition){
                        answer(selectedOptionPosition,R.drawable.redbg)
                    }
                        answer(correct_answerInt,R.drawable.greenbg)
                    if (currentPosition == amount){
                        binding.submitbtn.text ="Finish"
                    }
                    else{
                        binding.submitbtn.text = "Go to next question"
                    }
                    selectedOptionPosition = 0
                }

            }
        }
    }
    private fun selectedOptionView(mv:MaterialCardView,selectedOption:Int){
        defaultOptionsView()
        selectedOptionPosition = selectedOption
        mv.strokeColor = ContextCompat.getColor(this,R.color.main_color)
    }
    private fun answer(ans:Int,drawable:Int){
        when (ans){
            1->{
                binding.optionAbox.background = ContextCompat.getDrawable(this,drawable)
            }
            2->{
                binding.optionBbox.background = ContextCompat.getDrawable(this,drawable)
            }
            3->{
                binding.optionCbox.background = ContextCompat.getDrawable(this,drawable)
            }
            4->{
                binding.optionDbox.background = ContextCompat.getDrawable(this,drawable)
            }
        }
    }

}