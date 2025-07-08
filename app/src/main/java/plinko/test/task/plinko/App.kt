package plinko.test.task.plinko

import android.app.Application
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import plinko.test.task.di.appModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            androidContext(this@App)
            modules(
                listOf(appModule)
            )
        }
    }
}