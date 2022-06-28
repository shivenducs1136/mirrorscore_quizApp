package com.bitwisor.mirrorquiz.viewmodels

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitwisor.mirrorquiz.model.User
import com.bitwisor.mirrorquiz.repo.Repository
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class HomeViewModel(repository: Repository):ViewModel() {

    lateinit var auth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var usr: User
    val userdetails = MutableLiveData<User>()
    val categories = MutableLiveData<Array<String>>()
    val difficulty = MutableLiveData<Array<String>>()


    fun getUserData(){
        viewModelScope.launch {
            auth = FirebaseAuth.getInstance()
            firebaseDatabase = FirebaseDatabase.getInstance()
            var curntuser = auth.currentUser
//        binding.progress.visibility = View.VISIBLE
            firebaseDatabase.reference.child("profiles")
                .child(curntuser!!.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        usr = snapshot.getValue(User::class.java)!!
                        userdetails.value = usr
                    }

                    override fun onCancelled(error: DatabaseError) {
//                    Snackbar.make(view, "Database Error", Snackbar.LENGTH_SHORT).show()
                    }

                })
        }
    }
    fun getCategories(){
        viewModelScope.launch {
            val catag = arrayOf(
                "Any Category"
                ,"General Knowledge"
                ,"Entertainment: Books"
                ,"Entertainment: Film"
                ,"Entertainment: Music"
                ,"Entertainment: Musicals & Theatres"
                ,"Entertainment: Television"
                ,"Entertainment: Video Games"
                ,"Entertainment: Board Games"
                ,"Science & Nature"
                ,"Science: Computers"
                ,"Science: Mathematics"
                ,"Mythology"
                ,"Sports"
                ,"Geography"
                ,"History"
                ,"Politics"
                ,"Art"
                ,"Celebrities"
                ,"Animals"
                ,"Vehicles"
                ,"Entertainment: Comics"
                ,"Science: Gadgets"
                ,"Entertainment: Japanese Anime & Manga"
                ,"Entertainment: Cartoon & Animations"
            )
            categories.value = catag
        }
    }
    fun getDifficulty(){
        viewModelScope.launch {
            val diffi = arrayOf("Any Difficulty","easy","medium","hard")
            difficulty.value = diffi
        }
    }

}