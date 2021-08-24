package com.example.testchatapp


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestAdapter(val reqList : ArrayList<chatDataClass>) : RecyclerView.Adapter<RequestAdapter.requestHolder>() {
    class requestHolder(view: View ) : RecyclerView.ViewHolder(view) {
        val senderName  = view.findViewById<TextView>(R.id.senderNameTv)
        val requestMessage = view.findViewById<TextView>(R.id.requestMessage)
        val addButton = view.findViewById<Button>(R.id.requestAddButton)
        val rejectButton = view.findViewById<Button>(R.id.requestRejectButton)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : requestHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent , false)
        return requestHolder(view)
    }

    override fun onBindViewHolder(holder : requestHolder, position : Int) {
        val currentItem = reqList[position]

        val senderRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.senderId.toString())
        senderRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val senderName = snapshot.child("usernameR").value
                    Log.d("requestAdapter" , "username : ${senderName.toString()}")
                    holder.senderName.text = senderName.toString()

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        holder.requestMessage.text = currentItem.message.toString()
        holder.addButton.setOnClickListener {
            Toast.makeText(holder.itemView.context , "Add button is pressed" , Toast.LENGTH_SHORT).show()
            Log.d("requestAdapter" , "Add button is clicked")

            val ref = FirebaseDatabase.getInstance().getReference("Request")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot : DataSnapshot) {
                    if(snapshot.exists()){
                        for(addSnapshot in snapshot.children){
                            val connectionSnapshot = addSnapshot.getValue(chatDataClass::class.java)

                            //Add to friend to receiver
                            val hashMapUserOne : HashMap<String , String> = HashMap()
                            hashMapUserOne["FriendId"] = connectionSnapshot?.senderId.toString()
                            val connectionRefOne = FirebaseDatabase.getInstance().getReference("Users")
                                .child(connectionSnapshot?.receiverId.toString()).child("Friends").push().setValue(hashMapUserOne)

                            //Add to friend to sender
                            val hashMapUserTwo : HashMap<String , String> = HashMap()
                            hashMapUserTwo["FriendId"] = connectionSnapshot?.receiverId.toString()
                            val connectionRefTwo = FirebaseDatabase.getInstance().getReference("Users")
                                .child(connectionSnapshot?.senderId.toString()).child("Friends").push().setValue(hashMapUserTwo)

                            //Delete request in db
                            if(connectionSnapshot?.senderId == currentItem.senderId &&
                                connectionSnapshot?.receiverId == currentItem.receiverId ){
                                Log.d("requestAdapter" , "push id ${addSnapshot.key}")
                                val requestDeleteRef = FirebaseDatabase.getInstance().getReference("Request").child(addSnapshot.key.toString())
                                requestDeleteRef.removeValue()
                            }

                        }
                    }
                }

                override fun onCancelled(error : DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        }
        holder.rejectButton.setOnClickListener {
            Toast.makeText(holder.itemView.context , "Reject button is pressed" , Toast.LENGTH_SHORT).show()

            Log.d("requestAdapter" , "Reject button is clicked")
            val ref = FirebaseDatabase.getInstance().getReference("Request")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot : DataSnapshot) {
                    if(snapshot.exists()){
                        for(rejectRequest in snapshot.children){
                            val delRequest = rejectRequest.getValue(chatDataClass::class.java)
                            Log.d("requestAdapter" , "push id 11  ${rejectRequest.key}")
                            if(delRequest?.senderId == currentItem.senderId &&
                                delRequest?.receiverId == currentItem.receiverId ){
                                Log.d("requestAdapter" , "push id ${rejectRequest.key}")
                                val requestDeleteRef = FirebaseDatabase.getInstance().getReference("Request").child(rejectRequest.key.toString())
                                requestDeleteRef.removeValue()
                            }
                        }
                    }
                }

                override fun onCancelled(error : DatabaseError) {

                }

            })
        }
    }

    override fun getItemCount() : Int {
        return reqList.size
    }
}