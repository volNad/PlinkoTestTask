package plinko.test.task.plinko.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

object GameAnimations {

    @SuppressLint("SetTextI18n")
    fun animateScoreUpdate(tvScore: TextView, score: Int) {
        tvScore.text = "Score: $score"

        val colorAnimator = ValueAnimator.ofArgb(Color.WHITE, Color.YELLOW, Color.WHITE)
        colorAnimator.duration = 500
        colorAnimator.addUpdateListener { animator ->
            tvScore.setTextColor(animator.animatedValue as Int)
        }

        val scaleAnimator = tvScore.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(200)
            .withEndAction {
                tvScore.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start()
            }

        colorAnimator.start()
        scaleAnimator.start()
    }

    fun animateMatchedItems(recyclerView: RecyclerView, positions: List<Int>) {
        val animatorSet = AnimatorSet()
        val animations = positions.mapNotNull { position ->
            val holder = recyclerView.findViewHolderForAdapterPosition(position)
            holder?.itemView?.let { itemView ->
                val popUp = ObjectAnimator.ofFloat(itemView, "translationY", 0f, -30f)
                popUp.duration = 200
                popUp.interpolator = DecelerateInterpolator()

                val scaleX = ObjectAnimator.ofFloat(itemView, "scaleX", 1f, 1.2f, 1f)
                val scaleY = ObjectAnimator.ofFloat(itemView, "scaleY", 1f, 1.2f, 1f)
                scaleX.duration = 250
                scaleY.duration = 250
                scaleX.interpolator = OvershootInterpolator()
                scaleY.interpolator = OvershootInterpolator()

                val fadeOut = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f)
                val settleDown = ObjectAnimator.ofFloat(itemView, "translationY", -30f, 0f)
                settleDown.duration = 300
                fadeOut.duration = 300
                fadeOut.startDelay = 100
                settleDown.interpolator = AccelerateDecelerateInterpolator()

                AnimatorSet().apply {
                    play(popUp)
                        .before(scaleX).with(scaleY)
                    play(fadeOut).with(settleDown).after(scaleX)
                }
            }
        }

        if (animations.isNotEmpty()) {
            animatorSet.playTogether(animations)
            animatorSet.start()
        }
    }

    fun animateNewItems(recyclerView: RecyclerView, items: List<Pair<Int, Any>>) {
        items.forEach { (position, _) ->
            val holder = recyclerView.findViewHolderForAdapterPosition(position)
            holder?.itemView?.let { itemView ->
                itemView.alpha = 0f
                itemView.translationY = -200f
                itemView.scaleX = 0.3f
                itemView.scaleY = 0.3f

                itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setInterpolator(BounceInterpolator())
                    .start()
            }
        }
    }

}