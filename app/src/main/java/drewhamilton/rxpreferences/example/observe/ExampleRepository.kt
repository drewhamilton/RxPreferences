package drewhamilton.rxpreferences.example.observe

import drewhamilton.rxpreferences.RxPreferences
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

open class ExampleRepository @Inject constructor(protected val preferences: RxPreferences) {

  protected val scheduler
    get() = Schedulers.single()

  fun getExampleString() = preferences.getString(Keys.EXAMPLE_STRING, Defaults.STRING)
      .subscribeOn(scheduler)

  fun observeExampleString() = preferences.observeString(Keys.EXAMPLE_STRING, Defaults.STRING)
      .subscribeOn(scheduler)!!

  fun getExampleInt() = preferences.getInt(Keys.EXAMPLE_INT, Defaults.INT)
      .subscribeOn(scheduler)

  fun observeExampleInt() = preferences.observeInt(Keys.EXAMPLE_INT, Defaults.INT)
      .subscribeOn(scheduler)!!

  protected object Keys {
    const val EXAMPLE_STRING = "Example string"
    const val EXAMPLE_INT = "Example int"
  }

  protected object Defaults {
    const val STRING = ""
    const val INT = 0
  }
}
