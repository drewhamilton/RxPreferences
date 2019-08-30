package drewhamilton.rxpreferences3;

import android.content.SharedPreferences;
import io.reactivex.rxjava3.annotations.NonNull;

interface GetPreference<T> {

    @NonNull
    T invoke(@NonNull SharedPreferences preferences, @NonNull String key, @NonNull T defaultValue);
}
