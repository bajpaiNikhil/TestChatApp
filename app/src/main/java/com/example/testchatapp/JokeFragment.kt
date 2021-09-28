package com.example.testchatapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.testchatapp.data.JokesCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JokeFragment : Fragment() {

    var pickedItem : String = ""
    var jokeToSend : String = ""
    lateinit var auth  : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pickedItem = it.getString("itemPicked")!!
            jokeToSend = it.getString("jokesToSend")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_joke, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        getJokePicked()

        val refreshButton =  view.findViewById<TextView>(R.id.refreshJokeBtn)
        refreshButton.setOnClickListener {
            getJokePicked()
        }
    }
    private fun getJokePicked() {
        val request = ApiHitInterface.getJokeCategory().getPickedJoke(pickedItem)
        request.enqueue(PickedJokeCallBack())
    }

    inner class PickedJokeCallBack : Callback<JokesCategory>{
        override fun onResponse(call: Call<JokesCategory>, response: Response<JokesCategory>) {
            Log.d("JokeFragment"  , response.toString())

            if(response.isSuccessful){
                val jJoke = response.body()
                jJoke?.let {
                    val jokeTitle = view?.findViewById<TextView>(R.id.JokeTitleTV)
                    jokeTitle?.text = pickedItem

                    val jokeIs = view?.findViewById<TextView>(R.id.jokeTV)
                    jokeIs?.text = it.value.toString()
                    val buttonCopy = view?.findViewById<TextView>(R.id.copyBt)
                    buttonCopy?.setOnClickListener { a->
                        copyToClipBoard(it.value.toString())
                    }

                    val jokeBtn = view?.findViewById<Button>(R.id.jokeSendBtn)
                    jokeBtn?.setOnClickListener { b->
                        sendJoke(auth.currentUser?.uid!! , jokeToSend , it.value!!)
                        val bundle = bundleOf("userId" to jokeToSend)
                        findNavController().navigate(R.id.action_jokeFragment_to_chatFragment , bundle)
                    }

                }
            }
        }

        override fun onFailure(call: Call<JokesCategory>, t: Throwable) {
            Log.d("JokeFragment" , "Error is : ${t.message}")
        }
    }

    private fun sendJoke(senderId : String , receiverId : String , message : String ){

        val hashMap : HashMap<String , String> = HashMap()
        hashMap.put("senderId" , senderId)
        hashMap.put("receiverId" , receiverId)
        hashMap.put("message" , message)
        val ref = FirebaseDatabase.getInstance().reference
        ref.child("Chat").push().setValue(hashMap)

    }

    private fun copyToClipBoard(JokeCopy: String) {
        val clipboardManager = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Encrypted Text" , JokeCopy.replace(", ",""))
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context , "TextCopied"  , Toast.LENGTH_LONG).show()
    }
}