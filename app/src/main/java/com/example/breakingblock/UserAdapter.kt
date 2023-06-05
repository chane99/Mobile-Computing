package com.example.breakingblock

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.breakingblock.databinding.LocalRankingitemBinding
import com.example.breakingblock.roomdb.User

class UserAdapter(private var dataSet: MutableList<User>):RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    class MyViewHolder(val binding : LocalRankingitemBinding) :RecyclerView.ViewHolder(binding.root)
    override fun getItemCount() = dataSet.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LocalRankingitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }
    fun setList(newList: MutableList<User>) {
        this.dataSet = newList
        Log.d("UserAdapter", "Data set: $newList")
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        val user = dataSet[position]
        val sortedDataSet = dataSet.sortedByDescending { it.score }
        val userIndex = sortedDataSet.indexOf(user)
        val rank = userIndex + 1
        binding.uniqueid.text= "$rank 등"
        binding.nickname.text=user.name
        binding.score.text=user.score.toString()+"점"

    }
}
