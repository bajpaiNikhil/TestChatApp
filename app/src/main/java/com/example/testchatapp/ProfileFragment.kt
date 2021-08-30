package com.example.testchatapp

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
    lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: TextView
    lateinit var imageView: ImageView
    lateinit var userProfileUpdateFloatingActionButton: FloatingActionButton
    lateinit var userNameEditFloatingActionButton: FloatingActionButton
    lateinit var userNameUpdateFloatingActionButton: FloatingActionButton
    lateinit var imageUploadProgressBar: ProgressBar
    lateinit var languageSpinner: Spinner
    private var currentLanguage = "en"
    private var currentLang: String? = null
    lateinit var locale: Locale

    //user image
    lateinit var uri: Uri
    var imageUrl: String? = null
    var imageName: String = ""

    lateinit var logOut: Button

    // For FireBase
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUrl = it.getString("currentUserImgUrl")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // For Database*

        auth = Firebase.auth

        logOut = view.findViewById(R.id.logOut)

        languageSpinner = view.findViewById(R.id.languageSpinner)
        val language: MutableList<String?> = ArrayList()
        language.add(0, "Select a language")
        language.add("Hindi")
        language.add("French")
        language.add("English")

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
                        setLocale("hi")
                        FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString()).child("appLanguage").setValue("hi")
                    }
                    2 -> {
                        setLocale("fr")
                        FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString()).child("appLanguage").setValue("fr")
                    }
                    3 -> {
                        setLocale("")
                        FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString()).child("appLanguage").setValue("")
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

    private fun setLocale(localeName: String) {
        Log.d("loginFrag" , localeName)

        if (localeName != currentLanguage) {
            Log.d("loginFrag" , currentLanguage)
            locale = Locale(localeName)
            val res = resources
            val dm = res.displayMetrics
            val conf = res.configuration
            conf.locale = locale
            res.updateConfiguration(conf, dm)

            val refresh = Intent(
                context,
                MainActivity::class.java
            )

            Log.d("loginFrag" , currentLanguage)
            refresh.putExtra(currentLang, localeName)
            startActivity(refresh)

        } else {
            Toast.makeText(
                context, "Language, , already, , selected)!", Toast.LENGTH_SHORT).show();
        }
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
            Log.d("dialog", "Reached till here")
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
