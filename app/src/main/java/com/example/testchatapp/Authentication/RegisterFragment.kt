package com.example.testchatapp


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
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
    private
    lateinit var auth : FirebaseAuth
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
        cityList.add(0, "Select a city")
        cityList.add("Mumbai")
        cityList.add("Delhi")
        cityList.add("Kolkata")
        cityList.add("Bengaluru")
        cityList.add("Hyderabad")
        cityList.add("Chennai")
        cityList.add("Ahmedabad")
        cityList.add("Pune")
        cityList.add("Surat")
        cityList.add("Visakhapatnam")
        cityList.add("Other")

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
                if (parent?.getItemAtPosition(position) == "Select a city") {
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
        quesList.add(0, "Select a question")
        quesList.add("Favourite Dish?")
        quesList.add("Birth Place?")
        quesList.add("First Pet's Name?")

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
                if (parent?.getItemAtPosition(position) == "Select a question") {
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
            val passWord = passwordR.text.toString()
            val confirmPassword = confirmPasswordR.text.toString()
            val phoneNumber = phoneNumberR.text.toString()
            val designation = designationR.text.toString()
            val city = cityNameR
            val forgetPassQues = forgetPassQuesString
            val forgetPassAns = forgetPassAns.text.toString()

            if(eMail.isNotEmpty() && passWord.isNotEmpty() && confirmPassword.isNotEmpty() && phoneNumber.isNotEmpty()
                && designation.isNotEmpty() && city.isNotEmpty() && forgetPassQues.isNotEmpty() && forgetPassAns.isNotEmpty()
                ) {
                if(passWord.length>8 && passWord == confirmPassword) {
                    if (phoneNumber.length == 10) {
                        auth.createUserWithEmailAndPassword(eMail, passWord).addOnCompleteListener {
                            if (it.isSuccessful) {
                                addUserInDb()
                                Toast.makeText(context, getString(R.string.Registration_Complete), Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Registration InComplete",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else{
                        Toast.makeText(context, "Phone Number should be of 10 Digits", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(context, "Password length should be greater than 8", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(context, "Please check if all fields are filled", Toast.LENGTH_SHORT)
                    .show()
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
class UserDetail() {
    var userId : String = "" // primary key
    var emailR : String = ""
    var usernameR : String = ""
    var phoneNumberR : String = ""
    var status : String = ""
    var designation : String =""
    var city : String =""
    var forgetPassQues : String=""
    var forgetPassAns: String =""
    constructor(
        userId : String,
        emailR : String,
        usernameR : String,
        phoneNumberR : String,
        status : String,
        designation : String,
        city : String,
        forgetPassQues : String,
        forgetPassAns : String
    ) : this(){
        this.userId             = userId
        this.emailR             = emailR
        this.usernameR          = usernameR
        this.phoneNumberR       = phoneNumberR
        this.status             = status
        this.designation        = designation
        this.city               = city
        this.forgetPassQues     = forgetPassQues
        this.forgetPassAns      = forgetPassAns
    }
}