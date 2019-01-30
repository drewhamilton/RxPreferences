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
public final class RxPreferencesComponentTest {

  @Mock
  private SharedPreferences mockSharedPreferences;

  @Test
  public void builder_withSharedPreferences_createsExpectedRxPreferencesInstance() {
    final RxPreferencesComponent component = RxPreferencesComponent.Companion.builder()
        .sharedPreferences(mockSharedPreferences)
        .build();
    final RxPreferences rxPreferences = component.rxPreferences();

    final String testKey = "Test key";
    //noinspection ResultOfMethodCallIgnored
    rxPreferences.contains(testKey).blockingGet();

    verify(mockSharedPreferences).contains(testKey);
    verifyNoMoreInteractions(mockSharedPreferences);
  }

  @Test(expected = IllegalStateException.class)
  public void builder_withoutSharedPreferences_throwsExceptionOnBuild() {
    RxPreferencesComponent.Companion.builder()
        .build();
  }
}
