package drewhamilton.rxpreferences.example.edit

import drewhamilton.rxpreferences.RxPreferences
import drewhamilton.rxpreferences.edit
import drewhamilton.rxpreferences.example.observe.ExampleRepository

class MutableExampleRepository(preferences: RxPreferences) : ExampleRepository(preferences) {

  fun changeExampleString(value: String) = preferences.edit { putString(Keys.EXAMPLE_STRING, value) }

  fun removeExampleString() = preferences.edit { remove(Keys.EXAMPLE_STRING) }

  fun changeExampleInt(value: Int) = preferences.edit { putInt(Keys.EXAMPLE_INT, value) }

  fun removeExampleInt() = preferences.edit { remove(Keys.EXAMPLE_INT) }
}
