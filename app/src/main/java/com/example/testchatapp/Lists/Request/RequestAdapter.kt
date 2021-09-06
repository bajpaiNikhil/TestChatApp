package com.example.testchatapp.Lists.Request


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.testchatapp.Chat.chatDataClass
import com.example.testchatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

class RequestAdapter(private val reqList : ArrayList<chatDataClass>) : RecyclerView.Adapter<RequestAdapter.requestHolder>() {
    lateinit var auth: FirebaseAuth
    lateinit var addRequestButton: Button

    class requestHolder(view: View ) : RecyclerView.ViewHolder(view) {
        val senderName  = view.findViewById<TextView>(R.id.senderNameTv)
        val senderImage = view.findViewById<CircleImageView>(R.id.senderIv)
        val requestMessage = view.findViewById<TextView>(R.id.requestMessage)
        val rejectButton = view.findViewById<Button>(R.id.requestRejectButton)
    }

    override fun onCreateViewHolder(parent : ViewGroup, viewType : Int) : requestHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent , false)
        auth = Firebase.auth
        addRequestButton = view.findViewById(R.id.requestAddButton)
        return requestHolder(view)
    }

    override fun onBindViewHolder(holder : requestHolder, position : Int) {
        val currentItem = reqList[position]

        holder.senderImage.setOnClickListener {
            val bundle = bundleOf("FriendId" to currentItem.senderId.toString())
            holder.itemView.findNavController().navigate(R.id.action_requestFragment_to_friendProfileFragment, bundle)
        }

        val senderRef = FirebaseDatabase.getInstance().getReference("Users").child(currentItem.senderId.toString())
        senderRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                    val senderName = snapshot.child("usernameR").value

                    holder.senderName.text = senderName.toString()

                    val senderImageUrl = snapshot.child("userProfileImgUrl").value
                    if(snapshot.child("userProfileImgUrl").exists()) {

                        holder.senderImage?.let {
                            Glide.with(it).load(senderImageUrl).into(holder.senderImage)

                        }
                    }

                    else{
                        holder.senderImage?.let {
                            Glide.with(it).load(R.drawable.image).into(holder.senderImage)
                        }
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val languageRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("appLanguage")
        languageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val language = snapshot.value.toString()
                    if (language == "") {
                        holder.requestMessage.text = currentItem.message.toString()
                    } else if (language == "hi") {
                        holder.requestMessage.text = "आपको फ्रेंड रिक्वेस्ट भेजी है!!"
                    } else if (language == "fr") {
                        holder.requestMessage.text = "vous a envoyé une demande d'ami!!"
                    }
                }
                else{
                    holder.requestMessage.text = currentItem.message.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        addRequestButton.setOnClickListener {

            val ref = FirebaseDatabase.getInstance().getReference("Request")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot : DataSnapshot) {
                    if(snapshot.exists()){
                        for(addSnapshot in snapshot.children){
                            val connectionSnapshot = addSnapshot.getValue(chatDataClass::class.java)

                            if((connectionSnapshot?.senderId == currentItem.senderId) && (connectionSnapshot?.receiverId == currentItem.receiverId)){

                                val requestDeleteRef = FirebaseDatabase.getInstance().getReference("Request").child(addSnapshot.key.toString())
                                requestDeleteRef.addValueEventListener(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.child("receiverId").value != currentItem.receiverId.toString()){
                                            //Add to friend to receiver
                                            val hashMapUserOne : HashMap<String , String> = HashMap()
                                            hashMapUserOne["FriendId"] = connectionSnapshot?.senderId.toString()
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                .child(connectionSnapshot?.receiverId.toString()).child("Friends").push().setValue(hashMapUserOne)

                                            //Add to friend to sender
                                            val hashMapUserTwo : HashMap<String , String> = HashMap()
                                            hashMapUserTwo["FriendId"] = connectionSnapshot?.receiverId.toString()
                                            FirebaseDatabase.getInstance().getReference("Users")
                                                .child(connectionSnapshot?.senderId.toString()).child("Friends").push().setValue(hashMapUserTwo)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                            //Delete request in db
                            if(connectionSnapshot?.senderId == currentItem.senderId &&
                                connectionSnapshot?.receiverId == currentItem.receiverId ){
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

            holder.itemView.findNavController().navigate(R.id.action_requestFragment_to_friendFragment)
        }

        holder.rejectButton.setOnClickListener {


            val ref = FirebaseDatabase.getInstance().getReference("Request")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot : DataSnapshot) {
                    if(snapshot.exists()){
                        for(rejectRequest in snapshot.children){
                            val delRequest = rejectRequest.getValue(chatDataClass::class.java)

                            if(delRequest?.senderId == currentItem.senderId &&
                                delRequest?.receiverId == currentItem.receiverId ){

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