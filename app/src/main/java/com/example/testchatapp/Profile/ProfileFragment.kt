package com.example.testchatapp.Profile

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.testchatapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {

    // For Edit Text Display

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: TextView
    private lateinit var imageView: ImageView
    private lateinit var userProfileUpdateFloatingActionButton: FloatingActionButton
    private lateinit var userNameEditFloatingActionButton: FloatingActionButton
    private lateinit var userNameUpdateFloatingActionButton: FloatingActionButton
    private lateinit var imageUploadProgressBar: ProgressBar
    private lateinit var languageSpinner: Spinner
    private lateinit var passwordChangeTextView: TextView
    private lateinit var profileText : TextView

    //user image
    private lateinit var uri: Uri
    private var imageUrl: String? = null
    private var imageName: String = ""

    private lateinit var logOut: Button
    lateinit var builder : AlertDialog.Builder

    // For FireBase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString("currentUserImgUrl")
        }
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // For Database*

        logOut = view.findViewById(R.id.logOut)

        builder = AlertDialog.Builder(context)

        val languageRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString()).child("appLanguage")

        languageSpinner = view.findViewById(R.id.languageSpinner)
        passwordChangeTextView = view.findViewById(R.id.passTv)
        profileText = view.findViewById(R.id.profileText)

        val language: MutableList<String?> = ArrayList()
        language.add(0, getString(R.string.change_language))
        language.add(1,getString(R.string.Hindi))
        language.add(2,getString(R.string.French))
        language.add(3,getString(R.string.English))


        val languageAdapter: ArrayAdapter<String?>? =
            context?.let { ArrayAdapter<String?>(it, android.R.layout.simple_list_item_1, language) }
        languageAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = languageAdapter
        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (position) {
                    0 -> {
                    }
                    1 -> {
                        dialog(getString(R.string.Hindi))
                    }
                    2 -> {
                        dialog(getString(R.string.French))
                    }
                    3 -> {
                        dialog(getString(R.string.English))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

    // For Edit Text Display
        userNameEditText = view.findViewById(R.id.userNameEt)
        passwordEditText = view.findViewById(R.id.passTv)
        imageView = view.findViewById(R.id.profileIv)
        userProfileUpdateFloatingActionButton = view.findViewById(R.id.userImageFab)
        userNameEditFloatingActionButton = view.findViewById(R.id.editUserNameFab)
        userNameUpdateFloatingActionButton = view.findViewById(R.id.updateUserNameFab)
        imageUploadProgressBar = view.findViewById(R.id.imageUploadProgress)

        imageUploadProgressBar.visibility = View.INVISIBLE
        userNameUpdateFloatingActionButton.visibility = View.INVISIBLE

        val userReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString())
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //EditText
                val userName = snapshot.child("usernameR").value.toString()
                val photoUrl = snapshot.child("userProfileImgUrl").value
                if (snapshot.child("userProfileImgUrl").exists()) {
                    if (photoUrl.toString().isNotEmpty()) {
                        Log.d("ProfileFragment", "ImageUrl : $photoUrl")
                        if (imageUrl == null) {
                            context?.let { Glide.with(it).load(photoUrl).into(imageView) }
                        } else {
                            context?.let { Glide.with(it).load(imageUrl).into(imageView) }
                        }
                    } else {
                        context?.let { Glide.with(it).load(R.drawable.image).into(imageView) }
                    }

                } else {
                    context?.let { Glide.with(it).load(R.drawable.image).into(imageView) }
                }
                userNameEditText.isEnabled = false
                userNameEditText.setText(userName)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        return view
    }

    private fun dialog(string: String){

        builder.setCancelable(false)
        builder.setTitle(getString(R.string.Language))
        builder.setMessage("${getString(R.string.Changed_to)} $string")
        builder.setPositiveButton(getString(R.string.Yes)) { _, _ ->
            if (string == getString(R.string.Hindi)) {
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(auth.currentUser?.uid.toString()).child("appLanguage").setValue("hi")
            } else if (string == getString(R.string.French)) {
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(auth.currentUser?.uid.toString()).child("appLanguage").setValue("fr")
            } else {
                FirebaseDatabase.getInstance().getReference("Users")
                    .child(auth.currentUser?.uid.toString()).child("appLanguage").setValue("")
            }

            findNavController().navigate(R.id.loginFragment)
        }

        builder.setNegativeButton(getString(R.string.No)) { dialog, _ ->
            dialog.cancel()
        }
        builder.create().show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logOut.setOnClickListener {
            val lastUser = auth.currentUser?.uid
            auth.signOut()
            FirebaseDatabase.getInstance().getReference("Users").child(lastUser.toString())
                .child("status").setValue("InActive")
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

        userNameEditFloatingActionButton.setOnClickListener {
            userNameEditFloatingActionButton.visibility = View.INVISIBLE
            userNameUpdateFloatingActionButton.visibility = View.VISIBLE

            userNameEditText.isEnabled = true
        }

        userNameUpdateFloatingActionButton.setOnClickListener {
            userNameUpdateFloatingActionButton.visibility = View.INVISIBLE
            userNameEditFloatingActionButton.visibility = View.VISIBLE

            val newUserName = userNameEditText.text.toString()
            FirebaseDatabase.getInstance().getReference("Users")
                .child(auth.currentUser?.uid.toString()).child("usernameR").setValue(newUserName)
        }

        userProfileUpdateFloatingActionButton.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }

        passwordEditText.setOnClickListener{

            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            uri = data?.data!! //this data refer to data of our intent

            uploadImage(uri)
        }
    }



    private fun uploadImage(uri: Uri?) {
        imageUploadProgressBar.visibility = View.VISIBLE
        imageName =
            uri?.lastPathSegment?.removePrefix("raw:/storage/emulated/0/Download/").toString()
        Log.d(
            "ProfileFragment",
            "Image name to be uploaded in firebase storage database : $imageName"
        )
        val storageReference = FirebaseStorage.getInstance().reference.child("image/$imageName")
        if (uri != null) {
            storageReference.putFile(uri).addOnSuccessListener { it ->
                it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                    imageUrl = it.toString()
                    Log.d("ProfileFragment", "ImageUrl generated: $imageUrl")
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(auth.currentUser?.uid.toString()).child("userProfileImgUrl").setValue(imageUrl)
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(auth.currentUser?.uid.toString()).child("userProfileImgName").setValue(imageName)
                    imageUploadProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, "Your Image has been Updated", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                imageUploadProgressBar.visibility = View.INVISIBLE
                Toast.makeText(context, "Image Upload failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}
