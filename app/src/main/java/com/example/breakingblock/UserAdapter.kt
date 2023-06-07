import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.breakingblock.databinding.LocalRankingitemBinding
import com.example.breakingblock.roomdb.User

class UserAdapter(private var dataSet: MutableList<User>) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {

    class MyViewHolder(val binding: LocalRankingitemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return if (dataSet.isEmpty()) {
            1 // 데이터가 비어 있을 경우, 빈 상태를 표시하기 위한 아이템 하나 반환
        } else {
            dataSet.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (dataSet.isEmpty()) {
            VIEW_TYPE_EMPTY // 데이터가 비어 있을 경우, 빈 상태를 표시하는 뷰 타입 반환
        } else {
            VIEW_TYPE_DATA // 데이터가 있을 경우, 데이터 아이템을 표시하는 뷰 타입 반환
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LocalRankingitemBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_DATA) {
            val binding = holder.binding
            val user = dataSet[position]
            val sortedDataSet = dataSet.sortedByDescending { it.score }
            val userIndex = sortedDataSet.indexOf(user)
            val rank = userIndex + 1
            binding.uniqueid.text = "$rank 등"
            binding.nickname.text = user.name
            binding.score.text = "${user.score}점"
        }
    }

    fun setList(newList: MutableList<User>) {
        this.dataSet = newList
        Log.d("UserAdapter", "Data set: $newList")
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_DATA = 1
    }
}
