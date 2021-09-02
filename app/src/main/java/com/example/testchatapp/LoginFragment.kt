package com.example.testchatapp
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.bundleOf
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

    lateinit var locale: Locale
    lateinit var loginLayout: RelativeLayout
    lateinit var profile : ImageView

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
        }

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
}

