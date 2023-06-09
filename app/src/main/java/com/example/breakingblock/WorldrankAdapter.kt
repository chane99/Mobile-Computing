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
        var iv_profile: ImageView
        var tv_id: TextView
        var tv_pw: TextView
        var tv_username: TextView

        init {
            iv_profile = itemView.findViewById(R.id.iv_profile)
            tv_id = itemView.findViewById(R.id.tv_id)
            tv_pw = itemView.findViewById(R.id.tv_pw)
            tv_username = itemView.findViewById(R.id.tv_username)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorldrankViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.world_rankingitem, parent, false)
        val holder = WorldrankViewHolder(view)
        return holder
    }


    override fun onBindViewHolder(holder: WorldrankViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(arrayList.get(position).getProfile())
            .into(holder.iv_profile)
        holder.tv_id.setText(arrayList.get(position).getId())
        holder.tv_pw.setText((arrayList.get(position).getPw()).toString())
        holder.tv_username.setText(arrayList.get(position).getUserName())
    }

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

}
