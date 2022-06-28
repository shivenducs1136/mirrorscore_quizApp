package com.bitwisor.mirrorquiz.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.bitwisor.mirrorquiz.LoginActivity
import com.bitwisor.mirrorquiz.QuizActivity
import com.bitwisor.mirrorquiz.R
import com.bitwisor.mirrorquiz.databinding.FragmentHomeBinding
import com.bitwisor.mirrorquiz.model.User
import com.bitwisor.mirrorquiz.repo.Repository
import com.bitwisor.mirrorquiz.viewmodels.HomeViewModel
import com.bitwisor.mirrorquiz.viewmodels.HomeViewModelFactory
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment : Fragment() {

    lateinit var binding:FragmentHomeBinding
    lateinit var viewmodel:HomeViewModel
    lateinit var repo:Repository
    lateinit var usr:User
    var score = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repo= Repository()
        viewmodel = ViewModelProvider(this,HomeViewModelFactory(repo)).get(HomeViewModel::class.java)

        viewmodel.getUserData()
        binding.progress.visibility = View.VISIBLE
        viewmodel.userdetails.observe(viewLifecycleOwner){
            if (it != null){
                binding.progress.visibility = View.GONE
                binding.nametxt.text = it.name
                Glide.with(requireActivity())
                    .load(it.profilepic).into(binding.profileimg)
                score = it.score
                binding.scoretxt.text = "Your Last score : $score"

            }
        }

        binding.profileimg.setOnClickListener {
            showDialog("Are you sure want to Logout ?")
        }
        binding.quizbtn.setOnClickListener {
        toggleBottomSheet()
        }
    }
    private fun showDialog(title: String) {
        val dialog = Dialog(requireActivity())
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_layout)
        val body = dialog.findViewById(R.id.body) as TextView
        body.text = title
        val yesBtn = dialog.findViewById(R.id.yesbtn) as TextView
        val noBtn = dialog.findViewById(R.id.nobtn) as TextView
        yesBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var  i = Intent(requireActivity(),LoginActivity::class.java)
            startActivity(i)
            requireActivity().finishAffinity()
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }
    fun toggleBottomSheet(){
        val btnsheet = layoutInflater.inflate(R.layout.fragment_create_quiz, null)
        val startbtn = btnsheet.findViewById<Button>(R.id.startbtn)
        val categorySpinner = btnsheet.findViewById<Spinner>(R.id.categorySpinner)
        val difficultySpinner = btnsheet.findViewById<Spinner>(R.id.difficultySpinner)
        var amount = 10
        var selectedCategoryint= 0
        var selectedCategorystring =""
        var selectedDifficulty = ""
        viewmodel.getCategories()
        viewmodel.categories.observe(viewLifecycleOwner){
            if (it!=null){
                categorySpinner.adapter = ArrayAdapter<String>(requireContext(),R.layout.listitem,it)
                categorySpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedCategorystring = it[p2]
                        if(it[p2] == "Any Category"){
                            selectedCategoryint = 0

                        }
                        else{
                            selectedCategoryint = p2+8
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        selectedCategoryint = 0
                        selectedCategorystring = ""
                    }

                }

            }
        }
        viewmodel.getDifficulty()
        viewmodel.difficulty.observe(viewLifecycleOwner){
            if (it!=null){
                difficultySpinner.adapter = ArrayAdapter<String>(requireContext(),R.layout.listitem,it)
                difficultySpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        selectedDifficulty = it[p2]
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        selectedDifficulty =""
                    }

                }
            }
        }


        startbtn.setOnClickListener {
            val i = Intent(requireActivity(),QuizActivity::class.java)
            i.putExtra("category",selectedCategorystring)
            i.putExtra("categoryInt",selectedCategoryint)
            i.putExtra("difficulty",selectedDifficulty)
            i.putExtra("amount",amount)
            i.putExtra("score",score)
            startActivity(i)
        }
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(btnsheet)
        dialog.show()
    }

}