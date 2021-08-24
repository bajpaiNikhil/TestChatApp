package com.example.testchatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendAdapter(val friendList : ArrayList<FriendsDetails>, val listener : (FriendsDetails)->Unit) : RecyclerView.Adapter<FriendAdapter.FriendHolder>() {

    class FriendHolder(view : View) : RecyclerView.ViewHolder(view) {
        val userNameIs = view.findViewById<TextView>(R.id.FriendUserName)
        val userEmail  = view.findViewById<TextView>(R.id.FriendEmail)
        val onlineImage = view.findViewById<ImageView>(R.id.onlineIv)
        val offlineImage = view.findViewById<ImageView>(R.id.offlineIv)

//        val statusFlag = view.findViewById<TextView>(R.id.FriendTvStatus)

    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : FriendHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend , parent , false)
        return FriendHolder(view)
    }

    override fun onBindViewHolder(holder : FriendHolder, position : Int) {
        val currentItem = friendList[position]
        holder.onlineImage.visibility = View.INVISIBLE
        holder.offlineImage.visibility = View.INVISIBLE

        holder.userNameIs.text = currentItem.usernameR
        holder.userEmail.text  = currentItem.emailR

        if(currentItem.status == "Active") {
            holder.onlineImage.visibility = View.VISIBLE
        }
        else{
            holder.offlineImage.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener{
            listener(currentItem)
        }
    }

    override fun getItemCount() : Int {
        return friendList.size
    }
}