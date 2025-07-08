package plinko.test.task.plinko.ui.fragments.game

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.animation.OvershootInterpolator
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import plinko.test.task.plinko.R
import plinko.test.task.plinko.databinding.GameEndDialogBinding

class GameEndDialog(
    private val context: Context,
    private val finalScore: Int,
    private val highScore: Int,
    private val onPlayAgain: () -> Unit,
    private val onBackToMenu: () -> Unit
) {
    private lateinit var dialog: AlertDialog

    @SuppressLint("SetTextI18n")
    fun show() {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.game_end_dialog, null)

        val binding = GameEndDialogBinding.bind(dialogView)

        binding.tvFinalScore.text = "Final Score: $finalScore"
        binding.tvHighScore.text = "High Score: $highScore"

        if (finalScore > highScore) {
            binding.tvGameOverTitle.text = "NEW HIGH SCORE!"
            binding.tvGameOverTitle.setTextColor(Color.YELLOW)
        } else {
            binding.tvGameOverTitle.text = "Game Over"
        }

        binding.btnPlayAgain.setOnClickListener {
            animateButtonClick(binding.btnPlayAgain) {
                dialog.dismiss()
                onPlayAgain()
            }
        }

        binding.btnBackToMenu.setOnClickListener {
            animateButtonClick(binding.btnBackToMenu) {
                dialog.dismiss()
                onBackToMenu()
            }
        }

        dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        dialogView.alpha = 0f
        dialogView.scaleX = 0.7f
        dialogView.scaleY = 0.7f
        dialogView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(400)
            .setInterpolator(OvershootInterpolator())
            .start()
    }

    private fun animateButtonClick(button: Button, action: () -> Unit) {
        button.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .withEndAction {
                button.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .withEndAction(action)
            }
    }
}