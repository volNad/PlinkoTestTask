package plinko.test.task.plinko.ui.fragments.game.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.recyclerview.widget.RecyclerView
import plinko.test.task.plinko.R
import plinko.test.task.plinko.data.model.GameItem
import plinko.test.task.plinko.databinding.ItemGameGridBinding

class GameGridAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<GameGridAdapter.GameViewHolder>() {

    private var gameItems = listOf<GameItem>()
    private var selectedPosition: Int? = null

    @SuppressLint("NotifyDataSetChanged")
    fun updateGrid(newItems: List<GameItem>) {
        gameItems = newItems
        notifyDataSetChanged()
    }

    fun highlightSelectedItem(position: Int?) {
        val oldPosition = selectedPosition
        selectedPosition = position
        oldPosition?.let { notifyItemChanged(it) }
        position?.let { notifyItemChanged(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameGridBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(gameItems[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = gameItems.size

    inner class GameViewHolder(private val binding: ItemGameGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isAnimating = false

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && !isAnimating) {
                    animateItemClick {
                        onItemClick(adapterPosition)
                    }
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(item: GameItem, isSelected: Boolean) {
            val resourceId = when (item.type) {
                1 -> R.drawable.el1
                2 -> R.drawable.el2
                3 -> R.drawable.el3
                4 -> R.drawable.el4
                5 -> R.drawable.el5
                6 -> R.drawable.el6
                else -> R.drawable.el1
            }

            binding.imageView.setImageResource(resourceId)

            if (isSelected) {
                animateSelection(true)
            } else {
                animateSelection(false)
            }

            binding.root.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        view.animate()
                            .scaleX(0.95f)
                            .scaleY(0.95f)
                            .setDuration(100)
                            .start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .start()
                    }
                }
                false
            }
        }

        private fun animateSelection(isSelected: Boolean) {
            if (isSelected) {
                binding.root.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(200)
                    .setInterpolator(OvershootInterpolator())
                    .start()

                val pulseAnimator = ObjectAnimator.ofFloat(binding.imageView, "alpha", 1f, 0.7f, 1f)
                pulseAnimator.duration = 600
                pulseAnimator.repeatCount = ObjectAnimator.INFINITE
                pulseAnimator.interpolator = AccelerateDecelerateInterpolator()
                pulseAnimator.start()

                binding.root.tag = pulseAnimator

            } else {
                binding.root.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .start()

                val pulseAnimator = binding.root.tag as? ObjectAnimator
                pulseAnimator?.cancel()
                binding.imageView.alpha = 1f
            }
        }

        private fun animateItemClick(action: () -> Unit) {
            isAnimating = true

            val scaleDown = ObjectAnimator.ofFloat(binding.root, "scaleX", 1f, 0.8f)
            val scaleDownY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1f, 0.8f)
            val scaleUp = ObjectAnimator.ofFloat(binding.root, "scaleX", 0.8f, 1.2f)
            val scaleUpY = ObjectAnimator.ofFloat(binding.root, "scaleY", 0.8f, 1.2f)
            val scaleNormal = ObjectAnimator.ofFloat(binding.root, "scaleX", 1.2f, 1f)
            val scaleNormalY = ObjectAnimator.ofFloat(binding.root, "scaleY", 1.2f, 1f)

            scaleDown.duration = 100
            scaleDownY.duration = 100
            scaleUp.duration = 150
            scaleUpY.duration = 150
            scaleNormal.duration = 100
            scaleNormalY.duration = 100

            val animatorSet = AnimatorSet()
            animatorSet.play(scaleDown).with(scaleDownY)
            animatorSet.play(scaleUp).with(scaleUpY).after(scaleDown)
            animatorSet.play(scaleNormal).with(scaleNormalY).after(scaleUp)

            animatorSet.interpolator = OvershootInterpolator()
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    isAnimating = false
                    action()
                }
            })

            animatorSet.start()
        }
    }
}