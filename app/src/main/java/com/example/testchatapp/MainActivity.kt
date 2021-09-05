package com.example.testchatapp

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this , Observer { isConnected ->
            if(isConnected){
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(true)
                builder.setTitle("Delete message ?")
                builder.setMessage("Internet")
                builder.create()
                builder.show()
            }else{
                val builder = AlertDialog.Builder(this)
                builder.setCancelable(true)
                builder.setTitle("Delete message ?")
                builder.setMessage("No Internet")
                builder.create()
                builder.show()
            }
        })
    }
}
