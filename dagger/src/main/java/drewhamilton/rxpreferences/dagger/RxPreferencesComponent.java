package drewhamilton.rxpreferences.dagger;

import android.content.SharedPreferences;
import dagger.BindsInstance;
import dagger.Component;
import drewhamilton.rxpreferences.RxPreferences;

@Component(modules = RxPreferencesModule.class)
public interface RxPreferencesComponent {

  RxPreferences rxPreferences();

  @Component.Builder
  interface Builder {

    @BindsInstance
    Builder sharedPreferences(SharedPreferences sharedPreferences);

    RxPreferencesComponent build();
  }

  final class Companion {

    public static Builder builder() {
      return DaggerRxPreferencesComponent.builder();
    }

    private Companion() {
      throw new UnsupportedOperationException();
    }
  }
}
