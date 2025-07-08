package plinko.test.task.plinko.ui.fragments.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import plinko.test.task.plinko.R
import plinko.test.task.plinko.databinding.FragmentMenuBinding
import kotlin.system.exitProcess

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnPlay.setOnClickListener {
            findNavController().navigate(R.id.action_menu_to_game)
        }

        binding.btnLeaderboard.setOnClickListener {
            findNavController().navigate(R.id.action_menu_to_leaderboard)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_menu_to_settings)
        }

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.action_menu_to_about)
        }

        binding.btnExit.setOnClickListener {
            requireActivity().finish()
            exitProcess(0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}