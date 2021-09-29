package com.example.testchatapp.Chat.Joke

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testchatapp.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategoryAreFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    var jokesToSend : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jokesToSend = it.getString("userId")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_category_are, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rView)
        recyclerView.layoutManager = GridLayoutManager(context ,2)
        getCategoryForJokes()
    }

    private fun getCategoryForJokes() {
        val request = ApiHitInterface.getJokeCategory().getJoke()
        Log.d("NameFragment" , "$request")
        request.enqueue(CategoryCallBack())
    }

    inner class CategoryCallBack : Callback<List<String>> {
        override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
            Log.d("NameFragment" , "${response.body()}")
            if(response.isSuccessful){
                val jCat = response.body()

                fun onItemSelected(s: String) {
                    Log.d("NameFragment" , s)
                    val bundle = bundleOf("itemPicked" to s , "jokesToSend" to jokesToSend)
                    findNavController().navigate(R.id.action_categoryAreFragment_to_jokeFragment, bundle)

                }
                jCat?.let {
                    recyclerView.adapter = JokeAdapter(jCat , ::onItemSelected)
                }
            }
        }

        override fun onFailure(call: Call<List<String>>, t: Throwable) {
            TODO("Not yet implemented")
        }
    }

}