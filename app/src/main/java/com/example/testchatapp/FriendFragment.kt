package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


class FriendFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var FriendList : ArrayList<FriendsList>
    var friendListIs = mutableListOf<String>()
    lateinit var connectionList : ArrayList<FriendsDetails>

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
        connectionList = arrayListOf()
        auth = Firebase.auth
        recyclerView = view.findViewById(R.id.FriendRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        findFriends()

        setHasOptionsMenu(true)

    }

    private fun findFriends() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("Friends")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot : DataSnapshot) {
                if (snapshot.exists()) {
                    for (friendSnapshot in snapshot.children) {
                        val friendsId = friendSnapshot.getValue(FriendsList::class.java)
                        friendListIs.add(friendsId?.FriendId.toString())
                        FriendList.add(friendsId!!)
                        //Log.d("FriendsFragment", "Friends Id is ${friendsId?.FriendId}")
                    }
                    Log.d("FriendsFragment", "Friend list is ${friendListIs}")
                    Log.d("FriendsFragment", "Friends Id is ${FriendList}")
                }

            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val friendRef = FirebaseDatabase.getInstance().getReference("Users")
        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot : DataSnapshot) {

                if (snapshot.exists()) {
                    connectionList.clear()
                    for (friendSnapshot in snapshot.children) {
                        val friendAre = friendSnapshot.getValue(FriendsDetails::class.java)
                        if (friendAre?.userId in friendListIs) {
                            connectionList.add(friendAre!!)
//                            Log.d("FriendList" , "conneciton list $connctionList")
                        }
                    }
                    fun onItemSelected(friendsDetails : FriendsDetails) {
                        val bundle = bundleOf("userId" to friendsDetails.userId)
                        findNavController().navigate(
                            R.id.action_friendFragment_to_chatFragment,
                            bundle
                        )
                    }

                    recyclerView.adapter = FriendAdater(connectionList, ::onItemSelected)
                    Log.d("FriendList", "conneciton list $connectionList")
                }

            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu : Menu, inflater : MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        when (item.itemId) {
            R.id.Logout -> {

                FirebaseDatabase.getInstance().getReference("Users")
                    .child(auth.currentUser?.uid.toString()).child("status").setValue("InActive")
                auth.signOut()
                findNavController().navigate(R.id.action_friendFragment_to_loginFragment)

            }

            R.id.Request -> {
                findNavController().navigate(R.id.action_friendFragment_to_requestFragment)
            }
            R.id.Friends -> {
                findNavController().navigate(R.id.action_friendFragment_to_userFragment3)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeStatus() {
        Log.d("login"  , "${auth.signOut()}")
        auth.signOut()
        findNavController().navigate(R.id.action_friendFragment_to_loginFragment)
    }
}

data class FriendsList(
    val FriendId : String? = ""
)
data class FriendsDetails(
    val userId : String? = "",
    val emailR : String? = "",
    val usernameR : String? = "",
    var status : String = ""
)
