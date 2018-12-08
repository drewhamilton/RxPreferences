package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.ObservableEmitter;
import io.reactivex.annotations.NonNull;

abstract class RxPreferenceListener<T> implements SharedPreferences.OnSharedPreferenceChangeListener {

  final String key;
  private final ObservableEmitter<T> emitter;

  RxPreferenceListener(@NonNull String key, @NonNull ObservableEmitter<T> emitter) {
    this.key = key;
    this.emitter = emitter;
  }

  @Override
  public final void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
    if (this.key.equals(key)) {
      try {
        emitter.onNext(getCurrentValue(preferences));
      } catch (Throwable error) {
        emitter.onError(error);
      }
    }
  }

  abstract T getCurrentValue(SharedPreferences preferences);
}
