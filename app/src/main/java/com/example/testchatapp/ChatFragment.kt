package com.example.testchatapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AlertDialogLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testchatapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.nio.file.FileVisitResult


class ChatFragment : Fragment() {

    private var userId : String? = null

    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseDatabase
    lateinit var tvName: TextView
    lateinit var buttonSendClick : AppCompatImageButton

    lateinit var messageTextView : TextView
    lateinit var recyclerView : RecyclerView

    lateinit var chatList : ArrayList<chatDataClass>


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            userId = it.getString("userId")
            Log.d("chat" , userId!!)
        }
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        return view
    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.database
        auth = FirebaseAuth.getInstance()
        tvName  = view.findViewById(R.id.tvUserName)

        chatList = arrayListOf()

        buttonSendClick = view.findViewById(R.id.btnSendMessage)
        messageTextView = view.findViewById(R.id.etMessage)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        findUserToChat()

        chatList.clear()
        readMessage(auth.currentUser?.uid!! , userId!!)
        buttonSendClick.setOnClickListener {
            val message = messageTextView.text.toString()
            if(message.isEmpty()){
                Toast.makeText(context , "type the message you goose" , Toast.LENGTH_SHORT).show()
            }else{
                sendMessage(auth.currentUser?.uid!! , userId!! , message)
                messageTextView.text = ""
            }

            readMessage(auth.currentUser?.uid!! , userId!!)
        }
    }

    private fun findUserToChat() {
       val ref = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                val user = snapshot.getValue(userToChar::class.java)
                tvName.text = user?.usernameR.toString()
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendMessage(senderId : String , receiverId : String , message : String ){
        val hashMap : HashMap<String , String> = HashMap()
        hashMap.put("senderId" , senderId)
        hashMap.put("receiverId" , receiverId)
        hashMap.put("message" , message)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Chat").push().setValue(hashMap)

    }

    private fun readMessage(senderId : String , receiverId : String){
        val ref  = FirebaseDatabase.getInstance().getReference("Chat")


        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                chatList.clear()
                if(snapshot.exists()){
                    for(chatSnapshot in snapshot.children){
                        val chatUser = chatSnapshot.getValue(chatDataClass::class.java)

                        if(chatUser!!.senderId.equals(senderId) && chatUser!!.receiverId.equals(receiverId) ||
                            chatUser!!.senderId.equals(receiverId) && chatUser!!.receiverId.equals(senderId)   ){
                            chatList.add(chatUser)
                        }
                    }
                    recyclerView.adapter = chatAdapter(chatList)
                    val touchHelper = ItemTouchHelper(messageDeleteCallBack())
                    touchHelper.attachToRecyclerView(recyclerView)

                }
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    inner class messageDeleteCallBack : ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT){
        override fun onMove(
            recyclerView : RecyclerView,
            viewHolder : RecyclerView.ViewHolder,
            target : RecyclerView.ViewHolder
        ) : Boolean {
            TODO("Not yet implemented")
        }

        override fun onSwiped(viewHolder : RecyclerView.ViewHolder, direction : Int) {
            Log.d("chatFragment" , "swiped$viewHolder" )
            Log.d("chatFragment" , "message Position ${viewHolder.adapterPosition}")
            Log.d("chatFragment" , "message Position ${chatList[viewHolder.adapterPosition]}")
            Log.d("chatFragment" , "message Position ${chatList[viewHolder.adapterPosition].message}")
            val builder = AlertDialog.Builder(context!!)
            builder.setCancelable(false)
            builder.setTitle("Delete message ?")
            builder.setMessage("Are you sure you want to Delete")

            builder.setPositiveButton("YES" , DialogInterface.OnClickListener { dialog, which ->

                Toast.makeText(context , "Message Deleted" , Toast.LENGTH_SHORT).show()

                val delMessage = chatList[viewHolder.adapterPosition].message
                chatList.removeAt(viewHolder.adapterPosition)
                recyclerView.adapter?.notifyItemRemoved(viewHolder.adapterPosition)

                val ref = FirebaseDatabase.getInstance().getReference("Chat")
                ref.orderByChild("message").equalTo(delMessage).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot : DataSnapshot) {
                        for(child in snapshot.children){
                            val delRef = FirebaseDatabase.getInstance().getReference("Chat").child(child.key.toString())
                            delRef.removeValue()
                            break
//                            Log.d("chatFragment" , "push id is ${child.key}" )
//                            Log.d("chatFragment", " user ref ${child.ref.toString()}")
//                            Log.d("chatFragment", " user ref ${child.value.toString()}")
//                            break
                        }
                    }

                    override fun onCancelled(error : DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            })
            builder.setNegativeButton("NO" , DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                recyclerView.adapter?.notifyDataSetChanged()
            })
            builder.create().show()
        }

    }

}

data class userToChar(
    val usernameR : String? = ""
)