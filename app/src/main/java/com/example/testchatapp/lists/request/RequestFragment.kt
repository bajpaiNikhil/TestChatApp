package com.example.testchatapp.lists.request

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testchatapp.data.FriendsList
import com.example.testchatapp.data.ChatDataClass

import com.example.testchatapp.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    private lateinit var requestList : ArrayList<ChatDataClass>
    private lateinit var recyclerView : RecyclerView
    private var friendListIs = mutableListOf<String>()
    private var searchText = ""
    private var flag = 0

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("RestrictedApi", "WrongConstant")
    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        val requests = view.findViewById<BottomNavigationItemView>(R.id.Request)
        requests.setIconTintList(ColorStateList.valueOf(Color.BLUE))
        requests.setTextColor(ColorStateList.valueOf(Color.BLUE))



        val addNewFriends = view.findViewById<BottomNavigationItemView>(R.id.Add_Friends)
        addNewFriends.setOnClickListener {
            findFriends()
            val bundle = bundleOf("friendListIs" to friendListIs)
            findNavController().navigate(R.id.action_requestFragment_to_userFragment,bundle)
        }

        val friendsList = view.findViewById<BottomNavigationItemView>(R.id.FriendsList)
        friendsList.setOnClickListener {
            findNavController().navigate(R.id.action_requestFragment_to_friendFragment)
        }

        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.requestRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val friendSearchViewIs = view.findViewById<SearchView>(R.id.requestSearchView)

        auth = FirebaseAuth.getInstance()

        requestList = arrayListOf()

        requestView()

        friendSearchViewIs.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText!!
                if(flag == 1){
                    requestView()
                }else if(searchText.length>=2){
                    flag = 1
                    requestView()
                }
                return false
            }

        })

    }

    private fun requestView() {
        val ref  = FirebaseDatabase.getInstance().getReference("Request")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    requestList.clear()
                    for(requestSnapshot in snapshot.children){
                        val requestIs = requestSnapshot.getValue(ChatDataClass::class.java)

                        if(requestIs?.receiverId == auth.currentUser?.uid.toString()){
                            if(requestIs.receiverId?.lowercase()?.contains(searchText.lowercase()) == true){
                                requestList.add(requestIs)
                            }
                            else{
                                requestList.add(requestIs)
                            }
                        }

                    }

                    recyclerView.adapter = RequestAdapter(requestList)

                }

            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun findFriends() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("Friends")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (friendSnapshot in snapshot.children) {
                        val friendsId = friendSnapshot.getValue(FriendsList::class.java)
                        friendListIs.add(friendsId?.FriendId.toString())
                        //Log.d("FriendsFragment", "Friends Id is ${friendsId?.FriendId}")
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}