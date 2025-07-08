package plinko.test.task.plinko.viewmodel.content

import android.content.Intent
import android.net.Uri
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentViewModel : ViewModel() {

    private val _progress = MutableStateFlow(0)
    val progress = _progress.asStateFlow()

    private val _isProgressVisible = MutableStateFlow(false)
    val isProgressVisible = _isProgressVisible.asStateFlow()

    private val _events = MutableSharedFlow<ViewEvent>()
    val events = _events.asSharedFlow()

    var fileChooserCallback: ValueCallback<Array<Uri>>? = null
        private set


    fun onProgressChanged(newProgress: Int) {
        _progress.value = newProgress
        _isProgressVisible.value = newProgress < 100
    }

    fun onUrlLoading(url: String): Boolean {
        return when {
            url.startsWith("http") -> false
            else -> {
                try {
                    val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    viewModelScope.launch { _events.emit(ViewEvent.StartActivity(intent)) }
                } catch (_: Exception) { }
                true
            }
        }
    }

    fun onDownloadStart(url: String) {
        viewModelScope.launch {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            _events.emit(ViewEvent.StartActivity(intent))
        }
    }

    fun onPermissionRequest(request: PermissionRequest) {
        viewModelScope.launch { _events.emit(ViewEvent.RequestCameraPermission(request)) }
    }

    fun onShowFileChooser(intent: Intent, callback: ValueCallback<Array<Uri>>?) {
        fileChooserCallback = callback
        viewModelScope.launch { _events.emit(ViewEvent.ShowFileChooser(intent)) }
    }

    fun onFileChooserResult(resultUris: Array<Uri>?) {
        fileChooserCallback?.onReceiveValue(resultUris)
        fileChooserCallback = null
    }
}