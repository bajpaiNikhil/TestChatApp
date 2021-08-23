package com.example.testchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    lateinit var auth : FirebaseAuth


    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        navController = findNavController(R.id.navHostController)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp() : Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onStop() {
        super.onStop()
        changeStatus()
    }

    private fun changeStatus() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addValueEventListener(object  : ValueEventListener {
            override fun onDataChange(snapshot : DataSnapshot) {
                if(snapshot.exists()){
                    for(statusSnapshot in snapshot.children){
                        if(auth.currentUser?.uid.toString() == statusSnapshot.key){
                            ref.child(auth.currentUser?.uid.toString()).child("status").setValue("InActive")

                            break
                        }

                    }
                }
            }

            override fun onCancelled(error : DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}