package plinko.test.task.plinko.ui.fragments.leaderboard

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import plinko.test.task.plinko.ui.fragments.leaderboard.adapter.LeaderboardAdapter
import plinko.test.task.plinko.viewmodel.leaderboard.LeaderboardViewModel
import plinko.test.task.plinko.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private val leaderboardViewModel: LeaderboardViewModel by viewModel()
    private lateinit var leaderboardAdapter: LeaderboardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        leaderboardViewModel.loadLeaderboard()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        leaderboardAdapter = LeaderboardAdapter()
        binding.recyclerViewLeaderboard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leaderboardAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.bottom = 12.dpToPx()
                }
            })
        }
    }

    private fun setupObservers() {
        leaderboardViewModel.leaderboard.observe(viewLifecycleOwner) { players ->
            leaderboardAdapter.updateLeaderboard(players)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}