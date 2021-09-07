package com.example.testchatapp.authentication


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.testchatapp.data.UserDetail
import com.example.testchatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class RegisterFragment : Fragment() {
    private lateinit var emailR: EditText
    private lateinit var passwordR: EditText
    private lateinit var usernameR: EditText
    private lateinit var phoneNumberR: EditText
    private lateinit var confirmPasswordR: EditText
    private lateinit var designationR: EditText
    private lateinit var cityRS: Spinner
    private var cityNameR = ""
    private lateinit var  forgetPassQuesR: Spinner
    private var forgetPassQuesString = ""
    private lateinit var forgetPassAns: EditText
    private lateinit var registerButton : Button
    private lateinit var loginTextView: TextView

    private var currentLanguage = "en"
    private lateinit var locale: Locale
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseDatabase

    override fun onCreateView(
        inflater : LayoutInflater, container : ViewGroup?,
        savedInstanceState : Bundle?
    ) : View? {
        // Inflate the layout for this fragment
        locale = Locale(currentLanguage)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = locale
        res.updateConfiguration(conf, dm)

        return inflater.inflate(R.layout.fragment_register, container, false)
    }
    override fun onViewCreated(view : View, savedInstanceState : Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailR = view.findViewById(R.id.email)
        passwordR  =view.findViewById(R.id.password)
        usernameR = view.findViewById(R.id.name)
        phoneNumberR = view.findViewById(R.id.phone)
        confirmPasswordR = view.findViewById(R.id.passwordConfirm)
        designationR = view.findViewById(R.id.designation)

        // city spinner
        cityRS = view.findViewById(R.id.cityRS)
        val cityList: MutableList<String?> = ArrayList()
        cityList.add(0, getString(R.string.selectACity))
        cityList.add(getString(R.string.Mumbai))
        cityList.add(getString(R.string.Delhi))
        cityList.add(getString(R.string.Kolkata))
        cityList.add(getString(R.string.Bengaluru))
        cityList.add(getString(R.string.Hyderabad))
        cityList.add(getString(R.string.Chennai))
        cityList.add(getString(R.string.Ahmedabad))
        cityList.add(getString(R.string.Pune))
        cityList.add(getString(R.string.Surat))
        cityList.add(getString(R.string.Visakhapatnam))
        cityList.add(getString(R.string.Other))

        val cityArrayAdapter: ArrayAdapter<String?>? =
            context?.let { ArrayAdapter<String?>(it, android.R.layout.simple_list_item_1, cityList) }
        cityArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        cityRS.adapter = cityArrayAdapter
        cityRS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent?.getItemAtPosition(position) == getString(R.string.selectACity)) {
                    // no code to be added here
                }
                else {
                    cityNameR = parent?.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        // forget password question's spinner
        forgetPassQuesR = view.findViewById(R.id.forgetPassQuesRS)
        val quesList: MutableList<String?> = ArrayList()
        quesList.add(0, getString(R.string.SelectAQuestion))
        quesList.add(getString(R.string.FavouriteDish))
        quesList.add(getString(R.string.BirthPlace))
        quesList.add(getString(R.string.petName))

        val quesArrayAdapter: ArrayAdapter<String?>? =
            context?.let { ArrayAdapter<String?>(it, android.R.layout.simple_list_item_1, quesList) }
        quesArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        forgetPassQuesR.adapter = quesArrayAdapter
        forgetPassQuesR.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent?.getItemAtPosition(position) == getString(R.string.SelectAQuestion)) {
                    // no code to be added here
                }
                else {
                    forgetPassQuesString = parent?.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        forgetPassAns = view.findViewById(R.id.forgetPassAns)
        registerButton = view.findViewById(R.id.registerB)
        loginTextView = view.findViewById(R.id.loginTv)

        auth = FirebaseAuth.getInstance()
        db = Firebase.database

        loginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        registerButton.setOnClickListener {
            val eMail = emailR.text.toString()
            if(eMail.isEmpty()){
                emailR.setError(getString(R.string.PLEASE_ENTER_YOUR_EMAIL))
            }
            val passWord = passwordR.text.toString()
            if(passWord.isEmpty()){
                passwordR.setError(getString(R.string.PLEASE_ENTER_YOUR_passWord))
            }
            val confirmPassword = confirmPasswordR.text.toString()
            if(confirmPassword.isEmpty()){
                confirmPasswordR.setError(getString(R.string.PLEASE_ENTER_YOUR_confirm_passWord))
            }
            val phoneNumber = phoneNumberR.text.toString()
            if(phoneNumber.isEmpty()){
                phoneNumberR.setError(getString(R.string.PLEASE_ENTER_OUR_phoneNumber))
            }
            val designation = designationR.text.toString()
            if(designation.isEmpty()){
                designationR.setError(getString(R.string.PLEASE_ENTER_YOUR_designation))
            }
            val city = cityNameR
            val forgetPassQues = forgetPassQuesString
            val forgetPassAnsIs = forgetPassAns.text.toString()
            if(forgetPassAnsIs.isEmpty()){
                forgetPassAns.setError(getString(R.string.PLEASE_ENTER_YOUR_Response))
            }

            if(passWord.length>8 && passWord == confirmPassword && eMail.isEmpty() && designation.isEmpty() && forgetPassAnsIs.isEmpty() && city.isEmpty() && forgetPassQues.isEmpty() )  {
                if (phoneNumber.length == 10) {
                    auth.createUserWithEmailAndPassword(eMail, passWord).addOnCompleteListener {
                        if (it.isSuccessful) {
                            addUserInDb()
                            Toast.makeText(context, getString(R.string.Registration_Complete), Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        } else {
                            Toast.makeText(context, getString(R.string.Registration_InComplete), Toast.LENGTH_LONG).show()
                        }
                    }
                }
                else{
                    Toast.makeText(context, getString(R.string.Phone_Number_should_be_of_10_Digits), Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(context, getString(R.string.Password_length_should_be_greater_than_8), Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun addUserInDb(){
        val userName = usernameR.text.toString()
        val email = "${emailR.text}"
        val phoneNumber = phoneNumberR.text.toString()
        val designation = designationR.text.toString()
        val city = cityNameR
        val forgetPassQues = forgetPassQuesString
        val forgetPassAns = forgetPassAns.text.toString()
        val UserObj = UserDetail(auth.currentUser?.uid.toString(), email,userName,phoneNumber , "InActive", designation,city,forgetPassQues,forgetPassAns)
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(auth.currentUser?.uid.toString()).setValue(UserObj)
    }
}
