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

  static Builder builder() {
    return DaggerRxPreferencesComponent.builder();
  }
}
