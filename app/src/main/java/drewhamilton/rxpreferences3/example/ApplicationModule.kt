package drewhamilton.rxpreferences3.example

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
object ApplicationModule {

    @Provides
    @JvmStatic fun applicationContext(application: ExampleApplication): Context = application.applicationContext
}
