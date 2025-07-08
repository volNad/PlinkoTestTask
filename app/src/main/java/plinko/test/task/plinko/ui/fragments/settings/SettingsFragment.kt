package plinko.test.task.plinko.ui.fragments.settings

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import plinko.test.task.plinko.viewmodel.settings.SettingsViewModel
import plinko.test.task.plinko.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        val timerOptions = arrayOf("30s", "60s", "90s", "120s")
        val timerAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, timerOptions)
        timerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerTimer.adapter = timerAdapter

        binding.spinnerTimer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedTime = when (position) {
                    0 -> 30
                    1 -> 60
                    2 -> 90
                    3 -> 120
                    else -> 60
                }
                settingsViewModel.setGameTime(selectedTime)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val multiplier = when (checkedId) {
                plinko.test.task.plinko.R.id.radio_1x -> 1
                plinko.test.task.plinko.R.id.radio_2x -> 2
                plinko.test.task.plinko.R.id.radio_3x -> 3
                else -> 1
            }
            settingsViewModel.setScoreMultiplier(multiplier)
        }

        binding.btnRateUs.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=${context?.packageName}".toUri()
            )
            startActivity(intent)
        }

        binding.btnShareUs.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out this awesome Plinko game! https://play.google.com/store/apps/details?id=${context?.packageName}"
                )
            }
            startActivity(Intent.createChooser(shareIntent, "Share Plinko Game"))
        }
    }

    private fun setupObservers() {
        settingsViewModel.gameTime.observe(viewLifecycleOwner) { time ->
            val position = when (time) {
                30 -> 0
                60 -> 1
                90 -> 2
                120 -> 3
                else -> 1
            }
            binding.spinnerTimer.setSelection(position)
        }

        settingsViewModel.scoreMultiplier.observe(viewLifecycleOwner) { multiplier ->
            val radioId = when (multiplier) {
                1 -> plinko.test.task.plinko.R.id.radio_1x
                2 -> plinko.test.task.plinko.R.id.radio_2x
                3 -> plinko.test.task.plinko.R.id.radio_3x
                else -> plinko.test.task.plinko.R.id.radio_1x
            }
            binding.radioGroup.check(radioId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}