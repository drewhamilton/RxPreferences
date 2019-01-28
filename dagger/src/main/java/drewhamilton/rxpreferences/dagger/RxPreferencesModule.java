package drewhamilton.rxpreferences.dagger;

import android.content.SharedPreferences;
import dagger.Module;
import dagger.Provides;
import drewhamilton.rxpreferences.RxPreferences;

@Module
final class RxPreferencesModule {

  @Provides
  static RxPreferences rxPreferences(SharedPreferences sharedPreferences) {
    return new RxPreferences(sharedPreferences);
  }

  private RxPreferencesModule() {
    throw new UnsupportedOperationException();
  }
}
