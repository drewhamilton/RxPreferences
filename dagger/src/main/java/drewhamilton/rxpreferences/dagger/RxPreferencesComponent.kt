package drewhamilton.rxpreferences.dagger

import android.content.SharedPreferences
import dagger.BindsInstance
import dagger.Component
import drewhamilton.rxpreferences.RxPreferences

/**
 * A Dagger component that provides an [RxPreferences] instance for a given [SharedPreferences] instance.
 */
@Component(modules = [RxPreferencesModule::class])
interface RxPreferencesComponent {

  /**
   * @return an [RxPreferences] wrapping the [SharedPreferences] instance given to the [Factory]
   */
  fun rxPreferences(): RxPreferences

  /**
   * A factory for this component.
   */
  @Component.Factory interface Factory {

    /**
     * @return a new [RxPreferencesComponent]
     */
    fun create(@BindsInstance sharedPreferences: SharedPreferences): RxPreferencesComponent
  }

  companion object {

    /**
     * @return a concrete [RxPreferencesComponent] instance
     */
    fun create(sharedPreferences: SharedPreferences): RxPreferencesComponent {
      return DaggerRxPreferencesComponent.factory().create(sharedPreferences)
    }
  }
}
