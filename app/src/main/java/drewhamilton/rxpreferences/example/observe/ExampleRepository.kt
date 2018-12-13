package drewhamilton.rxpreferences.example.observe

import drewhamilton.rxpreferences.RxPreferences
import io.reactivex.schedulers.Schedulers

open class ExampleRepository(protected val preferences: RxPreferences) {

  private val scheduler
    get() = Schedulers.single()

  fun getExampleString() = preferences.getString(Keys.EXAMPLE_STRING, Defaults.STRING)
      .subscribeOn(scheduler)!!

  fun observeExampleString() = preferences.observeString(Keys.EXAMPLE_STRING, Defaults.STRING)
      .subscribeOn(scheduler)!!

  fun getExampleInt() = preferences.getInt(Keys.EXAMPLE_INT, Defaults.INT)
      .subscribeOn(scheduler)!!

  fun observeExampleInt() = preferences.observeInt(Keys.EXAMPLE_INT, Defaults.INT)
      .subscribeOn(scheduler)!!

  protected class Keys private constructor() {

    companion object {
      const val EXAMPLE_STRING = "Example string"
      const val EXAMPLE_INT = "Example int"
    }

    init {
      throw UnsupportedOperationException()
    }
  }

  protected class Defaults private constructor() {

    companion object {
      const val STRING = ""
      const val INT = 0
    }

    init {
      throw UnsupportedOperationException()
    }
  }
}
