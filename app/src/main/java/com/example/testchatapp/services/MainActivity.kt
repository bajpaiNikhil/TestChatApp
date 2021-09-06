package com.example.testchatapp.services

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.testchatapp.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observe(this , Observer { isConnected ->
            if (isConnected) {
                //do nothing
//                Toast.makeText(this@MainActivity, "Welcome", Toast.LENGTH_LONG).show()
            } else {
                AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Internet Connection Alert")
                    .setMessage("Please Check Your Internet Connection")
                    .setPositiveButton("Close") { dialogInterface, i -> finish() }.show()
            }
        })
    }
}