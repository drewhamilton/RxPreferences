package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class RxPreferencesTest {

  @Mock private SharedPreferences mockSharedPreferences;
  @Mock private SharedPreferences.Editor mockSharedPreferencesEditor;

  @InjectMocks private RxPreferences rxPreferences;

  private TestScheduler testScheduler;

  private final CompositeDisposable disposable = new CompositeDisposable();

  @Before
  public void setUp() {
    when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);
//    when(mockSharedPreferencesEditor.putBoolean(any(), anyBoolean())).thenReturn(mockSharedPreferencesEditor);
//    when(mockSharedPreferencesEditor.putFloat(any(), anyFloat())).thenReturn(mockSharedPreferencesEditor);
//    when(mockSharedPreferencesEditor.putInt(any(), anyInt())).thenReturn(mockSharedPreferencesEditor);
//    when(mockSharedPreferencesEditor.putLong(any(), anyLong())).thenReturn(mockSharedPreferencesEditor);
    when(mockSharedPreferencesEditor.putString(any(), anyString())).thenReturn(mockSharedPreferencesEditor);
//    when(mockSharedPreferencesEditor.remove(any())).thenReturn(mockSharedPreferencesEditor);
//    when(mockSharedPreferencesEditor.clear()).thenReturn(mockSharedPreferencesEditor);
    when(mockSharedPreferencesEditor.commit()).thenReturn(true);

    testScheduler = new TestScheduler();
  }

  @After
  public void tearDown() {
    disposable.clear();
  }

  @Test
  public void putString_callsExpectedMethods() {
    final String testKey = "Test key";
    final String testValue = "Test value";
    disposable.add(rxPreferences.edit()
        .putString(testKey, testValue)
        .commit()
        .subscribeOn(testScheduler)
        .subscribe());

    verify(mockSharedPreferences).edit();
    verifyNoMoreInteractions(mockSharedPreferences);
    verify(mockSharedPreferencesEditor).putString(testKey, testValue);
    verifyNoMoreInteractions(mockSharedPreferencesEditor);

    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
    verifyNoMoreInteractions(mockSharedPreferences);
    verify(mockSharedPreferencesEditor).commit();
    verifyNoMoreInteractions(mockSharedPreferencesEditor);
  }
}
