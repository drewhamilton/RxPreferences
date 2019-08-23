package drewhamilton.rxpreferences3;

import android.content.SharedPreferences;
import io.reactivex.ObservableEmitter;
import io.reactivex.annotations.NonNull;

final class RxPreferenceContainsListener extends RxPreferenceListener<Boolean> {

    RxPreferenceContainsListener(@NonNull String key, @NonNull ObservableEmitter<Boolean> emitter) {
        super(key, emitter);
    }

    @Override
    Boolean getCurrentValue(SharedPreferences preferences) {
        return preferences.contains(key);
    }
}
