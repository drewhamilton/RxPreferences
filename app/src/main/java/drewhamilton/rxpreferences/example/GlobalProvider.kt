package drewhamilton.rxpreferences.example

import android.app.Application
import android.content.Context
import drewhamilton.rxpreferences.RxPreferences
import drewhamilton.rxpreferences.example.edit.MutableExampleRepository
import drewhamilton.rxpreferences.example.observe.ExampleRepository

class GlobalProvider private constructor(private val application: Application) {

  private val sharedPreferencesName = "drewhamilton.rxpreferences.example.SharedPreferences"

  val mutableExampleRepository
    get() = MutableExampleRepository(preferences)

  val exampleRepository
    get() = ExampleRepository(preferences)

  private val preferences
    get() = RxPreferences(application.getSharedPreferences())

  private fun Context.getSharedPreferences() =
      this.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

  companion object {
    val instance
      get() = privateInstance!!

    private var privateInstance: GlobalProvider? = null

    fun initialize(application: Application) {
      if (privateInstance == null) privateInstance = GlobalProvider(application)
      else throw IllegalStateException("Can't initialize GlobalProvider more than once")
    }
  }
}
