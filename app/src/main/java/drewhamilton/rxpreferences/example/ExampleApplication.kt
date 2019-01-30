package drewhamilton.rxpreferences.example

import android.app.Application

class ExampleApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    _applicationComponent = ApplicationComponent.builder()
        .application(this)
        .build()
  }

  companion object {
    val applicationComponent: ApplicationComponent
      get() = _applicationComponent

    @Suppress("ObjectPropertyName")
    private lateinit var _applicationComponent: ApplicationComponent
  }
}
