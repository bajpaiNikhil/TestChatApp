package com.example.testchatapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private var userId : String? = null

    lateinit var auth : FirebaseAuth
    lateinit var db : FirebaseDatabase
    lateinit var tvName: TextView
    lateinit var buttonSendClick : AppCompatImageButton
    lateinit var backImageView: ImageView
    lateinit var messageTextView : TextView
    lateinit var recyclerView : RecyclerView
    lateinit var onlineImageView: ImageView
    lateinit var offlineImageView: ImageView
    lateinit var statusTextView: TextView

    lateinit var menuPress : ImageView

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
        tvName = view.findViewById(R.id.tvUserName)

        chatList = arrayListOf()

        buttonSendClick = view.findViewById(R.id.btnSendMessage)
        messageTextView = view.findViewById(R.id.etMessage)
        backImageView = view.findViewById(R.id.imgBack)
        onlineImageView = view.findViewById(R.id.onlineIv2)
        offlineImageView = view.findViewById(R.id.offlineIv2)
        statusTextView = view.findViewById(R.id.statusTv2)

        menuPress = view.findViewById(R.id.menuShow)

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        onlineImageView.visibility = View.INVISIBLE
        offlineImageView.visibility = View.INVISIBLE
        statusTextView.visibility = View.INVISIBLE

        menuPress.setOnClickListener {
            showPopUpMenu()
        }

        findUserToChat()

        chatList.clear()
        readMessage(auth.currentUser?.uid!!, userId!!)
        buttonSendClick.setOnClickListener {
            val message = messageTextView.text.toString()
            if (message.isEmpty()) {
                Toast.makeText(context, "type the message you goose", Toast.LENGTH_SHORT).show()
            } else {
                sendMessage(auth.currentUser?.uid!!, userId!!, message)
                messageTextView.text = ""
            }

            readMessage(auth.currentUser?.uid!!, userId!!)

        }

        backImageView.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_friendFragment)
        }
        menuPress.setOnClickListener {
            showPopUpMenu()
        }
    }

    private fun showPopUpMenu() {
        val pMenu = PopupMenu(context , menuPress)
        pMenu.menu.add("Font Size")
        pMenu.menu.add("Font Colour")
        pMenu.menu.add("Font Style")

        pMenu.setOnMenuItemClickListener {
            when(it.title){
                "Font"  -> {
                    Log.d("chatFrag" ,"${it.title} found ")
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose the Font Size")

                    // add a list
                    val fontSizeArray = arrayOf("Small", "Medium", "Large", "Extra Large", "Default")
                    builder.setItems(fontSizeArray) { dialog, which ->

                        when (which) {
                            0 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(14)

                                Toast.makeText(context,"item Clicked ${fontSizeArray[which]}" , Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(16)
                                Toast.makeText(context,"item Clicked ${fontSizeArray[which]}" , Toast.LENGTH_SHORT).show()
                            }
                            2 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(20)
                                Toast.makeText(context,"item Clicked ${fontSizeArray[which]}" , Toast.LENGTH_SHORT).show()
                            }
                            3 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(24)
                                Toast.makeText(context,"item Clicked ${fontSizeArray[which]}" , Toast.LENGTH_SHORT).show()
                            }
                            4 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(18)
                                Log.d("chatFrag" ,"$which found ")
                                Toast.makeText(context,"item Clicked ${fontSizeArray[which]}" , Toast.LENGTH_SHORT).show()
                            }

                        }
                    }

                    // create and show the alert dialog
                    val dialog = builder.create()
                    dialog.show()
                    Toast.makeText(context , "Font . " , Toast.LENGTH_LONG).show()
                }
                "Font Colour" -> {
                    Log.d("chatFrag" , "${it.title} found")
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose Font Colour")

                    // add a list
                    val fontColorArray = arrayOf("Crimson", "Coral", "DimGrey", "Snow", "Default")
                    builder.setItems(fontColorArray) { dialog, which ->
                        when (which) {
                            0 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#DC143C")
                                Toast.makeText(context,"item Clicked ${fontColorArray[which]}" , Toast.LENGTH_SHORT).show() }
                            1 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#FF7F50")
                                Toast.makeText(context,"item Clicked ${fontColorArray[which]}" , Toast.LENGTH_SHORT).show() }
                            2 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#696969")
                                Toast.makeText(context,"item Clicked ${fontColorArray[which]}" , Toast.LENGTH_SHORT).show() }
                            3 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#FFFAFA")
                                Toast.makeText(context,"item Clicked ${fontColorArray[which]}" , Toast.LENGTH_SHORT).show() }
                            4 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#36454F")
                                Toast.makeText(context,"item Clicked ${fontColorArray[which]}" , Toast.LENGTH_SHORT).show() }
                        }
                    }

                    // create and show the alert dialog
                    val dialog = builder.create()
                    dialog.show()
                    Toast.makeText(context , "Language . " , Toast.LENGTH_LONG).show()
                }
                "Font Style" -> {
                    Log.d("chatFrag" , "${it.title} found")
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose Font Colour")

                    // add a list
                    val fontStyleArray = arrayOf("cursive", "casual", "serif-monospace", "sans-serif-smallcaps", "serif")
                    builder.setItems(fontStyleArray) { dialog, which ->
                        when (which) {
                            0 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("cursive")
                                Toast.makeText(context,"item Clicked ${fontStyleArray[which]}" , Toast.LENGTH_SHORT).show() }
                            1 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("casual")
                                Toast.makeText(context,"item Clicked ${fontStyleArray[which]}" , Toast.LENGTH_SHORT).show() }
                            2 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("serif-monospace")
                                Toast.makeText(context,"item Clicked ${fontStyleArray[which]}" , Toast.LENGTH_SHORT).show() }
                            3 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("sans-serif-smallcaps")
                                Toast.makeText(context,"item Clicked ${fontStyleArray[which]}" , Toast.LENGTH_SHORT).show() }
                            4 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("serif")
                                Toast.makeText(context,"item Clicked ${fontStyleArray[which]}" , Toast.LENGTH_SHORT).show() }
                        }
                    }

                    // create and show the alert dialog
                    val dialog = builder.create()
                    dialog.show()
                    Toast.makeText(context , "Language . " , Toast.LENGTH_LONG).show()
                }
                else     -> {}
            }
            true
        }

        pMenu.show()
    }


    private fun findUserToChat() {
       val ref = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                val user = snapshot.getValue(userToChar::class.java)
                tvName.text = user?.usernameR.toString()
                val userStatus = snapshot.child("status").value
                if(userStatus == "Active"){
                    onlineImageView.visibility = View.VISIBLE
                    statusTextView.visibility = View.VISIBLE
                    statusTextView.text = "Online"
                }
                else{
                    offlineImageView.visibility = View.VISIBLE
                    statusTextView.visibility = View.VISIBLE
                    statusTextView.text = "User's Offline"
                }
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

                        if(chatUser!!.senderId.equals(senderId) && chatUser.receiverId.equals(receiverId) ||
                            chatUser.senderId.equals(receiverId) && chatUser.receiverId.equals(senderId)   ){
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
    val usernameR : String? = "",
    val status : String ? =""
)
