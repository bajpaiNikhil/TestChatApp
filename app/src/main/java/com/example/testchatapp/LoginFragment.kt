package com.example.testchatapp
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.*

class LoginFragment : Fragment() {
    var userName : String    = ""
    lateinit var auth : FirebaseAuth
    lateinit var emailL: EditText
    lateinit var passL: EditText
    lateinit var loginButton : Button
    lateinit var registerTextView: TextView
    private var currentLanguage = "en"
    private var currentLang: String? = null
    lateinit var locale: Locale

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        emailL = view.findViewById(R.id.email)
        passL = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.loginB)
        registerTextView = view.findViewById(R.id.registerTv)

        auth = Firebase.auth

        val currentUser = auth.currentUser
        Log.d("login" , currentUser.toString())

        if(currentUser != null){
            Log.d("login" , "Value is $currentUser")
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString()).child("status").setValue("Active")
            val appLanguageRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
            appLanguageRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("appLanguage").exists()) {
                        currentLanguage = snapshot.child("appLanguage").value.toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            findNavController().navigate(R.id.action_loginFragment_to_friendFragment)
        }

        setLocale(currentLanguage)

        registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginButton.setOnClickListener {
            val eMail = emailL.text.toString()
            val passWord = passL.text.toString()
            Log.d("login" , userName)
            auth.signInWithEmailAndPassword(eMail,passWord)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString()).child("status").setValue("Active")
                        findNavController().navigate(R.id.action_loginFragment_to_friendFragment)
                    }else{
                        Toast.makeText(context, "Something wrong...", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun setLocale(localeName: String) {
        if (localeName != currentLanguage) {
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)
            val refresh = Intent(
                context,
                MainActivity::class.java
            )
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)
        } else {
//            Toast.makeText(
//                context, "Language, , already, , selected)!", Toast.LENGTH_SHORT).show()
        }
    }
}