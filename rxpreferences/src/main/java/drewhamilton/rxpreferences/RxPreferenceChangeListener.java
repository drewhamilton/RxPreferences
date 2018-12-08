package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.ObservableEmitter;
import io.reactivex.annotations.NonNull;

final class RxPreferenceChangeListener<T> extends RxPreferenceListener<T> {

  private final T defaultValue;
  private final GetPreference<T> getPreference;

  RxPreferenceChangeListener(@NonNull String key, @NonNull ObservableEmitter<T> emitter, @NonNull T defaultValue,
      @NonNull GetPreference<T> getPreference) {
    super(key, emitter);
    this.defaultValue = defaultValue;
    this.getPreference = getPreference;
  }

  @Override
  T getCurrentValue(SharedPreferences preferences) {
    return getPreference.invoke(preferences, key, defaultValue);
  }
}
