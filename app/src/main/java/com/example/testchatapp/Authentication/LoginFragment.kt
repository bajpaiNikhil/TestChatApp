package com.example.testchatapp.Authentication
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.testchatapp.R

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*

class LoginFragment : Fragment() {
    private var userName : String    = ""
    private lateinit var auth : FirebaseAuth
    private lateinit var emailL: EditText
    private lateinit var passL: EditText
    private lateinit var loginButton : Button
    private lateinit var registerTextView: TextView
    private var currentLanguage = "en"

    private lateinit var locale: Locale
    private lateinit var loginLayout: RelativeLayout
    private lateinit var profile : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locale = Locale(currentLanguage)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)

        auth = Firebase.auth

        return inflater.inflate(R.layout.fragment_login, container, false)
    }
    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailL = view.findViewById(R.id.email)
        passL = view.findViewById(R.id.password)
        loginButton = view.findViewById(R.id.loginB)
        registerTextView = view.findViewById(R.id.registerTv)

        profile = view.findViewById(R.id.profile)
        loginLayout = view.findViewById(R.id.loginLayout)

        if(auth.currentUser != null){
            findNavController().navigate(R.id.action_loginFragment_to_friendFragment)
            FirebaseDatabase.getInstance().getReference("Users")
                .child(auth.currentUser?.uid.toString()).child("status")
                .setValue("Active")
        }

        registerTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginButton.setOnClickListener {
            val eMail = emailL.text.toString()
            val passWord = passL.text.toString()
            if(eMail.isEmpty() || passWord.isEmpty()) {
                Toast.makeText(context, getString(R.string.please_enter_all_fields), Toast.LENGTH_LONG).show()
            }
            else{
                Log.d("login", userName)
                auth.signInWithEmailAndPassword(eMail, passWord)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child(auth.currentUser?.uid.toString()).child("status")
                                .setValue("Active")
                            findNavController().navigate(R.id.action_loginFragment_to_friendFragment)
                        } else {
                            Toast.makeText(context, getString(R.string.somethingWrong), Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }
}

