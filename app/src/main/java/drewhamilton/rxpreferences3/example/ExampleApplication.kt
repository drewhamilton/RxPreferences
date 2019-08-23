package drewhamilton.rxpreferences3.example

import android.app.Application

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        _applicationComponent = ApplicationComponent.create(this)
    }

    companion object {
        val applicationComponent: ApplicationComponent
            get() = _applicationComponent

        @Suppress("ObjectPropertyName")
        private lateinit var _applicationComponent: ApplicationComponent
    }
}
