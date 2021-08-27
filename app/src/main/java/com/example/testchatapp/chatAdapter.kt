package com.example.testchatapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class chatAdapter(val chatList : ArrayList<chatDataClass>) : RecyclerView.Adapter<chatAdapter.ChatHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    lateinit var auth: FirebaseAuth
    var userImageUrl =""
    var senderImageUrl =""

    var firebaseUser : FirebaseUser? = null

    class ChatHolder(view : View):RecyclerView.ViewHolder(view) {
        val message = view.findViewById<TextView>(R.id.tvMessage)
        val userImageView = view.findViewById<CircleImageView>(R.id.userImage)
        val friendImageView = view.findViewById<CircleImageView>(R.id.friendImage)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ChatHolder {
        auth = Firebase.auth
            if(viewType == MESSAGE_TYPE_RIGHT){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_right , parent ,false)
            return ChatHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left , parent ,false)
            return ChatHolder(view)
        }
    }

    override fun onBindViewHolder(holder : ChatHolder, position : Int) {
        val currentItem  = chatList[position]
        val userImageRef = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
        userImageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userImageUrl = snapshot.child("userProfileImgUrl").value.toString()
                holder.userImageView?.let { Glide.with(it).load(userImageUrl).into(holder.userImageView) }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val friendImageRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.senderId.toString())
        friendImageRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                senderImageUrl = snapshot.child("userProfileImgUrl").value.toString()
                holder.friendImageView?.let { Glide.with(it).load(senderImageUrl).into(holder.friendImageView) }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        holder.message.text = currentItem.message
    }

    override fun getItemCount() : Int {
        return chatList.size
    }

    override fun getItemViewType(position : Int) : Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if(chatList[position].senderId == firebaseUser!!.uid){
            return MESSAGE_TYPE_RIGHT
        }else{
            return MESSAGE_TYPE_LEFT
        }
    }
}