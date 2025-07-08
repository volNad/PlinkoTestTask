package plinko.test.task.plinko.data.clients

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import plinko.test.task.plinko.util.WebViewConfigurator
import plinko.test.task.plinko.util.newLogicReturn
import plinko.test.task.plinko.ui.fragments.content.ContentFragment
import plinko.test.task.plinko.viewmodel.content.ContentViewModel

@Suppress("DEPRECATION")
class ContentChromeClient(
    private val fragment: ContentFragment,
    private val viewModel: ContentViewModel,
    private val contentView: WebView,
    private val contentViews: MutableList<WebView>,
    private val layout: ConstraintLayout
) : WebChromeClient() {

    var customView: View? = null
    var customViewCallback: CustomViewCallback? = null
    var originalOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    var fullscreenContainer: FrameLayout? = null

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        viewModel.onProgressChanged(newProgress)
    }

    override fun onPermissionRequest(request: PermissionRequest) {
        fragment.permissionRequest = request
        viewModel.onPermissionRequest(request)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        fileChooserParams ?: return false
        val intent = fileChooserParams.createIntent()
        if (fileChooserParams.mode == FileChooserParams.MODE_OPEN_MULTIPLE) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        return try {
            viewModel.onShowFileChooser(intent, filePathCallback)
            true
        } catch (_: ActivityNotFoundException) {
            viewModel.onFileChooserResult(null)
            false
        }
    }

    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        val newWebView = WebView(fragment.requireContext())
        WebViewConfigurator.setup(newWebView, viewModel, fragment, contentViews, layout)
        return newLogicReturn(newWebView, resultMsg, layout)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        val activity = fragment.requireActivity() as? Activity ?: return

        originalOrientation = activity.requestedOrientation

        fullscreenContainer = FrameLayout(activity).apply {
            setBackgroundColor(Color.BLACK)
            addView(
                view, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }

        val decorView = activity.window.decorView as FrameLayout
        decorView.addView(
            fullscreenContainer, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        contentView.visibility = View.GONE

        customView = view
        customViewCallback = callback

        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    override fun onHideCustomView() {
        val activity = fragment.requireActivity() as? Activity ?: return

        fullscreenContainer?.let { container ->
            val decorView = activity.window.decorView as FrameLayout
            decorView.removeView(container)
        }
        fullscreenContainer = null

        contentView.visibility = View.VISIBLE

        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        activity.requestedOrientation = originalOrientation

        customViewCallback?.onCustomViewHidden()

        customView = null
        customViewCallback = null
    }
}