package dev.drewhamilton.rxpreferences.dagger

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dev.drewhamilton.rxpreferences.RxPreferences

@Module
internal object RxPreferencesModule {

    @JvmStatic
    @Provides
    internal fun rxPreferences(sharedPreferences: SharedPreferences) = RxPreferences(sharedPreferences)
}
