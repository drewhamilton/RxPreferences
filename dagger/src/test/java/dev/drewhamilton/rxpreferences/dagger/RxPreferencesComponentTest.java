package dev.drewhamilton.rxpreferences.dagger;

import android.content.SharedPreferences;
import dev.drewhamilton.rxpreferences.RxPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public final class RxPreferencesComponentTest {

    @Mock private SharedPreferences mockSharedPreferences;

    @Test
    public void factory_createsExpectedRxPreferencesInstance() {
        final RxPreferencesComponent component = RxPreferencesComponent.create(mockSharedPreferences);
        final RxPreferences rxPreferences = component.rxPreferences();

        final String testKey = "Test key";
        //noinspection ResultOfMethodCallIgnored: function return values are tested in main module
        rxPreferences.containsOnce(testKey).blockingGet();

        verify(mockSharedPreferences).contains(testKey);
        verifyNoMoreInteractions(mockSharedPreferences);
    }
}
