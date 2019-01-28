package drewhamilton.rxpreferences.dagger;

import android.content.SharedPreferences;
import drewhamilton.rxpreferences.RxPreferences;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public final class RxPreferencesModuleTest {

  @Mock private SharedPreferences mockSharedPreferences;

  @Test
  public void rxPreferences_wrapsProvidedSharedPreferences() {
    final RxPreferences rxPreferences = RxPreferencesModule.rxPreferences(mockSharedPreferences);

    final String testKey = "Test key";
    //noinspection ResultOfMethodCallIgnored
    rxPreferences.contains(testKey).blockingGet();

    verify(mockSharedPreferences).contains(testKey);
    verifyNoMoreInteractions(mockSharedPreferences);
  }
}
