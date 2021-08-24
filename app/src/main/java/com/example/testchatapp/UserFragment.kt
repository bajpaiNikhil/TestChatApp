package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testchatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class UserFragment : Fragment() {

    lateinit var auth :  FirebaseAuth
    lateinit var db   : FirebaseDatabase
    lateinit var recyclerView : RecyclerView
    lateinit var userArrayList : ArrayList<UserDetails>

    var userConnection = mutableListOf<String>()

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            userConnection = it?.getStringArrayList("friendListIs")!!
        }
    }
    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.database
        auth = FirebaseAuth.getInstance()
        userArrayList = arrayListOf()
        recyclerView = view.findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        Log.d("userFragment" , "userConnection $userConnection")

        getUser()
    }

    private fun getUser() {

        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    userArrayList.clear()
                    for(userSnapShot in snapshot.children){
                        val userIs = userSnapShot.getValue(UserDetails::class.java)
                        if(userIs?.userId != auth.currentUser?.uid){
                            if(userIs?.userId !in userConnection){
                                Log.d("userFragment" , "${userIs?.userId} , $userConnection , ${userIs?.userId !in userConnection}")
                                userArrayList.add(userIs!!)
                            }
                        }
                    }
                    Log.d("userFragment" , "userArrayList $userArrayList")
                    recyclerView.adapter = UserAdapter(userArrayList)
                }
            }

            override fun onCancelled(error : DatabaseError) {

            }

        })
    }
}

data class UserDetails(
    val userId : String? = "",
    val emailR : String? = "",
    val usernameR : String? = "",
    var status : String = ""
)