package com.example.breakingblock.roomdb

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.breakingblock.databinding.LocalRankingitemBinding

class UserAdapter(private var dataSet: MutableList<user>):RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    class MyViewHolder(val binding : LocalRankingitemBinding) :RecyclerView.ViewHolder(binding.root)
    override fun getItemCount() = dataSet.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LocalRankingitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    fun setList(newList: MutableList<user>) {
        this.dataSet = newList
        notifyDataSetChanged()
        Log.d("UserAdapter", "Data set: $newList")
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.nickname.text=dataSet[position].name
        binding.score.text=dataSet[position].score.toString()

    }
}
