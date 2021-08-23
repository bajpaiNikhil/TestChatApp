package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.testchatapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    lateinit var emailR: EditText
    lateinit var passwordR: EditText
    lateinit var usernameR: EditText
    lateinit var phoneNumberR: EditText

    lateinit var auth : FirebaseAuth
    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding
    lateinit var db : FirebaseDatabase


    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailR = view.findViewById(R.id.email)
        passwordR  =view.findViewById(R.id.password)
        usernameR = view.findViewById(R.id.name)
        phoneNumberR = view.findViewById(R.id.phone)

        auth = FirebaseAuth.getInstance()
        db = Firebase.database

        binding?.register?.setOnClickListener {
            val userName = usernameR.text.toString()
            val eMail = emailR.text.toString()
            val passWord = passwordR.text.toString()
            val phoneNumber = phoneNumberR.text.toString()
            Log.d("Register",userName)


            auth.createUserWithEmailAndPassword(eMail,passWord)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        addUserInDb()
                        Toast.makeText(context , "Registration Complete" , Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                    }else{
                        Toast.makeText(context , "Registration InComplete" , Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    private fun addUserInDb(){
        val userName = usernameR.text.toString()
        val email = "${emailR.text}"
        val phoneNumber = phoneNumberR.text.toString()

        val UserObj = UserDetail(auth.currentUser?.uid.toString(), email,userName,phoneNumber , "InActive")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.currentUser?.uid.toString()).setValue(UserObj)

    }

}

class UserDetail() {
    var userId : String = "" // primary key
    var emailR : String = ""
    var usernameR : String = ""
    var phoneNumberR : String = ""
    var status : String = ""
    constructor(
        userId : String,
        emailR : String,
        usernameR : String,
        phoneNumberR : String,
        status : String
    ) : this(){
        this.userId             = userId
        this.emailR             = emailR
        this.usernameR          = usernameR
        this.phoneNumberR       = phoneNumberR
        this.status             = status
    }
}