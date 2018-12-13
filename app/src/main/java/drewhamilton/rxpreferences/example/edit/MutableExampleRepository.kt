package drewhamilton.rxpreferences.example.edit

import drewhamilton.rxpreferences.RxPreferences
import drewhamilton.rxpreferences.edit
import drewhamilton.rxpreferences.example.observe.ExampleRepository

class MutableExampleRepository(preferences: RxPreferences) : ExampleRepository(preferences) {

  fun changeExampleString(value: String) = preferences.edit { putString(Keys.EXAMPLE_STRING, value) }
      .subscribeOn(scheduler)!!

  fun removeExampleString() = preferences.edit { remove(Keys.EXAMPLE_STRING) }
      .subscribeOn(scheduler)!!

  fun changeExampleInt(value: Int) = preferences.edit { putInt(Keys.EXAMPLE_INT, value) }
      .subscribeOn(scheduler)!!

  fun removeExampleInt() = preferences.edit { remove(Keys.EXAMPLE_INT) }
      .subscribeOn(scheduler)!!
}
