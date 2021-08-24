package com.example.testchatapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(val userList : ArrayList<UserDetails>) : RecyclerView.Adapter<UserAdapter.UserHolder>() {
    lateinit var auth : FirebaseAuth
    var rId : String? = ""
    class UserHolder(view : View) : RecyclerView.ViewHolder(view) {
        val userNameIs = view.findViewById<TextView>(R.id.userName)
        val userEmail = view.findViewById<TextView>(R.id.temp)
        val addButton = view.findViewById<ImageView>(R.id.addUserButton)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : UserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user , parent  ,false)
        return UserHolder(view)
    }

    override fun onBindViewHolder(holder : UserHolder, position : Int) {
        val currentItem = userList[position]
        rId = currentItem.userId.toString()
        Log.d("UserFragment" , rId!!)
        holder.userNameIs.text = currentItem.usernameR
        holder.userEmail.text  = currentItem.emailR

        holder.addButton.setOnClickListener {
            Log.d("UserFragment" , "button is pressed")
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setCancelable(false)
            builder.setTitle("Add ${currentItem.usernameR}")
            builder.setMessage("u sure ?")
            builder.setPositiveButton("yes" , DialogInterface.OnClickListener { dialog, which ->
                Log.d("UserFragment" , "yes is pressed ${currentItem.userId}")

                auth = FirebaseAuth.getInstance()

                Log.d("UserFragment" , "herr again ${rId!!}")

                val hashMap : HashMap<String , String> = HashMap()

                hashMap.put("senderId" , auth.currentUser?.uid.toString())
                hashMap.put("receiverId" , currentItem.userId.toString() )
                hashMap.put("message" , "SENT YOU A FRIEND REQUEST!")

                val ref = FirebaseDatabase.getInstance().getReference("Request").push().setValue(hashMap)
            })
            builder.setNegativeButton("No" , DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                Log.d("UserFragment" , "No is pressed")
            })
            builder.create().show()
        }
    }

    override fun getItemCount() : Int {
        return userList.size
    }
}