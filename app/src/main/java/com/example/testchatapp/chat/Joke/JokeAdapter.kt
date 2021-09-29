package com.example.testchatapp.Chat.Joke

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.testchatapp.R

class JokeAdapter (private val jokeCatList : List<String>, val listener : (String) -> Unit): RecyclerView.Adapter<JokeAdapter.JokeHolder>(){
    inner class JokeHolder(view : View) : RecyclerView.ViewHolder(view) {
        val catIs = view.findViewById<TextView>(R.id.itemCatTv)
        val cvBackGroundChange = view.findViewById<CardView>(R.id.cardViewIs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JokeHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent , false)
        return JokeHolder(view)
    }

    override fun onBindViewHolder(holder: JokeHolder, position: Int) {
        val currentItem = jokeCatList[position]
        holder.catIs.text = currentItem

        holder.itemView.setOnClickListener {
            listener(currentItem)
        }
        val colorArray  = arrayListOf(
            Color.RED , Color.LTGRAY , Color.WHITE , Color.GRAY ,
            Color.MAGENTA ,
            Color.YELLOW)
        val randomColor = (0..5).random()
        holder.cvBackGroundChange.setCardBackgroundColor(colorArray[randomColor])
    }

    override fun getItemCount(): Int {
        return jokeCatList.size
    }

}