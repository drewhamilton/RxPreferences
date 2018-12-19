package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.ObservableEmitter;
import io.reactivex.annotations.NonNull;

import java.util.Map;

final class RxAllPreferencesListener extends RxPreferenceListener<Map<String, ?>> {

  RxAllPreferencesListener(@NonNull ObservableEmitter<Map<String, ?>> emitter) {
    super("", emitter);
  }

  @Override
  Map<String, ?> getCurrentValue(SharedPreferences preferences) {
    return preferences.getAll();
  }

  @Override
  boolean shouldEmit(String key) {
    // Emit when any key changes:
    return true;
  }
}
