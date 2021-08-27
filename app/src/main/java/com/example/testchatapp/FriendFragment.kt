package com.example.testchatapp

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView


class FriendFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var friendList: ArrayList<FriendsList>
    var friendListIs = mutableListOf<String>()
    lateinit var connectionList: ArrayList<FriendsDetails>
    lateinit var userProfileLinearLayout: LinearLayout
    lateinit var userProfileImageView: CircleImageView

    lateinit var auth: FirebaseAuth

    var searchText = ""
    var flag = 0
    lateinit var bundle: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friend, container, false)

        auth = Firebase.auth
        userProfileImageView = view.findViewById(R.id.currentUserIv)
        val userReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString())
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //EditText
                val photoUrl = snapshot.child("userProfileImgUrl").value
                if (snapshot.child("userProfileImgUrl").exists()) {
                    if (snapshot.child("userProfileImgUrl").exists()) {
                        Log.d("ProfileFragment", "ImageUrl : $photoUrl")
                        bundle = bundleOf("currentUserImgUrl" to photoUrl)
                        context?.let { Glide.with(it).load(photoUrl).into(userProfileImageView) }
                    } else {
                        context?.let { Glide.with(it).load(R.drawable.image).into(userProfileImageView) }
                    }

                }
                else {
                    context?.let { Glide.with(it).load(R.drawable.image).into(userProfileImageView) }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        userProfileLinearLayout = view.findViewById(R.id.userProfileLl)
        userProfileLinearLayout.setOnClickListener {
            findNavController().navigate(R.id.action_friendFragment_to_profileFragment, bundle)
        }

        val requests = view.findViewById<BottomNavigationItemView>(R.id.Request)
        requests.setOnClickListener {
            findNavController().navigate(R.id.action_friendFragment_to_requestFragment)
        }

        val addNewFriends = view.findViewById<BottomNavigationItemView>(R.id.Add_Friends)
        addNewFriends.setOnClickListener {
            val bundle = bundleOf("friendListIs" to friendListIs)
            findNavController().navigate(R.id.action_friendFragment_to_userFragment3, bundle)
        }

        val friendsList = view.findViewById<BottomNavigationItemView>(R.id.FriendsList)
        friendsList.setIconTintList(ColorStateList.valueOf(Color.BLUE))
        friendsList.setTextColor(ColorStateList.valueOf(Color.BLUE))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendList = arrayListOf()
        connectionList = arrayListOf()
        recyclerView = view.findViewById(R.id.FriendRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val friendSearchViewIs = view.findViewById<SearchView>(R.id.friendSearchView)

        findFriends()
        friendSearchViewIs.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchText = newText!!
                if(flag == 1){
                    findFriends()
                }else if(searchText.length>=2){
                    flag = 1
                    findFriends()
                }
                return false
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
                        friendList.add(friendsId!!)
                        //Log.d("FriendsFragment", "Friends Id is ${friendsId?.FriendId}")
                    }
                    Log.d("FriendsFragment", "Friend list is ${friendListIs}")
                    Log.d("FriendsFragment", "Friends Id is ${friendList}")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val friendRef = FirebaseDatabase.getInstance().getReference("Users")
        friendRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    connectionList.clear()
                    for (friendSnapshot in snapshot.children) {
                        val friendAre = friendSnapshot.getValue(FriendsDetails::class.java)
                        if (friendAre?.userId in friendListIs) {
                            if(friendAre?.usernameR?.lowercase()?.contains(searchText.lowercase()) == true){
                                connectionList.add(friendAre)
                            }
                        }
                    }
                    fun onItemSelected(friendsDetails: FriendsDetails) {
                        val bundle = bundleOf("userId" to friendsDetails.userId)
                        findNavController().navigate(
                            R.id.action_friendFragment_to_chatFragment,
                            bundle
                        )
                    }

                    recyclerView.adapter = FriendAdapter(connectionList, ::onItemSelected)
                    Log.d("FriendList", "conneciton list $connectionList")
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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
