package plinko.test.task.plinko.util

import android.annotation.SuppressLint
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout

@Suppress("DEPRECATION")
@SuppressLint("SetJavaScriptEnabled")
fun WebView.applyCustomSettings() {
    layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    setLayerType(View.LAYER_TYPE_HARDWARE, null)

    CookieManager.getInstance().setAcceptCookie(true)
    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

    isFocusable = true
    isFocusableInTouchMode = true
    isSaveEnabled = true

    settings.apply {
        javaScriptEnabled = true
        javaScriptCanOpenWindowsAutomatically = true
        domStorageEnabled = true
        databaseEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
        allowFileAccess = true
        allowContentAccess = true
        loadsImagesAutomatically = true
        cacheMode = WebSettings.LOAD_DEFAULT
        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        mediaPlaybackRequiresUserGesture = false
        setSupportMultipleWindows(true)

        builtInZoomControls = true
        displayZoomControls = false

        userAgentString = userAgentString.replace("; wv", "")
    }
}

fun newLogicReturn(
    webView: WebView,
    message: Message?,
    layout: ConstraintLayout
): Boolean {

    layout.addView(webView)
    (message?.obj as? WebView.WebViewTransport)?.apply {
        this.webView = webView
        message.sendToTarget()
    }

    return true
}