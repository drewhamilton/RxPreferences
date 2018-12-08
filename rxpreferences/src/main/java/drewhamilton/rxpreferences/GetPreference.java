package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.annotations.NonNull;

interface GetPreference<T> {

  @NonNull
  T invoke(@NonNull SharedPreferences preferences, @NonNull String key, @NonNull T defaultValue);
}
