package dev.drewhamilton.rxpreferences.example.edit

import dev.drewhamilton.rxpreferences.RxPreferences
import dev.drewhamilton.rxpreferences.edit
import dev.drewhamilton.rxpreferences.example.observe.ExampleRepository
import javax.inject.Inject

class MutableExampleRepository @Inject constructor(preferences: RxPreferences) : ExampleRepository(preferences) {

    fun changeExampleValues(string: String, int: Int) = preferences.edit {
        putString(Keys.EXAMPLE_STRING, string)
        putInt(Keys.EXAMPLE_INT, int)
    }.subscribeOn(scheduler)

    fun removeExampleValues() = preferences.edit {
        remove(Keys.EXAMPLE_STRING)
        remove(Keys.EXAMPLE_INT)
    }.subscribeOn(scheduler)
}
