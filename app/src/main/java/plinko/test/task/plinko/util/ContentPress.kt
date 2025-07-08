package plinko.test.task.plinko.util

import android.content.Context
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout

class ContentPress(
    private val contentViews: MutableList<WebView>,
    private val context: Context,
    private val layout: ConstraintLayout
) {

    fun contentCustomPress() {
        contentViews.lastOrNull()?.let { currentWebView ->
            if (currentWebView.canGoBack()) {
                currentWebView.goBack()
            } else {
                viewsStack()
            }
        } ?: viewsStack()
    }

    private fun viewsStack() {
        if (contentViews.size > 1) {
            removeLastView()
        } else {
            finishActivity()
        }
    }

    private fun removeLastView() {
        contentViews.lastOrNull()?.let { removeContentView(it) }
    }

    private fun removeContentView(webView: WebView) {
        layout.removeView(webView)
        webView.destroy()
        contentViews.removeAt(contentViews.size - 1)
    }

    private fun finishActivity() {
        (context as? ComponentActivity)?.finish()
    }
}