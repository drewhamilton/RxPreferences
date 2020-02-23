package dev.drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;

final class RxPreferenceContainsListener extends RxPreferenceListener<Boolean> {

    RxPreferenceContainsListener(@NonNull String key, @NonNull ObservableEmitter<Boolean> emitter) {
        super(key, emitter);
    }

    @Override
    Boolean getCurrentValue(SharedPreferences preferences) {
        return preferences.contains(key);
    }
}
