package com.example.testchatapp.Chat

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
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testchatapp.data.ChatDataClass
import com.example.testchatapp.R
import com.example.testchatapp.chat.ChatAdapter
import com.example.testchatapp.data.UserDetail
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

    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseDatabase
    private lateinit var tvName: TextView
    private lateinit var buttonSendClick : AppCompatImageButton
    private lateinit var backImageView: ImageView
    private lateinit var messageTextView : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var onlineImageView: ImageView
    private lateinit var offlineImageView: ImageView
    private lateinit var statusTextView: TextView
    lateinit var menuPress : ImageView

    lateinit var chatList : ArrayList<ChatDataClass>


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
                Toast.makeText(context, getString(R.string.Type_the_message), Toast.LENGTH_SHORT).show()
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

            //showPopUpMenu()

            findNavController().navigate(R.id.action_chatFragment_to_bottomSheetMenuFragment)

        }
    }

    private fun showPopUpMenu() {
        val pMenu = PopupMenu(context , menuPress)
        pMenu.menu.add(getString(R.string.Font_Size))
        pMenu.menu.add(getString(R.string.Font_Colour))
        pMenu.menu.add(getString(R.string.Font_Style))

        pMenu.setOnMenuItemClickListener {
            when(it.title){

                getString(R.string.Font_Size)  -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(getString(R.string.Choose_the_Font_Size))

                    // add a list
                    val fontSizeArray = arrayOf(getString(R.string.Small), getString(R.string.Medium), getString(
                        R.string.Large
                    ), getString(R.string.Extra_Large), getString(R.string.Default))
                    builder.setItems(fontSizeArray) { dialog, which ->

                        when (which) {
                            0 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(14)

                            }
                            1 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(16)
                            }
                            2 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(20)
                            }
                            3 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(24)
                            }
                            4 -> {
                                val sizePicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontSize").setValue(18)
                                Log.d("chatFrag" ,"$which found ")

                            }

                        }
                    }

                    // create and show the alert dialog
                    val dialog = builder.create()
                    dialog.show()
                }
                getString(R.string.Font_Colour) -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Choose Font Colour")

                    // add a list
                    val fontColorArray = arrayOf(getString(R.string.Crimson), getString(R.string.Coral), getString(
                        R.string.DimGrey
                    ), getString(R.string.Snow), getString(R.string.Default))
                    builder.setItems(fontColorArray) { dialog, which ->
                        when (which) {
                            0 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#DC143C")
                                 }
                            1 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#FF7F50")
                                 }
                            2 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#696969")
                                 }
                            3 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#FFFAFA")
                                 }
                            4 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontColor").setValue("#36454F")
                                }
                        }
                    }

                    // create and show the alert dialog
                    val dialog = builder.create()
                    dialog.show()
                }
                getString(R.string.Font_Style) -> {

                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(getString(R.string.Choose_Font_Colour))

                    // add a list
                    val fontStyleArray = arrayOf(getString(R.string.cursive), getString(R.string.casual),
                        getString(R.string.serif_monospace), getString(R.string.sans_serif_smallcaps), getString(
                            R.string.serif
                        ))
                    builder.setItems(fontStyleArray) { dialog, which ->
                        when (which) {
                            0 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("cursive")
                                 }
                            1 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("casual")
                                 }
                            2 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("serif-monospace")
                                }
                            3 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("sans-serif-smallcaps")
                                 }
                            4 -> {
                                val colorPicked = FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
                                    .child("chatCharacteristics").child("fontStyle").setValue("serif")
                                 }
                        }
                    }

                    // create and show the alert dialog
                    val dialog = builder.create()
                    dialog.show()

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
                val user = snapshot.getValue(UserDetail::class.java)
                tvName.text = user?.usernameR.toString()
                val userStatus = snapshot.child("status").value
                if(userStatus == "Active") {
                    onlineImageView.visibility = View.VISIBLE
                    statusTextView.visibility = View.VISIBLE
                    statusTextView.text = getString(R.string.Online)
                }
                else{
                    offlineImageView.visibility = View.VISIBLE
                    statusTextView.visibility = View.VISIBLE
                    statusTextView.text = getString(R.string.Offline)
                }
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendMessage(senderId : String, receiverId : String, message : String ){
        val hashMap : HashMap<String , String> = HashMap()
        hashMap["senderId"] = senderId
        hashMap["receiverId"] = receiverId
        hashMap["message"] = message
        if(message  == "command-/-Joke"){
            val bundle  = bundleOf("userId" to userId)
            findNavController().navigate(R.id.action_chatFragment_to_categoryAreFragment , bundle)
        }else{
            val ref = FirebaseDatabase.getInstance().reference
            ref.child("Chat").push().setValue(hashMap)
        }

    }

    private fun readMessage(senderId : String , receiverId : String){
        val ref  = FirebaseDatabase.getInstance().getReference("Chat")


        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                chatList.clear()
                if(snapshot.exists()){
                    for(chatSnapshot in snapshot.children){
                        val chatUser = chatSnapshot.getValue(ChatDataClass::class.java)

                        if(chatUser!!.senderId.equals(senderId) && chatUser.receiverId.equals(receiverId) ||
                            chatUser.senderId.equals(receiverId) && chatUser.receiverId.equals(senderId)   ){
                            chatList.add(chatUser)
                        }
                    }

                    recyclerView.adapter = ChatAdapter(chatList)
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

            val builder = AlertDialog.Builder(context!!)
            builder.setCancelable(false)
            builder.setTitle(getString(R.string.Delete_message))
            builder.setMessage(getString(R.string.Are_you_sure_you_want_to_Delete))

            builder.setPositiveButton("YES" , DialogInterface.OnClickListener { dialog, which ->

                Toast.makeText(context , getString(R.string.Message_Deleted) , Toast.LENGTH_SHORT).show()

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

