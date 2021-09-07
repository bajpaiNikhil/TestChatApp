package com.example.testchatapp.lists.globalUsers

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testchatapp.R
import com.example.testchatapp.data.UserDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val userList : ArrayList<UserDetail>) : RecyclerView.Adapter<UserAdapter.UserHolder>() {
    lateinit var auth : FirebaseAuth


    class UserHolder(view : View) : RecyclerView.ViewHolder(view) {

        val userNameIs = view.findViewById<TextView>(R.id.userName)
        val userImage = view.findViewById<CircleImageView>(R.id.globalUsersImage)
        val userEmail = view.findViewById<TextView>(R.id.temp)
        val addButton = view.findViewById<ImageView>(R.id.addUserButton)

    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : UserHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent  ,false)
        auth = Firebase.auth
        return UserHolder(view)

    }

    override fun onBindViewHolder(holder : UserHolder, position : Int) {
        val currentItem = userList[position]

        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.userId.toString())
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userImageUrl = snapshot.child("userProfileImgUrl").value
                if(snapshot.child("userProfileImgUrl").exists()) {
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
        holder.userEmail.text  = currentItem.emailR


        holder.addButton.setOnClickListener {

            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setCancelable(false)
            builder.setTitle("Add ${currentItem.usernameR}")
            builder.setMessage("Are you sure ?")
            builder.setPositiveButton("Yes") { _, _ ->

                val hashMap: HashMap<String, String> = HashMap()

                hashMap["senderId"] = auth.currentUser?.uid.toString()
                hashMap["receiverId"] = currentItem.userId.toString()
                hashMap["message"] = "SENT YOU A FRIEND REQUEST!!"
                FirebaseDatabase.getInstance().getReference("Request").push().setValue(hashMap)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()

            }
            builder.create().show()
        }
    }


    override fun getItemCount() : Int {
        return userList.size
    }
}
