package com.example.testchatapp

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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
    // For Edit Text Display
    lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: TextView
    lateinit var imageView: ImageView
    lateinit var userProfileUpdateFloatingActionButton: FloatingActionButton
    lateinit var userNameEditFloatingActionButton: FloatingActionButton
    lateinit var userNameUpdateFloatingActionButton: FloatingActionButton
    lateinit var imageUploadProgressBar: ProgressBar

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logOut.setOnClickListener {
            val lastUser = auth.currentUser?.uid
            auth.signOut()
            FirebaseDatabase.getInstance().getReference("Users").child(lastUser.toString())
                .child("status").setValue("InActive")
//            val mSaving = this.childFragmentManager.findFragmentById(R.id.profileFragment)
//            getFragmentManager()?.beginTransaction()?.hide(mSaving!!)?.commit()
////            getFragmentManager()?.beginTransaction()?.remove(requireParentFragment())?.commitAllowingStateLoss()
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

//            val forgetRef =  FirebaseDatabase.getInstance().getReference("Users").child(auth.currentUser?.uid.toString())
//            forgetRef.addValueEventListener(object : ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val question = snapshot.child("forgetPassQues").value
//                    val answer   = snapshot.child("forgetPassAns").value
//                    val questionDialog = AlertDialog.Builder(context)
//                    questionDialog.setTitle("Give adequate response")
//                    questionDialog.setMessage(question.toString())
//
//                    val questionLayout = LinearLayout(context)
//                    questionLayout.orientation = LinearLayout.VERTICAL
//
//                    val questionET = EditText(context)
//                    questionET.setSingleLine()
//                    questionET.hint = "Answer"
//                    questionLayout.addView(questionET)
//                    questionLayout.setPadding(50, 40, 50, 10)
//                    questionDialog.setView(questionLayout)
//
//                    questionDialog.setPositiveButton("Correct"){ questionDialogIs , _ ->
//
//                        val answerIs = answer.toString()
//                        val questionRes = questionET.text.toString()
//
//                        if(questionRes == answerIs){
//
//                            questionDialogIs.cancel()
//
//                            Log.d("dialog" , "Reached till here")
//
//                            val alert = AlertDialog.Builder(context)
//                            alert.setTitle("Change password")
//                            alert.setMessage("Change Password")
//                            val layout = LinearLayout(context)
//                            layout.orientation = LinearLayout.VERTICAL
//
//
//                            val passwordET = EditText(context)
//                            passwordET.setSingleLine()
//                            passwordET.hint = "Old password"
//                            layout.addView(passwordET)
//
//
//                            val newPasswordET = EditText(context)
//                            newPasswordET.setSingleLine()
//                            newPasswordET.hint = "new Password"
//                            layout.addView(newPasswordET)
//
//
//                            val confirmPasswordET = EditText(context)
//                            confirmPasswordET.setSingleLine()
//                            confirmPasswordET.hint = "confirm Password"
//                            layout.addView(confirmPasswordET)
//
//                            layout.setPadding(50, 40, 50, 10)
//
//                            alert.setView(layout)
//
//                            alert.setPositiveButton("Proceed") { answerDialogIs, _ ->
//                                answerDialogIs.dismiss()
//                                questionDialogIs.cancel()
//                                val password = passwordET.text.toString()
//                                val newPassword = newPasswordET.text.toString()
//                                val confirmPassword = confirmPasswordET.text.toString()
//
//                                Log.i("dialog",password )
//                                Log.i("dialog",newPassword )
//                                Log.i("dialog",confirmPassword )
//
//                                if(confirmPassword == newPassword){
//                                    val userCurrent = auth.currentUser
//                                    val credentials = EmailAuthProvider.getCredential(userCurrent?.email.toString() ,password)
//                                    userCurrent?.reauthenticate(credentials)?.addOnCompleteListener { p0 ->
//                                        if (p0.isSuccessful) {
//                                            userCurrent.updatePassword(confirmPassword)
//                                                .addOnCompleteListener { p01 ->
//                                                    if (p01.isSuccessful) {
//                                                        Toast.makeText(
//                                                            context,
//                                                            "password Changed",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    } else {
//                                                        Toast.makeText(
//                                                            context,
//                                                            "password Changed",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                }
//                                        }
//                                    }
//
//                                    Toast.makeText(context, "Saved Sucessfully", Toast.LENGTH_LONG).show()
//
//                                }
//                            }
//
//                            alert.setNegativeButton("Cancel") { dialog, _ ->
//                                dialog.dismiss()
//                            }
//                            alert.setCancelable(false)
//                            alert.show()
//                        }
//                        questionDialogIs.dismiss()
//                    }
//                    questionDialog.setNegativeButton("cancel"){dialog,_ ->
//                        dialog.cancel()
//                    }
//                    questionDialog.setCancelable(false)
//                    questionDialog.show()
//                }
//                override fun onCancelled(error: DatabaseError){
//                    TODO("Not yet implemented")
//                }
//            })
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(context, "Your Image is being Uploaded", Toast.LENGTH_LONG).show()
        imageUploadProgressBar.visibility = View.VISIBLE

        if (resultCode == Activity.RESULT_OK && requestCode == 0) {
            uri = data?.data!! //this data refer to data of our intent

            uploadImage(uri)
        }
    }



    private fun uploadImage(uri: Uri?) {
        imageName =
            uri?.lastPathSegment?.removePrefix("raw:/storage/emulated/0/Download/").toString()
        Log.d(
            "ProfileFragment",
            "Image name to be uploaded in firebase storage database : $imageName"
        )
        val storageReference = FirebaseStorage.getInstance().reference.child("image/$imageName")
        if (uri != null) {
            storageReference.putFile(uri)
                .addOnSuccessListener { it ->
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        imageUrl = it.toString()
                        Log.d("ProfileFragment", "ImageUrl generated: $imageUrl")
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(auth.currentUser?.uid.toString()).child("userProfileImgUrl").setValue(imageUrl)
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(auth.currentUser?.uid.toString()).child("userProfileImgName").setValue(imageName)
                    }
                    imageUploadProgressBar.visibility = View.INVISIBLE

                    Toast.makeText(context, "Your Image has been Updated", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    imageUploadProgressBar.visibility = View.INVISIBLE
                    Toast.makeText(context, "Image Upload failed", Toast.LENGTH_LONG).show()
                }
        }
    }
}
