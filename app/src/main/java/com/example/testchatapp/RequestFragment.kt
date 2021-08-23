package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RequestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var auth : FirebaseAuth
    lateinit var requestList : ArrayList<chatDataClass>

    lateinit var recyclerView : RecyclerView


    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)


    }

    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.requestRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        auth = FirebaseAuth.getInstance()

        requestList = arrayListOf()


        requestView()
    }

    private fun requestView() {
        val ref  = FirebaseDatabase.getInstance().getReference("Request")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    requestList.clear()
                    for(requestSnapshot in snapshot.children){
                        val requestIs = requestSnapshot.getValue(chatDataClass::class.java)
                        Log.d("requestFragment" , " content is ${requestIs?.message}" +
                                "${requestIs?.receiverId} , ${requestIs?.senderId} , ${auth.currentUser?.uid.toString()}")
                        if (requestIs?.receiverId == auth.currentUser?.uid.toString() ){
                            requestList.add(requestIs)
                        }

                    }
                    recyclerView.adapter = RequestAdapter(requestList)


                }

            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}