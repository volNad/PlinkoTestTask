package plinko.test.task.di

import android.content.Context
import androidx.core.bundle.bundleOf
import androidx.navigation.NavController
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import plinko.test.task.plinko.viewmodel.leaderboard.LeaderboardViewModel
import plinko.test.task.plinko.R
import plinko.test.task.plinko.viewmodel.settings.SettingsViewModel
import plinko.test.task.plinko.viewmodel.splash.SplashViewModel
import plinko.test.task.plinko.data.repository.GameRepository
import plinko.test.task.plinko.data.repository.LeaderboardRepository
import plinko.test.task.plinko.viewmodel.content.ContentViewModel
import plinko.test.task.plinko.viewmodel.game.GameViewModel

val appModule = module {

    single {
        androidContext().getSharedPreferences("plinko_prefs", Context.MODE_PRIVATE)
    }

    single { GameRepository(get()) }
    single { LeaderboardRepository(get()) }
    single {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
            )
            setDefaultsAsync(mapOf(
                "entryNavigation" to "menu",
                "currentContent" to ""
            ))
        }
    }

    single<Map<String, (NavController, String?) -> Unit>> {
        mapOf(
            "menu" to { navController, _ ->
                navController.navigate(R.id.action_splash_to_menu)
            },
            "content" to { navController, content ->
                val args = bundleOf("content" to content)
                navController.navigate(R.id.action_to_content, args)
            }
        )
    }

    viewModel { SplashViewModel(get()) }
    viewModel { GameViewModel(get()) }
    viewModel { LeaderboardViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { ContentViewModel() }
}