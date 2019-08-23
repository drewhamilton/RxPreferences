package drewhamilton.rxpreferences3.example

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import drewhamilton.rxpreferences3.dagger.RxPreferencesComponent

@Module
object PersistenceModule {

    private const val sharedPreferencesName = "drewhamilton.rxpreferences.example.SharedPreferences"

    private val ExampleApplication.sharedPreferences
        get() = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    @JvmStatic
    @Provides
    fun preferencesComponent(application: ExampleApplication) =
        RxPreferencesComponent.create(application.sharedPreferences)

    @JvmStatic
    @Provides
    @Reusable
    fun preferences(preferencesComponent: RxPreferencesComponent) = preferencesComponent.rxPreferences()
}
