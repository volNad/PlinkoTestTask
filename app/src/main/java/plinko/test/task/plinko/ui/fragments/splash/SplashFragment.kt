package plinko.test.task.plinko.ui.fragments.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import plinko.test.task.plinko.R
import plinko.test.task.plinko.databinding.FragmentSplashBinding
import plinko.test.task.plinko.viewmodel.splash.SplashViewModel

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SplashViewModel by viewModel()
    private val navigationMap: Map<String, (NavController, String?) -> Unit> by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate_infinite)
        binding.loadingIcon.startAnimation(rotateAnimation)

        viewModel.navigationData.observe(viewLifecycleOwner) { navData ->
            val navController = findNavController()
            val navigateAction = navigationMap[navData.targetKey] ?: navigationMap["menu"]!!
            navigateAction(navController, navData.currentContent)
        }

        viewModel.loadNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}