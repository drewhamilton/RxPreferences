package drewhamilton.rxpreferences3.dagger

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import drewhamilton.rxpreferences3.RxPreferences

@Module
internal object RxPreferencesModule {

    @JvmStatic
    @Provides
    internal fun rxPreferences(sharedPreferences: SharedPreferences) = RxPreferences(sharedPreferences)
}
