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
   * @return an [RxPreferences] wrapping the [SharedPreferences] given to the [Builder]
   */
  fun rxPreferences(): RxPreferences

  /**
   * A builder for this component.
   */
  @Component.Builder
  interface Builder {

    /**
     * @param sharedPreferences The preferences to be wrapped by an [RxPreferences] instance.
     * @return this builder
     */
    @BindsInstance
    fun sharedPreferences(sharedPreferences: SharedPreferences): Builder

    /**
     * @return a new [RxPreferencesComponent]
     * @throws IllegalStateException if [sharedPreferences] has not been set
     */
    fun build(): RxPreferencesComponent
  }

  companion object {

    /**
     * @return a concrete [Builder] instance
     */
    fun builder(): Builder {
      return DaggerRxPreferencesComponent.builder()
    }
  }
}
