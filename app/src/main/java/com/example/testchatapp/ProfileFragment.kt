package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
//import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    // For Edit Text Display
    lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: TextView
    lateinit var imageView: ImageView
    lateinit var userProfileUpdateFloatingActionButton: FloatingActionButton
    lateinit var userNameEditFloatingActionButton: FloatingActionButton
    lateinit var userNameUpdateFloatingActionButton: FloatingActionButton


    lateinit var logOut: Button

    // For FireBase
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For Database*
        auth = Firebase.auth

        logOut = view.findViewById(R.id.logOut)

        // For Edit Text Display
        userNameEditText = view.findViewById(R.id.userNameEt)
        passwordEditText = view.findViewById(R.id.passTv)
        imageView = view.findViewById(R.id.imageView)
        userProfileUpdateFloatingActionButton = view.findViewById(R.id.userImageFab)
        userNameEditFloatingActionButton = view.findViewById(R.id.editUserNameFab)
        userNameUpdateFloatingActionButton = view.findViewById(R.id.updateUserNameFab)

        userNameUpdateFloatingActionButton.visibility = View.INVISIBLE

        viewProfile()

        logOut.setOnClickListener {
            val lastUser = auth.currentUser?.uid
            auth.signOut()
            FirebaseDatabase.getInstance().getReference("Users").child(lastUser.toString())
                .child("status").setValue("InActive")
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)

        }

        userNameEditFloatingActionButton.setOnClickListener {
            userNameEditFloatingActionButton.visibility = View.INVISIBLE
            userNameUpdateFloatingActionButton.visibility = View.VISIBLE

            userNameEditText.isEnabled = true
        }

        userNameUpdateFloatingActionButton.setOnClickListener {
            userNameUpdateFloatingActionButton.visibility = View.INVISIBLE
            userNameEditFloatingActionButton.visibility = View.VISIBLE

            val newUserName = userNameEditText.text.toString()
            FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString()).child("usernameR").setValue(newUserName)
        }
    }

    private fun viewProfile() {
        val userReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //EditText
                val userName = snapshot.child("usernameR").value.toString()
//                val photoUrl = snapshot.child("profileImageUrl").value.toString()
//                if(photoUrl.isEmpty()) {
//                    }
//                else{
//                    Log.d("ProfileFragment", "ImageUrl : $photoUrl")
//                    context?.let {
//                        Glide.with(it).load(photoUrl).into(imageView)
//                    }
//                }
                userNameEditText.isEnabled = false
                userNameEditText.setText(userName)

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
