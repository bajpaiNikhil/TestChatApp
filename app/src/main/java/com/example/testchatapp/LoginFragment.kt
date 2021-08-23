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
import com.example.testchatapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginFragment : Fragment() {

    var userName : String    = ""

    lateinit var auth : FirebaseAuth
    lateinit var emailL: EditText
    lateinit var passL: EditText
    var statusFlag : String    = ""

    private var _binding : FragmentLoginBinding?= null
    private val binding get() = _binding



    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {

        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)


        binding?.register?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding?.root
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailL = view.findViewById(R.id.email)
        passL = view.findViewById(R.id.password)


        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if(currentUser != null){
            changeStatus()
            findNavController().navigate(R.id.action_loginFragment_to_userFragment)
        }

        binding?.login?.setOnClickListener {
            val eMail = emailL.text.toString()
            val passWord = passL.text.toString()
            Log.d("login" , userName)

            auth.signInWithEmailAndPassword(eMail,passWord)
                .addOnCompleteListener{
                    if(it.isSuccessful){
                        changeStatus()
                        findNavController().navigate(R.id.action_loginFragment_to_userFragment)
                    }else{
                        Toast.makeText(context, "Something wrong...", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun changeStatus() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object  : ValueEventListener{

            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    for(statusSnapshot in snapshot.children){
                        if(auth.currentUser?.uid.toString() == statusSnapshot.key){
                            ref.child(auth.currentUser?.uid.toString()).child("status").setValue("Active")
                            break
                        }
                    }
                }
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}