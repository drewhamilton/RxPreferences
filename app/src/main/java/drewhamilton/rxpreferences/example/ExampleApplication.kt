package drewhamilton.rxpreferences.example

import android.app.Application
import org.koin.android.ext.android.startKoin

class ExampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    startKoin(this, listOf(
        Modules.applicationModule,
        Modules.persistenceModule,
        Modules.observeModule,
        Modules.editModule
    ))
  }
}
