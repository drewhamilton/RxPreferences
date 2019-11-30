package drewhamilton.rxpreferences3.example.observe

import drewhamilton.rxpreferences3.RxPreferences
import io.reactivex.rxjava3.schedulers.Schedulers

import javax.inject.Inject

open class ExampleRepository @Inject constructor(protected val preferences: RxPreferences) {

    protected val scheduler
        get() = Schedulers.single()

    fun getExampleStringOnce() = preferences.getStringOnce(Keys.EXAMPLE_STRING, Defaults.STRING)
        .subscribeOn(scheduler)

    fun getExampleStringStream() = preferences.getStringStream(Keys.EXAMPLE_STRING, Defaults.STRING)
        .subscribeOn(scheduler)

    fun getExampleIntOnce() = preferences.getIntOnce(Keys.EXAMPLE_INT, Defaults.INT)
        .subscribeOn(scheduler)

    fun getExampleIntStream() = preferences.getIntStream(Keys.EXAMPLE_INT, Defaults.INT)
        .subscribeOn(scheduler)

    protected object Keys {
        const val EXAMPLE_STRING = "Example string"
        const val EXAMPLE_INT = "Example int"
    }

    protected object Defaults {
        const val STRING = ""
        const val INT = 0
    }
}
