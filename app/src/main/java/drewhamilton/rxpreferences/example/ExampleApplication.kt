package drewhamilton.rxpreferences.example

import android.app.Application

class ExampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    GlobalProvider.initialize(this)
  }
}
