package plinko.test.task.plinko.util

import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import plinko.test.task.plinko.data.clients.ContentChromeClient
import plinko.test.task.plinko.data.clients.ContentViewClient
import plinko.test.task.plinko.ui.fragments.content.ContentFragment
import plinko.test.task.plinko.viewmodel.content.ContentViewModel

object WebViewConfigurator {

    fun setup(
        webView: WebView,
        viewModel: ContentViewModel,
        fragment: ContentFragment,
        contentViews: MutableList<WebView>,
        layout: ConstraintLayout
    ) {
        webView.apply {
            applyCustomSettings()
            webViewClient = ContentViewClient(viewModel)
            webChromeClient = ContentChromeClient(fragment, viewModel, this, contentViews, layout)
            setDownloadListener { url, _, _, _, _ -> viewModel.onDownloadStart(url) }
            contentViews.add(this)
        }
    }
}