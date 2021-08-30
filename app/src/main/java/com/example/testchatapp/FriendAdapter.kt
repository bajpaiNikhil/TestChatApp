package com.example.testchatapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class FriendAdapter(val friendList : ArrayList<FriendsDetails>, val listener : (FriendsDetails)->Unit) : RecyclerView.Adapter<FriendAdapter.FriendHolder>() {
    lateinit var auth: FirebaseAuth

    class FriendHolder(view : View) : RecyclerView.ViewHolder(view) {
        val userNameIs = view.findViewById<TextView>(R.id.FriendUserName)
        val userImage = view.findViewById<CircleImageView>(R.id.FriendUserImage)
        val onlineImage = view.findViewById<ImageView>(R.id.onlineIv)
        val offlineImage = view.findViewById<ImageView>(R.id.offlineIv)
        val statusTextView = view.findViewById<TextView>(R.id.statusTv)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : FriendHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend , parent , false)
        auth = Firebase.auth
        return FriendHolder(view)
    }

    override fun onBindViewHolder(holder : FriendHolder, position : Int) {
        val currentItem = friendList[position]
        holder.onlineImage.visibility = View.INVISIBLE
        holder.offlineImage.visibility = View.INVISIBLE

        holder.userImage.setOnClickListener {
            val bundle = bundleOf("FriendId" to currentItem.userId.toString())
            holder.itemView.findNavController().navigate(R.id.action_friendFragment_to_friendProfileFragment , bundle)
        }

        val userImageRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.userId.toString())
        userImageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userImageUrl = snapshot.child("userProfileImgUrl").value
                if(snapshot.child("userProfileImgUrl").exists()) {
                    Log.d("FriendAdapter", "userProfileImhUrl : $userImageUrl")
                    holder.userImage?.let {
                        Glide.with(it).load(userImageUrl).into(holder.userImage)
                    }
                }
                else{
                    holder.userImage?.let {
                        Glide.with(it).load(R.drawable.image).into(holder.userImage)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        holder.userNameIs.text = currentItem.usernameR

        val languageRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("appLanguage")

        if(currentItem.status == "Active") {
            holder.onlineImage.visibility = View.VISIBLE

            languageRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val language = snapshot.value.toString()
                        if (language == "") {
                            holder.statusTextView.text = "Online"
                        } else if (language == "hi") {
                            holder.statusTextView.text = "ऑनलाइन"
                        } else if (language == "fr") {
                            holder.statusTextView.text = "en ligne"
                        }
                    }
                    else{
                        holder.statusTextView.text = "Online"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        else{
            holder.offlineImage.visibility = View.VISIBLE

            languageRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val language = snapshot.value.toString()
                        if (language == "") {
                            holder.statusTextView.text = "Offline"
                        } else if (language == "hi") {
                            holder.statusTextView.text = "ऑफ़लाइन"
                        } else if (language == "fr") {
                            holder.statusTextView.text = "Hors-ligne"
                        }
                    }
                    else{
                        holder.statusTextView.text = "Offline"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        holder.itemView.setOnClickListener{
            listener(currentItem)
        }
    }

    override fun getItemCount() : Int {
        return friendList.size
    }
}