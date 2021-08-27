package com.example.testchatapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
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
    var friendListIs = mutableListOf<String>()

    var userConnection = mutableListOf<String>()

    var searchText = ""
    var flag = 0


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            userConnection = it?.getStringArrayList("friendListIs")!!
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user, container, false)

        val requests = view.findViewById<BottomNavigationItemView>(R.id.Request)
        requests.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_requestFragment)
        }

        val addNewFriends = view.findViewById<BottomNavigationItemView>(R.id.Add_Friends)
        addNewFriends.setIconTintList(ColorStateList.valueOf(Color.BLUE))
        addNewFriends.setTextColor(ColorStateList.valueOf(Color.BLUE))

        val friendsList = view.findViewById<BottomNavigationItemView>(R.id.FriendsList)
        friendsList.setOnClickListener {
            findNavController().navigate(R.id.action_userFragment_to_friendFragment)
        }

        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.database
        auth = FirebaseAuth.getInstance()
        userArrayList = arrayListOf()

        val searchView = view.findViewById<SearchView>(R.id.seachBar)

        recyclerView = view.findViewById(R.id.userRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        Log.d("userFragment" , "userConnection $userConnection")

        getUser()
        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText!!
                if(flag == 1){
                    getUser()
                }else if(searchText.length>=3){
                    flag = 1
                    getUser()
                }
             return false
            }

        })
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
                                if(userIs?.usernameR?.lowercase()?.contains(searchText.lowercase()) == true){
                                    Log.d("userFragment" , "${userIs?.userId} , $userConnection , ${userIs?.userId !in userConnection}")
                                    userArrayList.add(userIs!!)
                                }
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