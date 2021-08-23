package com.example.testchatapp

import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class chatAdapter(val chatList : ArrayList<chatDataClass>) : RecyclerView.Adapter<chatAdapter.ChatHolder>() {

    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    var firebaseUser : FirebaseUser? = null

    class ChatHolder(view : View):RecyclerView.ViewHolder(view) {
        val message = view.findViewById<TextView>(R.id.tvMessage)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : ChatHolder {
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