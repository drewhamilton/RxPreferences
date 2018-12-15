package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public final class RxPreferencesTest {

  @Mock private SharedPreferences mockSharedPreferences;
  @Mock private SharedPreferences.Editor mockSharedPreferencesEditor;

  @InjectMocks private RxPreferences rxPreferences;

  private TestScheduler testScheduler;

  private final CompositeDisposable subscriptions = new CompositeDisposable();

  @Before
  public void setUp() {
    when(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor);
    when(mockSharedPreferencesEditor.commit()).thenReturn(true);

    testScheduler = new TestScheduler();
  }

  @After
  public void tearDown() {
    subscriptions.clear();
  }

  private void advanceScheduler() {
    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
  }

  //region Editor
  //region put
  @Test
  public void putString_putsAndCommitsWhenExpected() {
    final String testValue = "Test value";
    testPutMethod(PutType.STRING, testValue);
  }

  @Test
  public void putStringSet_putsAndCommitsWhenExpected() {
    final Set<String> testValue = Collections.singleton("Test value");
    testPutMethod(PutType.STRING_SET, testValue);
  }

  @Test
  public void putInt_putsAndCommitsWhenExpected() {
    final int testValue = 87245;
    testPutMethod(PutType.INT, testValue);
  }

  @Test
  public void putLong_putsAndCommitsWhenExpected() {
    final long testValue = 3141235425L;
    testPutMethod(PutType.LONG, testValue);
  }

  @Test
  public void putFloat_putsAndCommitsWhenExpected() {
    final float testValue = 141.1341f;
    testPutMethod(PutType.FLOAT, testValue);
  }

  @Test
  public void putBoolean_putsAndCommitsWhenExpected() {
    final boolean testValue = true;
    testPutMethod(PutType.BOOLEAN, testValue);
  }

  private void testPutMethod(PutType putType, Object value) {
    assertNotNull(putType);
    assertNotNull(value);
    final String valueTypeFailureMessage = "verifyPut with type " + putType + " requires a value of type "
        + putType.valueClass.getSimpleName() + ", but the provided value was of type "
        + value.getClass().getSimpleName();
    assertTrue(valueTypeFailureMessage, putType.valueClass.isAssignableFrom(value.getClass()));

    final String testKey = "Test " + putType + " key";

    final Disposable subscription = putOnTestScheduler(putType, testKey, value);
    subscriptions.add(subscription);

    verifyPutBeforeSubscribe(putType, testKey, value, subscription);

    advanceScheduler();
    verifyCommitAfterSubscribe(subscription);
  }

  private Disposable putOnTestScheduler(PutType putType, String key, Object value) {
    return put(rxPreferences.edit(), putType, key, value)
        .commit()
        .subscribeOn(testScheduler)
        .subscribe();
  }

  private void verifyPutBeforeSubscribe(PutType putType, String key, Object value, Disposable subscription) {
    // Before subscribing, edits are pushed to the internal editor, but not committed:
    verify(mockSharedPreferences).edit();
    verifyNoMoreInteractions(mockSharedPreferences);
    verifyPut(mockSharedPreferencesEditor, putType, key, value);
    verifyNoMoreInteractions(mockSharedPreferencesEditor);
    assertFalse(subscription.isDisposed());
  }

  private void verifyCommitAfterSubscribe(Disposable subscription) {
    // After subscribing, the internal edits are commited:
    verifyNoMoreInteractions(mockSharedPreferences);
    verify(mockSharedPreferencesEditor).commit();
    verifyNoMoreInteractions(mockSharedPreferencesEditor);
    assertTrue(subscription.isDisposed());
  }

  private static RxPreferences.Editor put(RxPreferences.Editor editor, PutType putType, String key, Object value) {
    switch (putType) {
      case STRING:
        return editor.putString(key, (String) value);
      case STRING_SET:
        //noinspection unchecked
        return editor.putStringSet(key, (Set<String>) value);
      case INT:
        return editor.putInt(key, (int) value);
      case LONG:
        return editor.putLong(key, (long) value);
      case FLOAT:
        return editor.putFloat(key, (float) value);
      case BOOLEAN:
        return editor.putBoolean(key, (boolean) value);
      default:
        fail("Unknown PutType: " + putType);
        throw new UnsupportedOperationException();
    }
  }

  private static void verifyPut(SharedPreferences.Editor mockSharedPreferencesEditor, PutType putType, String key,
      Object value) {
    switch (putType) {
      case STRING:
        verify(mockSharedPreferencesEditor).putString(key, (String) value);
        break;
      case STRING_SET:
        //noinspection unchecked
        verify(mockSharedPreferencesEditor).putStringSet(key, (Set<String>) value);
        break;
      case INT:
        verify(mockSharedPreferencesEditor).putInt(key, (int) value);
        break;
      case LONG:
        verify(mockSharedPreferencesEditor).putLong(key, (long) value);
        break;
      case FLOAT:
        verify(mockSharedPreferencesEditor).putFloat(key, (float) value);
        break;
      case BOOLEAN:
        verify(mockSharedPreferencesEditor).putBoolean(key, (boolean) value);
        break;
    }
  }
  //endregion

  //region remove
  @Test
  public void remove_removesFromInternalPreferences() {
    final String testKey = "Another test key";

    final Disposable subscription = rxPreferences.edit()
        .remove(testKey)
        .commit()
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    // Before subscribing, edits are pushed to the internal editor, but not committed:
    verify(mockSharedPreferences).edit();
    verifyNoMoreInteractions(mockSharedPreferences);
    verify(mockSharedPreferencesEditor).remove(testKey);
    verifyNoMoreInteractions(mockSharedPreferencesEditor);
    assertFalse(subscription.isDisposed());

    advanceScheduler();
    verifyCommitAfterSubscribe(subscription);
  }
  //endregion

  //region clear
  @Test
  public void clear_clearsFromInternalPreferences() {
    final Disposable subscription = rxPreferences.edit()
        .clear()
        .commit()
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    // Before subscribing, edits are pushed to the internal editor, but not committed:
    verify(mockSharedPreferences).edit();
    verifyNoMoreInteractions(mockSharedPreferences);
    verify(mockSharedPreferencesEditor).clear();
    verifyNoMoreInteractions(mockSharedPreferencesEditor);
    assertFalse(subscription.isDisposed());

    advanceScheduler();
    verifyCommitAfterSubscribe(subscription);
  }
  //endregion

  //region commit
  @Test
  public void commit_internalEditorReturnsTrue_completes() {
    final TestObserver<Void> testObserver = rxPreferences.edit()
        .commit()
        .test();
    subscriptions.add(testObserver);

    testObserver.assertComplete();
  }

  @Test
  public void commit_internalEditorReturnsFalse_propagatesCommitException() {
    when(mockSharedPreferencesEditor.commit()).thenReturn(false);

    final TestObserver<Void> testObserver = rxPreferences.edit()
        .commit()
        .test();
    subscriptions.add(testObserver);

    testObserver.assertError(RxPreferences.Editor.CommitException.class);
  }
  //endregion
  //endregion

  //region CommitException
  @Test
  public void commitException_hasExpectedMessage() {
    final String expectedMessage = "Failed to commit the desired preference changes";
    final RxPreferences.Editor.CommitException commitException = new RxPreferences.Editor.CommitException();
    assertEquals(expectedMessage, commitException.getMessage());
  }
  //endregion
}
