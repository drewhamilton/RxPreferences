package drewhamilton.rxpreferences.dagger;

import android.content.SharedPreferences;
import dagger.BindsInstance;
import dagger.Component;
import drewhamilton.rxpreferences.RxPreferences;

/**
 * A Dagger component that provides an {@link RxPreferences} instance for a given {@link SharedPreferences} instance.
 */
@Component(modules = RxPreferencesModule.class)
public interface RxPreferencesComponent {

  /**
   * @return the {@link RxPreferences} wrapping the {@link SharedPreferences} given to the {@link Builder}
   */
  RxPreferences rxPreferences();

  /**
   * A builder for this component.
   */
  @Component.Builder
  interface Builder {

    /**
     * @param sharedPreferences The preferences to be wrapped by an {@link RxPreferences} instance.
     * @return this builder
     */
    @BindsInstance
    Builder sharedPreferences(SharedPreferences sharedPreferences);

    /**
     * @return a new {@link RxPreferencesComponent}
     * @throws IllegalStateException if {@link #sharedPreferences(SharedPreferences)} has not been set
     */
    RxPreferencesComponent build();
  }

  /**
   * Wrapper for the static {@link #builder()} method to remain callable from Java 6
   */
  final class Companion {

    /**
     * @return a concrete {@link Builder} instance
     */
    public static Builder builder() {
      return DaggerRxPreferencesComponent.builder();
    }

    private Companion() {
      throw new UnsupportedOperationException();
    }
  }
}
