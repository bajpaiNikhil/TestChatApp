package com.example.testchatapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendAdater(val FriendList : ArrayList<FirendsDetails> , val listner : (FirendsDetails)->Unit) : RecyclerView.Adapter<FriendAdater.FriendHolder>() {
    class FriendHolder(view : View) : RecyclerView.ViewHolder(view) {
        val userNameIs = view.findViewById<TextView>(R.id.FriendUserName)
        val userEmail  = view.findViewById<TextView>(R.id.FriendEmail)
        val statusFlag = view.findViewById<TextView>(R.id.FriendTvStatus)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : FriendHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend , parent , false)
        return FriendHolder(view)
    }

    override fun onBindViewHolder(holder : FriendHolder, position : Int) {
        val currentItem = FriendList[position]
        holder.userNameIs.text = currentItem.usernameR
        holder.userEmail.text  = currentItem.emailR
        holder.statusFlag.text = currentItem.status
        holder.itemView.setOnClickListener{
            listner(currentItem)
        }
    }

    override fun getItemCount() : Int {
        return FriendList.size
    }
}