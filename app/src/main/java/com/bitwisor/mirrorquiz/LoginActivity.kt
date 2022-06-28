package com.bitwisor.mirrorquiz

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bitwisor.mirrorquiz.databinding.ActivityLoginBinding
import com.bitwisor.mirrorquiz.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.R
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var googleSignInClient:GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var database:FirebaseDatabase
    val RC_SIGN_IN = 11
    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser!=null){
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finishAffinity()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createRequest()
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        binding.LoginBtn.setOnClickListener {
            signIn()
        }
    }
    private fun createRequest() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.bitwisor.mirrorquiz.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this,"Sign In Failed Please Try Again ", Toast.LENGTH_SHORT).show()
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    val usr: User = User(user!!.uid,user.displayName.toString(),user.photoUrl.toString(),0)
                    database.reference.child("profiles").child(user.uid).setValue(usr).addOnCompleteListener {
                        if (it.isSuccessful){
                            val i = Intent(this,MainActivity::class.java)
                            startActivity(i)
                            finishAffinity()
                        }else{
                            Toast.makeText(this,"Error in database", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }
}