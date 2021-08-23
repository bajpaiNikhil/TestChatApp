package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FriendFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var FriendList : ArrayList<FirendsDetails>
    lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FriendList = arrayListOf()
        auth = FirebaseAuth.getInstance()
        recyclerView = view.findViewById(R.id.FriendRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        findFriends()
    }

    private fun findFriends() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("Friends")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot : DataSnapshot) {
                if (snapshot.exists()) {
                    for (friendSnapshot in snapshot.children) {
                        val friendsId = friendSnapshot.getValue(FriendsList::class.java)
                        Log.d("FriendsFragment", "Friends Id is ${friendsId?.FriendId}")
                        val connectionId = friendsId?.FriendId.toString()
                        //findConnction(connectionId)
                        val refIs =
                            FirebaseDatabase.getInstance().getReference("Users").child(connectionId)
                        refIs.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot : DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (connectionSnapshot in snapshot.children) {
                                        val friendIs =
                                            connectionSnapshot.getValue(FirendsDetails::class.java)
                                        FriendList.add(friendIs!!)
                                        recyclerView.adapter = FriendAdater(FriendList)
                                    }

                                }

                            }

                            override fun onCancelled(error : DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })

                    }

                }

            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun findConnction(connectionId : String) {
//        Log.d("FriendsFragment" , "Friends Id here is  $connectionId")
//
//        val ref = FirebaseDatabase.getInstance().getReference("Users").child(connectionId)
//        ref.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot : DataSnapshot) {
//                if(snapshot.exists()){
//                    for(connectionSnapshot in snapshot.children){
//                        val friendIs = connectionSnapshot.getValue(UserDetails::class.java)
//                        FriendList.add(friendIs!!)
//                        recyclerView.adapter = FriendAdater(FriendList)
//                    }
//                }
//            }
//
//            override fun onCancelled(error : DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//
//        })
//
//    }

    }
}
data class FriendsList(
    val FriendId : String? = ""
)
data class FirendsDetails(
    val userId : String? = "",
    val emailR : String? = "",
    val usernameR : String? = "",
    var status : String = ""
)
