package plinko.test.task.plinko.ui.fragments.content

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import plinko.test.task.plinko.viewmodel.content.ViewEvent
import plinko.test.task.plinko.util.WebViewConfigurator
import plinko.test.task.plinko.databinding.FragmentContentBinding
import plinko.test.task.plinko.util.ContentPress
import plinko.test.task.plinko.viewmodel.content.ContentViewModel

class ContentFragment : Fragment() {

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContentViewModel by viewModel()
    internal lateinit var permissionRequest: PermissionRequest
    var contentViews = mutableListOf<WebView>()
    lateinit var contentPress: ContentPress

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionRequest.grant(permissionRequest.resources)
            }
        }

    private val fileChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            var uris: Array<Uri>? = null
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData = result.data
                if (intentData?.clipData != null) {
                    val count = intentData.clipData!!.itemCount
                    uris = Array(count) { i ->
                        intentData.clipData!!.getItemAt(i).uri
                    }
                } else if (intentData?.data != null) {
                    uris = arrayOf(intentData.data!!)
                }
            }
            viewModel.onFileChooserResult(uris)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyAdvancedEdgeToEdge()
        val content = arguments?.getString("content") ?: ""
        WebViewConfigurator.setup(binding.contentView, viewModel, this, contentViews, binding.root)
        contentPress = ContentPress(contentViews, requireActivity(), binding.root)
        observeViewModel()
        binding.contentView.loadUrl(content)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            contentPress.contentCustomPress()
        }
    }

    override fun onResume() {
        super.onResume()
        contentViews.lastOrNull()?.onResume().also {
            CookieManager.getInstance().flush()
        }
    }

    override fun onPause() {
        super.onPause()
        contentViews.lastOrNull()?.onPause().also {
            CookieManager.getInstance().flush()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentViews.lastOrNull()?.saveState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.contentView.destroy()
        _binding = null
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isProgressVisible.collectLatest { isVisible ->
                binding.contentViewProgress.isVisible = isVisible
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.progress.collectLatest { progress ->
                ObjectAnimator.ofInt(binding.contentViewProgress, "progress", progress)
                    .apply {
                        duration = 300
                        interpolator = DecelerateInterpolator()
                    }.start()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is ViewEvent.StartActivity -> {
                        try {
                            startActivity(event.intent)
                        } catch (_: ActivityNotFoundException) {
                        }
                    }

                    is ViewEvent.RequestCameraPermission -> handleCameraPermission(event.request)
                    is ViewEvent.ShowFileChooser -> fileChooserLauncher.launch(event.intent)
                }
            }
        }
    }

    private fun handleCameraPermission(request: PermissionRequest) {
        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                cameraPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            request.grant(request.resources)
        } else {
            cameraPermissionLauncher.launch(cameraPermission)
        }
    }

    private fun applyAdvancedEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomInset = maxOf(imeInsets.bottom, systemBarsInsets.bottom)

            view.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                bottomInset
            )
            insets
        }
    }
}