package plinko.test.task.plinko.ui.fragments.leaderboard.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import plinko.test.task.plinko.R
import plinko.test.task.plinko.data.model.Player
import plinko.test.task.plinko.databinding.ItemLeaderboardBinding

@SuppressLint("SetTextI18n")
class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    private var players = listOf<Player>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateLeaderboard(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val binding = ItemLeaderboardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return LeaderboardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(players[position], position + 1)
    }

    override fun getItemCount(): Int = players.size

    inner class LeaderboardViewHolder(private val binding: ItemLeaderboardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(player: Player, rank: Int) {
            binding.tvRank.text = rank.toString()
            binding.tvName.text = player.name
            binding.tvScore.text = player.score.toString()
            binding.tvTime.text = "${player.time}s"

            // Set rank icon based on position
            when (rank) {
                1 -> {
                    binding.ivRankIcon.setImageResource(R.drawable.ic_trophy_gold)
                    binding.ivRankIcon.visibility = View.VISIBLE
                    binding.tvRank.visibility = View.GONE
                }
                2 -> {
                    binding.ivRankIcon.setImageResource(R.drawable.ic_trophy_silver)
                    binding.ivRankIcon.visibility = View.VISIBLE
                    binding.tvRank.visibility = View.GONE
                }
                3 -> {
                    binding.ivRankIcon.setImageResource(R.drawable.ic_trophy_bronze)
                    binding.ivRankIcon.visibility = View.VISIBLE
                    binding.tvRank.visibility = View.GONE
                }
                else -> {
                    binding.ivRankIcon.visibility = View.GONE
                    binding.tvRank.visibility = View.VISIBLE
                }
            }

            if (player.isCurrentPlayer) {
                binding.root.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.current_player_highlight)
                )
                binding.root.cardElevation = 8f
            } else {
                binding.root.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.default_card_background)
                )
                binding.root.cardElevation = 4f
            }

            if (player.isCurrentPlayer) {
                binding.root.animate()
                    .scaleX(1.02f)
                    .scaleY(1.02f)
                    .setDuration(200)
                    .start()
            } else {
                binding.root.scaleX = 1f
                binding.root.scaleY = 1f
            }
        }
    }
}