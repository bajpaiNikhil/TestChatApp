package com.example.testchatapp.Chat.Joke

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.testchatapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BottomSheetMenuFragment : BottomSheetDialogFragment() {

    lateinit var auth : FirebaseAuth

    private lateinit var fontSize : TextView
    private lateinit var fontColor : TextView
    private lateinit var fontStyle : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        fontSize = view.findViewById(R.id.fontSizeNewTV)
        fontStyle = view.findViewById(R.id.fontStyleNewTV)
        fontColor = view.findViewById(R.id.fontColorTV)

        fontSize.setOnClickListener {
            Log.d("BSMF" , "item is clicked")
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

        fontColor.setOnClickListener {
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

        fontStyle.setOnClickListener {
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
    }

}