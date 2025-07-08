package plinko.test.task.plinko.viewmodel.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import plinko.test.task.plinko.data.model.NavigationData

class SplashViewModel(
    private val remoteConfig: FirebaseRemoteConfig
) : ViewModel() {

    private val _navigationData = MutableLiveData<NavigationData>()
    val navigationData: LiveData<NavigationData> = _navigationData

    fun loadNavigation() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            val target = if (task.isSuccessful) {
                remoteConfig.getString("entryNavigation")
            } else "menu"

            val content = if (task.isSuccessful) {
                remoteConfig.getString("currentContent")
            } else ""

            _navigationData.postValue(NavigationData(target, content))
        }
    }
}