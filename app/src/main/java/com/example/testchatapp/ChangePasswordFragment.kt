package com.example.testchatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChangePasswordFragment : Fragment() {

    lateinit var auth : FirebaseAuth

    lateinit var questionTV : TextView
    lateinit var answerTV : TextView
    lateinit var headingTV : TextView
    lateinit var answerET : EditText
    lateinit var questionET : TextView
    lateinit var submit : Button

    lateinit var passHeadingTV : TextView
    lateinit var passwordTV : TextView
    lateinit var newPasswordTV : TextView
    lateinit var confirmPasswordTV : TextView

    lateinit var passwordET : EditText
    lateinit var newPasswordET : EditText
    lateinit var confirmPasswordET : EditText
    lateinit var submitPassword : Button

    lateinit var quesAnsCardView: CardView
    lateinit var changePassCardView: CardView

    var answer =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //findviewbyid for first pass

        quesAnsCardView = view.findViewById(R.id.quesAnsCv)
        changePassCardView = view.findViewById(R.id.changePassCv)
        changePassCardView.visibility = View.INVISIBLE

        questionTV = view.findViewById(R.id.questionTextView)
        answerTV = view.findViewById(R.id.answerTextView)
        headingTV = view.findViewById(R.id.textView2)

        questionET = view.findViewById(R.id.questionEditText)
        answerET = view.findViewById(R.id.answerEditText)
        submit = view.findViewById(R.id.submitAnswer)

        //findViewById  for second pass

        passHeadingTV = view.findViewById(R.id.textView7)
        passwordTV = view.findViewById(R.id.textView8)
        newPasswordTV = view.findViewById(R.id.textView9)
        confirmPasswordTV = view.findViewById(R.id.textView10)

        passwordET = view.findViewById(R.id.oldPasswordET)
        newPasswordET = view.findViewById(R.id.newPasswordET)
        confirmPasswordET = view.findViewById(R.id.confirmPasswordET)

        submitPassword = view.findViewById(R.id.submitPassword)
        submitPassword.isClickable = false

        auth = FirebaseAuth.getInstance()

        val forgetRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(auth.currentUser?.uid.toString())
        forgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val question = snapshot.child("forgetPassQues").value
                answer = snapshot.child("forgetPassAns").value.toString()

                questionET.text = question.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        submit.setOnClickListener {
            if(answerET.text.toString() == answer){

                quesAnsCardView.visibility = View.INVISIBLE
                submit.isClickable = false

                changePassCardView.visibility = View.VISIBLE
                submitPassword.isClickable = true
            }
        }

        submitPassword.setOnClickListener {
            if(passwordET.text.isNotEmpty() && newPasswordET.text.isNotEmpty() && confirmPasswordET.text.isNotEmpty()) {
                if (newPasswordET.text.toString() == confirmPasswordET.text.toString()) {
                    val password = passwordET.text.toString()
                    val newPassword = newPasswordET.text.toString()
                    val confirmPassword = confirmPasswordET.text.toString()

                    Log.i("dialog", password)
                    Log.i("dialog", newPassword)
                    Log.i("dialog", confirmPassword)

                    if (confirmPassword == newPassword) {
                        val userCurrent = auth.currentUser
                        val credentials =
                            EmailAuthProvider.getCredential(userCurrent?.email.toString(), password)
                        userCurrent?.reauthenticate(credentials)?.addOnCompleteListener { p0 ->
                            if (p0.isSuccessful) {
                                userCurrent.updatePassword(confirmPassword).addOnCompleteListener { p01 ->
                                    if (p01.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "password Changed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "password Changed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                        Toast.makeText(context, "Saved Sucessfully", Toast.LENGTH_LONG).show()
                    }
                }
                findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
            }
            else{
                Toast.makeText(
                    context,
                    "Please make sure to enter all the fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
