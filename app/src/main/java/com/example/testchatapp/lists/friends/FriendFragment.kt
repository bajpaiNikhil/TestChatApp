package com.example.testchatapp.lists.friends

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testchatapp.data.FriendsList
import com.example.testchatapp.data.UserDetail
import com.example.testchatapp.R
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList


class FriendFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var friendList: ArrayList<FriendsList>
    private var friendListIs = mutableListOf<String>()
    private lateinit var connectionList: ArrayList<UserDetail>

    private lateinit var userProfileLinearLayout: LinearLayout
    private lateinit var userProfileImageView: CircleImageView
    private lateinit var auth: FirebaseAuth

    private var searchText = ""
    private var currentLanguage = ""
    private var flag = 0
    private var bundle: Bundle? = null
    private lateinit var profileTv: TextView
    private lateinit var locale: Locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userProfileImageView = view.findViewById(R.id.currentUserIv)
        userProfileLinearLayout = view.findViewById(R.id.userProfileLl)
        recyclerView = view.findViewById(R.id.FriendRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        profileTv = view.findViewById(R.id.profileTextView)

        val friendSearchViewIs = view.findViewById<SearchView>(R.id.friendSearchView)

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


        val userReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString())
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                //EditText
                val photoUrl = snapshot.child("userProfileImgUrl").value

                if (snapshot.child("userProfileImgUrl").exists()) {
                    if (snapshot.child("userProfileImgUrl").exists()) {

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


        findFriends()

        friendList = arrayListOf()
        connectionList = arrayListOf()
        profileTv.text = getString(R.string.profile)

        userProfileLinearLayout.setOnClickListener {
            findNavController().navigate(
                R.id.action_friendFragment_to_profileFragment,
                bundle
            )
        }

        val requests = view.findViewById<BottomNavigationItemView>(R.id.Request)
        requests.setTitle(getString(R.string.request))
        requests.setOnClickListener {
            findNavController().navigate(R.id.action_friendFragment_to_requestFragment)
        }

        val addNewFriends = view.findViewById<BottomNavigationItemView>(R.id.Add_Friends)
        addNewFriends.setTitle(getString(R.string.find_friends))
        addNewFriends.setOnClickListener {
            val bundle = bundleOf("friendListIs" to friendListIs)
            findNavController().navigate(
                R.id.action_friendFragment_to_userFragment3,
                bundle
            )
        }

        val friendsList = view.findViewById<BottomNavigationItemView>(R.id.FriendsList)
        friendsList.setTitle(getString(R.string.friends_list))
        friendsList.setIconTintList(ColorStateList.valueOf(Color.BLUE))
        friendsList.setTextColor(ColorStateList.valueOf(Color.BLUE))

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
                        val friendAre = friendSnapshot.getValue(UserDetail::class.java)
                        if (friendAre?.userId in friendListIs) {
                            if(friendAre?.usernameR?.lowercase()?.contains(searchText.lowercase()) == true){
                                connectionList.add(friendAre)
                            }
                        }
                    }
                    fun onItemSelected(friendsDetails: UserDetail) {
                        val bundle = bundleOf("userId" to friendsDetails.userId)
                        findNavController().navigate(
                            R.id.action_friendFragment_to_chatFragment,
                            bundle
                        )
                    }
                    recyclerView.adapter = FriendAdapter(connectionList, ::onItemSelected)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

//
//data class FriendsList(
//    val FriendId : String? = ""
//)
//data class FriendsDetails(
//    val userId : String? = "",
//    val emailR : String? = "",
//    val usernameR : String? = "",
//    var status : String = ""
//)
