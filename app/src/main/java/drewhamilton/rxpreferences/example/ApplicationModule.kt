package drewhamilton.rxpreferences.example

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
object ApplicationModule {

  @JvmStatic
  @Provides
  fun applicationContext(application: ExampleApplication): Context = application.applicationContext
}
