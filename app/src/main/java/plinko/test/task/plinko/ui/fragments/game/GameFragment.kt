package plinko.test.task.plinko.ui.fragments.game

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import plinko.test.task.plinko.data.model.GameItem
import plinko.test.task.plinko.databinding.FragmentGameBinding
import plinko.test.task.plinko.ui.fragments.game.adapter.GameGridAdapter
import plinko.test.task.plinko.util.GameAnimations
import plinko.test.task.plinko.viewmodel.game.GameViewModel

@SuppressLint("SetTextI18n")
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val gameViewModel: GameViewModel by viewModel()
    private lateinit var gameAdapter: GameGridAdapter
    private var gameTimer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        startGame()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            binding.btnBack.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    binding.btnBack.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {
                            findNavController().popBackStack()
                        }
                }
        }

        gameAdapter = GameGridAdapter { position ->
            gameViewModel.onItemClicked(position)
        }

        binding.recyclerViewGrid.apply {
            layoutManager = GridLayoutManager(context, 8)
            adapter = gameAdapter
            itemAnimator = null
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }


    private fun setupObservers() {
        gameViewModel.gameGrid.observe(viewLifecycleOwner) { grid ->
            gameAdapter.updateGrid(grid)
        }

        gameViewModel.score.observe(viewLifecycleOwner) { score ->
            animateScoreUpdate(score)
        }

        gameViewModel.timeLeft.observe(viewLifecycleOwner) { time ->
            binding.tvTimer.text = "Time: ${time}s"
            if (time <= 10) {
                binding.tvTimer.setTextColor(Color.RED)
                binding.tvTimer.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(300)
                    .withEndAction {
                        binding.tvTimer.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                    }
            }
        }

        gameViewModel.selectedPosition.observe(viewLifecycleOwner) { position ->
            gameAdapter.highlightSelectedItem(position)
        }

        gameViewModel.matchedItems.observe(viewLifecycleOwner) { positions ->
            animateMatchedItems(positions)
        }

        gameViewModel.newItems.observe(viewLifecycleOwner) { items ->
            animateNewItems(items)
        }
    }

    private fun animateScoreUpdate(score: Int) {
        GameAnimations.animateScoreUpdate(binding.tvScore, score)
    }

    private fun startGame() {
        gameViewModel.initializeGame()
        val gameTime = gameViewModel.timeLeft.value ?: 60L
        gameTimer = object : CountDownTimer(gameTime * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = "Time: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun animateMatchedItems(positions: List<Int>) {
        GameAnimations.animateMatchedItems(binding.recyclerViewGrid, positions)
    }

    private fun animateNewItems(items: List<Pair<Int, GameItem>>) {
        GameAnimations.animateNewItems(binding.recyclerViewGrid, items.map { it as Pair<Int, Any> })
    }

    private fun endGame() {
        gameTimer?.cancel()
        gameViewModel.saveFinalScore()
        val finalScore = gameViewModel.score.value ?: 0
        val highScore = gameViewModel.getHighScore()

        GameEndDialog(
            requireContext(),
            finalScore,
            highScore,
            onPlayAgain = {
                gameViewModel.resetGame()
                startGame()
            },
            onBackToMenu = {
                findNavController().popBackStack()
            }
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gameTimer?.cancel()
        _binding = null
    }
}