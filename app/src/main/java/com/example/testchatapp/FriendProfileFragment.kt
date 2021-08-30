package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.w3c.dom.Text

class FriendProfileFragment : Fragment() {
    lateinit var userId : String
    lateinit var friendImage : ImageView
    lateinit var friendEmail : TextView
    lateinit var friendUserName : TextView
    lateinit var friendPhonenumber  : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("FriendId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FPF" , userId)
        friendImage = view.findViewById(R.id.friendImageView)
        friendEmail = view.findViewById(R.id.fEmailTV)
        friendUserName = view.findViewById(R.id.fNameTV)
        friendPhonenumber = view.findViewById(R.id.fPhNumberTV)


        val friendProfileRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        friendProfileRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendImageUrl = snapshot.child("userProfileImgUrl").value
                val friendNameIs = snapshot.child("usernameR").value.toString()
                val friendEmailIs = snapshot.child("emailR").value.toString()
                val friendPhNoIs = snapshot.child("phoneNumberR").value.toString()
                if (snapshot.child("userProfileImgUrl").exists()) {
                    Log.d("FPF" , "$friendImageUrl")
                    context?.let { Glide.with(it).load(friendImageUrl).into(friendImage)}
                    friendEmail.text = friendEmailIs
                    friendUserName.text = friendNameIs
                    friendPhonenumber.text = friendPhNoIs
                }
                friendEmail.text = friendEmailIs
                friendUserName.text = friendNameIs
                friendPhonenumber.text = friendPhNoIs


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}