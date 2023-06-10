package com.example.breakingblock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class WorldrankAdapter(private val arrayList: ArrayList<UserRecord>, private val context: Context) :
    RecyclerView.Adapter<WorldrankAdapter.WorldrankViewHolder>() {

    inner class WorldrankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_rank: TextView
        var tv_score: TextView
        var tv_displayName: TextView

        init {
            tv_rank = itemView.findViewById(R.id.tv_rank)
            tv_score = itemView.findViewById(R.id.tv_score)
            tv_displayName = itemView.findViewById(R.id.tv_displayName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldrankViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.world_rankingitem, parent, false)
        val holder = WorldrankViewHolder(view)
        return holder
    }


    override fun onBindViewHolder(holder: WorldrankViewHolder, position: Int) {
        holder.tv_rank.setText((position+1).toString() + "등")
        holder.tv_score.setText((arrayList.get(position).getScore()).toString()+ "점")
        holder.tv_displayName.setText(arrayList.get(position).getName())
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

}
