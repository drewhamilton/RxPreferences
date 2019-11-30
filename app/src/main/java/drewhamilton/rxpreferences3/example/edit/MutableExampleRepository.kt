package drewhamilton.rxpreferences3.example.edit

import drewhamilton.rxpreferences3.RxPreferences
import drewhamilton.rxpreferences3.edit
import drewhamilton.rxpreferences3.example.observe.ExampleRepository
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
