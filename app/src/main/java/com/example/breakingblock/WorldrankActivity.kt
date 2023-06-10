package com.example.breakingblock

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.breakingblock.roomdb.User
import com.google.firebase.database.*

class WorldrankActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorldrankAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var arrayList: ArrayList<UserRecord>
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.world_ranking_view)

        recyclerView = findViewById(R.id.world_ranking_recyclerview)
        recyclerView.setHasFixedSize(true)
        arrayList = ArrayList() // World rank 기록을 담을 배열

        adapter = WorldrankAdapter(arrayList, this)
        layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        database = FirebaseDatabase.getInstance() // 파이어베이스 DB 연동
        databaseReference = database.getReference("Worldranking");
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //파이어베이스 데이터베이스 받아오는 부분
                arrayList.clear() // 기존 배열 존재하지 않게 초기화
                for (snapshot in dataSnapshot.children) {
                    val worldranking = snapshot.getValue(UserRecord::class.java)
                    if (worldranking != null) {
                        arrayList.add(worldranking)
                    }
                    arrayList.sortByDescending { it.getScore().toInt() }

                    adapter.notifyDataSetChanged() // 리스트 저장 및 새로고침
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                //
                Log.d("TAG",databaseError.toException().toString())
            }
        })
        adapter = WorldrankAdapter(arrayList, this)
        recyclerView.adapter = adapter

    }
}
