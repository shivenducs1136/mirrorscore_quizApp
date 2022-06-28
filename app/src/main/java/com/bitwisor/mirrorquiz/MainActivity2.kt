package com.bitwisor.mirrorquiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bitwisor.mirrorquiz.databinding.ActivityMain2Binding
import com.bitwisor.mirrorquiz.model.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity2 : AppCompatActivity() {
    lateinit var binding:ActivityMain2Binding
    lateinit var auth:FirebaseAuth
    lateinit var database: FirebaseDatabase
    var score = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        Glide.with(this).load(auth.currentUser!!.photoUrl).into(binding.profile)
        binding.name.text = auth.currentUser!!.displayName
        score = intent.getIntExtra("score",0)
        binding.scoretxt.text = "Your Score : ${score}"
        database = FirebaseDatabase.getInstance()
        val usr: User = User(auth.currentUser!!.uid,auth.currentUser!!.displayName.toString(),auth.currentUser!!.photoUrl.toString(),score)
        database.reference.child("profiles").child(auth.currentUser!!.uid).setValue(usr).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(this,"Congratulations your score has been updated!!", Toast.LENGTH_SHORT).show()
                val i = Intent(this,MainActivity::class.java)
                binding.endbtn.setOnClickListener {
                    startActivity(i)
                    finishAffinity()
                }
            }else{
                Toast.makeText(this,"Error in database", Toast.LENGTH_SHORT).show()
                binding.endbtn.text = "Continue"
                val i = Intent(this,MainActivity::class.java)
                binding.endbtn.setOnClickListener {
                    startActivity(i)
                    finishAffinity()
                }
            }
        }

    }
}