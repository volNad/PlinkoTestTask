package plinko.test.task.plinko.viewmodel.content

import android.content.Intent
import android.webkit.PermissionRequest

sealed class ViewEvent {
    data class StartActivity(val intent: Intent) : ViewEvent()
    data class RequestCameraPermission(val request: PermissionRequest) : ViewEvent()
    data class ShowFileChooser(val intent: Intent) : ViewEvent()
}