package plinko.test.task.plinko.data.clients

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import plinko.test.task.plinko.viewmodel.content.ContentViewModel

class ContentViewClient(
    private val viewModel: ContentViewModel
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return viewModel.onUrlLoading(request.url.toString())
    }
}