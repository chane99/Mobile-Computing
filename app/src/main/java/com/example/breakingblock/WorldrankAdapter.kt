package com.example.breakingblock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WorldrankAdapter(private val arrayList: ArrayList<WorldrankUser>, private val context: Context) :
    RecyclerView.Adapter<WorldrankAdapter.WorldrankViewHolder>() {

    inner class WorldrankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_id: TextView
        var tv_score: TextView
        var tv_nickname: TextView

        init {
            tv_id = itemView.findViewById(R.id.tv_id)
            tv_score = itemView.findViewById(R.id.tv_score)
            tv_nickname = itemView.findViewById(R.id.tv_nickname)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldrankViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.world_rankingitem, parent, false)
        val holder = WorldrankViewHolder(view)
        return holder
    }


    override fun onBindViewHolder(holder: WorldrankViewHolder, position: Int) {
        holder.tv_id.setText(arrayList.get(position).getId())
        holder.tv_score.setText((arrayList.get(position).getPw()).toString())
        holder.tv_nickname.setText(arrayList.get(position).getUserName())
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

}
