package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
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

  //region RxPreferences
  //region all preferences
  @Test
  public void getAll_emitsMapFromInternalPreferences() {
    final Map<String, ?> testMap = Collections.singletonMap("Made up map key", 23498);

    //noinspection unchecked: Not sure why this cast is needed but it works
    when(mockSharedPreferences.getAll()).thenReturn((Map) testMap);

    final TestObserver<Map<String, ?>> subscription = rxPreferences.getAll()
        .test();
    subscriptions.add(subscription);

    subscription
        .assertComplete()
        .assertValueCount(1)
        .assertValue(testMap);
  }

  @Test
  public void getAll_getsAfterSubscribe() {
    final Map<String, ?> testMap = Collections.singletonMap("Made up map key", 234234);
    //noinspection unchecked: Not sure why this cast is needed but it works
    when(mockSharedPreferences.getAll()).thenReturn((Map) testMap);

    final Disposable subscription = rxPreferences.getAll()
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    verifyNoMoreInteractions(mockSharedPreferences);

    advanceScheduler();
    verify(mockSharedPreferences).getAll();
    verifyNoMoreInteractions(mockSharedPreferences);
  }

  @Test
  public void observeAll_emitsOnListenerUpdate() {
    final Map<String, ?> returnedMap = Collections.singletonMap("Test dummy key", 234);
    //noinspection unchecked
    when(mockSharedPreferences.getAll()).thenReturn((Map) returnedMap);

    final TestObserver<Map<String, ?>> subscription = rxPreferences.observeAll().test();
    subscriptions.add(subscription);

    subscription
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(returnedMap);

    final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
        ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

    final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();
    listener.onSharedPreferenceChanged(mockSharedPreferences, "Another key");

    //noinspection unchecked
    subscription
        .assertNotComplete()
        .assertValueCount(2)
        .assertValues(returnedMap, returnedMap);
  }

  @Test
  public void observeAll_emitsCurrentValueOnSubscribe() {
    final Map<String, ?> returnedMap = Collections.singletonMap("Test dummy long key", 23453534523L);
    //noinspection unchecked
    when(mockSharedPreferences.getAll()).thenReturn((Map) returnedMap);

    final Disposable subscription = rxPreferences.observeAll()
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    verifyObserveBeforeSubscribe(subscription);

    advanceScheduler();

    verifyNoMoreInteractions(mockSharedPreferencesEditor);
    verify(mockSharedPreferences).getAll();
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA(RxAllPreferencesListener.class));
    verifyNoMoreInteractions(mockSharedPreferences);
    assertFalse(subscription.isDisposed());
  }

  @Test
  public void observeAll_unregistersListenerOnUnsubscribe() {
    final Map<String, ?> returnedMap = Collections.singletonMap("Test dummy long key", 23453534523L);
    //noinspection unchecked
    when(mockSharedPreferences.getAll()).thenReturn((Map) returnedMap);

    final TestObserver<Map<String, ?>> subscription = rxPreferences.observeAll().test();
    subscriptions.add(subscription);

    final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
        ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

    final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();

    verify(mockSharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any());
    subscription.dispose();

    verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(listener);
  }
  //endregion

  //region get preference
  @Test
  public void getString_emitsFromInternalPreferences() {
    testGetMethod_emitsFromInternalPreferences(PreferenceType.STRING, "Test value", "Test default");
  }

  @Test
  public void getString_getsAfterSubscribe() {
    testGetMethod_getsAfterSubscribe(PreferenceType.STRING, "Test value", "Test default");
  }

  @Test
  public void getStringSet_emitsFromInternalPreferences() {
    testGetMethod_emitsFromInternalPreferences(PreferenceType.STRING_SET, Collections.singleton("Test value"),
        Collections.singleton("Test default"));
  }

  @Test
  public void getStringSet_getsAfterSubscribe() {
    testGetMethod_getsAfterSubscribe(PreferenceType.STRING_SET, Collections.singleton("Test value"),
        Collections.singleton("Test default"));
  }

  @Test
  public void getInt_emitsFromInternalPreferences() {
    testGetMethod_emitsFromInternalPreferences(PreferenceType.INT, 2332, -987);
  }

  @Test
  public void getInt_getsAfterSubscribe() {
    testGetMethod_getsAfterSubscribe(PreferenceType.INT, 2332, -987);
  }

  @Test
  public void getLong_emitsFromInternalPreferences() {
    testGetMethod_emitsFromInternalPreferences(PreferenceType.LONG, 342342342343L, -38948985934859L);
  }

  @Test
  public void getLong_getsAfterSubscribe() {
    testGetMethod_getsAfterSubscribe(PreferenceType.LONG, 342342342343L, -38948985934859L);
  }

  @Test
  public void getFloat_emitsFromInternalPreferences() {
    testGetMethod_emitsFromInternalPreferences(PreferenceType.FLOAT, 234.432f, -987.654f);
  }

  @Test
  public void getFloat_getsAfterSubscribe() {
    testGetMethod_getsAfterSubscribe(PreferenceType.FLOAT, 234.432f, -987.654f);
  }

  @Test
  public void getBoolean_emitsFromInternalPreferences() {
    testGetMethod_emitsFromInternalPreferences(PreferenceType.BOOLEAN, true, false);
  }

  @Test
  public void getBoolean_getsAfterSubscribe() {
    testGetMethod_getsAfterSubscribe(PreferenceType.BOOLEAN, true, false);
  }

  private void testGetMethod_emitsFromInternalPreferences(PreferenceType type, Object returnedValue,
      Object defaultValue) {
    assertNotNull(type);
    assertNotNull(returnedValue);
    assertNotNull(defaultValue);
    final String valueTypeFailureMessage = "Testing #get method of type " + type + " requires a value of type "
        + type.valueClass.getSimpleName() + ", but the provided value was of type "
        + returnedValue.getClass().getSimpleName();
    assertTrue(valueTypeFailureMessage, type.valueClass.isAssignableFrom(returnedValue.getClass()));

    final String testKey = "Test " + type + " key";
    mockGet(type, testKey, returnedValue);

    final TestObserver<Object> subscription = get(rxPreferences, type, testKey, defaultValue)
        .test();
    subscriptions.add(subscription);

    subscription
        .assertComplete()
        .assertValueCount(1)
        .assertValue(returnedValue);
  }

  private void testGetMethod_getsAfterSubscribe(PreferenceType type, Object returnedValue, Object defaultValue) {
    assertNotNull(type);
    assertNotNull(returnedValue);
    assertNotNull(defaultValue);
    final String valueTypeFailureMessage = "Testing #get method of type " + type + " requires a value of type "
        + type.valueClass.getSimpleName() + ", but the provided value was of type "
        + returnedValue.getClass().getSimpleName();
    assertTrue(valueTypeFailureMessage, type.valueClass.isAssignableFrom(returnedValue.getClass()));

    final String testKey = "Test " + type + " key";
    mockGet(type, testKey, returnedValue);

    final Disposable subscription = getOnTestScheduler(type, testKey, defaultValue);
    subscriptions.add(subscription);

    verifyGetBeforeSubscribe(subscription);

    advanceScheduler();
    verifyGetAfterSubscribe(type, testKey, defaultValue, subscription);
  }

  private Disposable getOnTestScheduler(PreferenceType type, String key, Object defaultValue) {
    return get(rxPreferences, type, key, defaultValue)
        .subscribeOn(testScheduler)
        .subscribe();
  }

  private void verifyGetBeforeSubscribe(Disposable subscription) {
    // Before subscribing, there are no interactions with the internal preferences:
    verifyNoMoreInteractions(mockSharedPreferences);
    verifyNoMoreInteractions(mockSharedPreferencesEditor);
    assertFalse(subscription.isDisposed());
  }

  private void verifyGetAfterSubscribe(PreferenceType type, String key, Object defaultValue,
      Disposable subscription) {
    verifyNoMoreInteractions(mockSharedPreferencesEditor);

    // After subscribing, the preference is retrieved from the internal preferences:
    verifyGet(type, key, defaultValue);
    verifyNoMoreInteractions(mockSharedPreferences);
    assertTrue(subscription.isDisposed());
  }

  private void mockGet(PreferenceType type, String key, Object returnedValue) {
    switch (type) {
      case STRING:
        when(mockSharedPreferences.getString(eq(key), anyString())).thenReturn((String) returnedValue);
        break;
      case STRING_SET:
        //noinspection unchecked
        when(mockSharedPreferences.getStringSet(eq(key), anySet())).thenReturn((Set<String>) returnedValue);
        break;
      case INT:
        when(mockSharedPreferences.getInt(eq(key), anyInt())).thenReturn((int) returnedValue);
        break;
      case LONG:
        when(mockSharedPreferences.getLong(eq(key), anyLong())).thenReturn((long) returnedValue);
        break;
      case FLOAT:
        when(mockSharedPreferences.getFloat(eq(key), anyFloat())).thenReturn((float) returnedValue);
        break;
      case BOOLEAN:
        when(mockSharedPreferences.getBoolean(eq(key), anyBoolean())).thenReturn((boolean) returnedValue);
        break;
    }
  }

  private void verifyGet(PreferenceType type, String key, Object defaultValue) {
    switch (type) {
      case STRING:
        verify(mockSharedPreferences).getString(key, (String) defaultValue);
        break;
      case STRING_SET:
        //noinspection unchecked
        verify(mockSharedPreferences).getStringSet(key, (Set<String>) defaultValue);
        break;
      case INT:
        verify(mockSharedPreferences).getInt(key, (int) defaultValue);
        break;
      case LONG:
        verify(mockSharedPreferences).getLong(key, (long) defaultValue);
        break;
      case FLOAT:
        verify(mockSharedPreferences).getFloat(key, (float) defaultValue);
        break;
      case BOOLEAN:
        verify(mockSharedPreferences).getBoolean(key, (boolean) defaultValue);
        break;
    }
  }

  private static <T> Single<T> get(RxPreferences rxPreferences, PreferenceType type, String key, Object defaultValue) {
    final Single preferenceSingle;
    switch (type) {
      case STRING:
        preferenceSingle = rxPreferences.getString(key, (String) defaultValue);
        break;
      case STRING_SET:
        //noinspection unchecked
        preferenceSingle = rxPreferences.getStringSet(key, (Set<String>) defaultValue);
        break;
      case INT:
        preferenceSingle = rxPreferences.getInt(key, (int) defaultValue);
        break;
      case LONG:
        preferenceSingle = rxPreferences.getLong(key, (long) defaultValue);
        break;
      case FLOAT:
        preferenceSingle = rxPreferences.getFloat(key, (float) defaultValue);
        break;
      case BOOLEAN:
        preferenceSingle = rxPreferences.getBoolean(key, (boolean) defaultValue);
        break;
      default:
        fail("Unknown preference type: " + type);
        throw new UnsupportedOperationException();
    }

    //noinspection unchecked: the caller needs to use the correct type
    return preferenceSingle;
  }
  //endregion

  //region observe preference
  @Test
  public void observeString_emitsOnListenerUpdate() {
    testObserve_emitsOnListenerUpdate(PreferenceType.STRING, "String value", "Default string");
  }

  @Test
  public void observeString_emitsCurrentValueOnSubscribe() {
    testObserve_emitsCurrentValueOnSubscribe(PreferenceType.STRING, "Current value", "Default value");
  }

  @Test
  public void observeString_unregistersListenerOnUnsubscribe() {
    testObserve_unregistersListenerOnUnsubscribe(PreferenceType.STRING, "Dummy value", "Default value");
  }

  @Test
  public void observeStringSet_emitsOnListenerUpdate() {
    testObserve_emitsOnListenerUpdate(PreferenceType.STRING_SET, Collections.singleton("String value"),
        Collections.singleton("Default string"));
  }

  @Test
  public void observeStringSet_emitsCurrentValueOnSubscribe() {
    testObserve_emitsCurrentValueOnSubscribe(PreferenceType.STRING_SET, Collections.singleton("Current value"),
        Collections.singleton("Default value"));
  }

  @Test
  public void observeStringSet_unregistersListenerOnUnsubscribe() {
    testObserve_unregistersListenerOnUnsubscribe(PreferenceType.STRING_SET, Collections.singleton("Dummy value"),
        Collections.singleton("Default value"));
  }

  @Test
  public void observeInt_emitsOnListenerUpdate() {
    testObserve_emitsOnListenerUpdate(PreferenceType.INT, 123, -321);
  }

  @Test
  public void observeInt_emitsCurrentValueOnSubscribe() {
    testObserve_emitsCurrentValueOnSubscribe(PreferenceType.INT, 123, -321);
  }

  @Test
  public void observeInt_unregistersListenerOnUnsubscribe() {
    testObserve_unregistersListenerOnUnsubscribe(PreferenceType.INT, 123, -321);
  }

  @Test
  public void observeLong_emitsOnListenerUpdate() {
    testObserve_emitsOnListenerUpdate(PreferenceType.LONG, 12345678900L, -9876543210L);
  }

  @Test
  public void observeLong_emitsCurrentValueOnSubscribe() {
    testObserve_emitsCurrentValueOnSubscribe(PreferenceType.LONG, 12345678900L, -9876543210L);
  }

  @Test
  public void observeLong_unregistersListenerOnUnsubscribe() {
    testObserve_unregistersListenerOnUnsubscribe(PreferenceType.LONG, 12345678900L, -9876543210L);
  }

  @Test
  public void observeFloat_emitsOnListenerUpdate() {
    testObserve_emitsOnListenerUpdate(PreferenceType.FLOAT, 123.456f, -321.987f);
  }

  @Test
  public void observeFloat_emitsCurrentValueOnSubscribe() {
    testObserve_emitsCurrentValueOnSubscribe(PreferenceType.FLOAT, 123.456f, -321.987f);
  }

  @Test
  public void observeFloat_unregistersListenerOnUnsubscribe() {
    testObserve_unregistersListenerOnUnsubscribe(PreferenceType.FLOAT, 123.456f, -321.987f);
  }

  @Test
  public void observeBoolean_emitsOnListenerUpdate() {
    testObserve_emitsOnListenerUpdate(PreferenceType.BOOLEAN, true, false);
  }

  @Test
  public void observeBoolean_emitsCurrentValueOnSubscribe() {
    testObserve_emitsCurrentValueOnSubscribe(PreferenceType.BOOLEAN, true, false);
  }

  @Test
  public void observeBoolean_unregistersListenerOnUnsubscribe() {
    testObserve_unregistersListenerOnUnsubscribe(PreferenceType.BOOLEAN, true, false);
  }

  private void testObserve_emitsOnListenerUpdate(PreferenceType type, Object returnedValue, Object defaultValue) {
    final String testKey = "Test " + type + " key";
    mockGet(type, testKey, returnedValue);

    final TestObserver<Object> subscription = observe(rxPreferences, type, testKey, defaultValue).test();
    subscriptions.add(subscription);

    subscription
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(returnedValue);

    final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
        ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

    final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();
    listener.onSharedPreferenceChanged(mockSharedPreferences, testKey);

    subscription
        .assertNotComplete()
        .assertValueCount(2)
        .assertValues(returnedValue, returnedValue);
  }

  private void testObserve_emitsCurrentValueOnSubscribe(PreferenceType type, Object returnedValue,
      Object defaultValue) {
    final String testKey = "Test " + type + " key";
    mockGet(type, testKey, returnedValue);

    final Disposable subscription = observe(rxPreferences, type, testKey, defaultValue)
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    verifyObserveBeforeSubscribe(subscription);

    advanceScheduler();
    verifyObserveAfterSubscribe(type, testKey, defaultValue, subscription);
  }

  private void testObserve_unregistersListenerOnUnsubscribe(PreferenceType type, Object returnedValue,
      Object defaultValue) {
    final String testKey = "Test " + type + " key";
    mockGet(type, testKey, returnedValue);

    final TestObserver<Object> subscription = observe(rxPreferences, type, testKey, defaultValue).test();
    subscriptions.add(subscription);

    final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
        ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

    final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();

    verify(mockSharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any());
    subscription.dispose();

    verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(listener);
  }

  private void verifyObserveBeforeSubscribe(Disposable subscription) {
    verifyGetBeforeSubscribe(subscription);
  }

  private void verifyObserveAfterSubscribe(PreferenceType type, String key, Object defaultValue,
      Disposable subscription) {
    verifyNoMoreInteractions(mockSharedPreferencesEditor);

    // After subscribing, the preference is retrieved from the internal preferences:
    verifyGet(type, key, defaultValue);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA(RxPreferenceChangeListener.class));
    verifyNoMoreInteractions(mockSharedPreferences);
    assertFalse(subscription.isDisposed());
  }

  private static <T> Observable<T> observe(RxPreferences rxPreferences, PreferenceType type, String key, Object defaultValue) {
    final Observable preferenceObservable;
    switch (type) {
      case STRING:
        preferenceObservable = rxPreferences.observeString(key, (String) defaultValue);
        break;
      case STRING_SET:
        //noinspection unchecked
        preferenceObservable = rxPreferences.observeStringSet(key, (Set<String>) defaultValue);
        break;
      case INT:
        preferenceObservable = rxPreferences.observeInt(key, (int) defaultValue);
        break;
      case LONG:
        preferenceObservable = rxPreferences.observeLong(key, (long) defaultValue);
        break;
      case FLOAT:
        preferenceObservable = rxPreferences.observeFloat(key, (float) defaultValue);
        break;
      case BOOLEAN:
        preferenceObservable = rxPreferences.observeBoolean(key, (boolean) defaultValue);
        break;
      default:
        fail("Unknown preference type: " + type);
        throw new UnsupportedOperationException();
    }

    //noinspection unchecked: the caller needs to use the correct type
    return preferenceObservable;
  }
  //endregion

  //region contains
  @Test
  public void contains_emitsFromInternalPreferences() {
    final String testKey = "Test key";
    when(mockSharedPreferences.contains(testKey)).thenReturn(true);

    final TestObserver<Boolean> subscription = rxPreferences.contains(testKey)
        .test();
    subscriptions.add(subscription);

    subscription
        .assertComplete()
        .assertValueCount(1)
        .assertValue(true);
  }

  @Test
  public void contains_getsAfterSubscribe() {
    final String testKey = "Test key";
    when(mockSharedPreferences.contains(testKey)).thenReturn(true);

    final Disposable subscription = rxPreferences.contains(testKey)
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    verifyNoMoreInteractions(mockSharedPreferences);

    advanceScheduler();
    verify(mockSharedPreferences).contains(testKey);
    verifyNoMoreInteractions(mockSharedPreferences);
  }

  @Test
  public void observeContains_emitsOnListenerUpdate() {
    final String testKey = "Test contains key";
    when(mockSharedPreferences.contains(testKey)).thenReturn(true);

    final TestObserver<Boolean> subscription = rxPreferences.observeContains(testKey).test();
    subscriptions.add(subscription);

    subscription
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(true);

    final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
        ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

    final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();
    listener.onSharedPreferenceChanged(mockSharedPreferences, testKey);

    subscription
        .assertNotComplete()
        .assertValueCount(2)
        .assertValues(true, true);
  }

  @Test
  public void observeContains_emitsCurrentValueOnSubscribe() {
    final String testKey = "Test contains key";
    when(mockSharedPreferences.contains(testKey)).thenReturn(true);

    final Disposable subscription = rxPreferences.observeContains(testKey)
        .subscribeOn(testScheduler)
        .subscribe();
    subscriptions.add(subscription);

    verifyObserveBeforeSubscribe(subscription);

    advanceScheduler();
    // After subscribing, the value is retrieved from the internal preferences:
    verify(mockSharedPreferences).contains(testKey);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA(RxPreferenceContainsListener.class));
    verifyNoMoreInteractions(mockSharedPreferences);
    assertFalse(subscription.isDisposed());
  }

  @Test
  public void observeContains_unregistersListenerOnUnsubscribe() {
    final String testKey = "Test contains key";
    when(mockSharedPreferences.contains(testKey)).thenReturn(true);

    final TestObserver<Boolean> subscription = rxPreferences.observeContains(testKey).test();
    subscriptions.add(subscription);

    final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
        ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

    final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();

    verify(mockSharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any());
    subscription.dispose();

    verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(listener);
  }
  //endregion

  //region edit
  @Test
  public void edit_returnsNewInstance() {
    assertNotEquals(rxPreferences.edit(), rxPreferences.edit());
  }
  //endregion
  //endregion

  //region Editor
  //region put
  @Test
  public void putString_putsAndCommitsWhenExpected() {
    final String testValue = "Test value";
    testPutMethod(PreferenceType.STRING, testValue);
  }

  @Test
  public void putStringSet_putsAndCommitsWhenExpected() {
    final Set<String> testValue = Collections.singleton("Test value");
    testPutMethod(PreferenceType.STRING_SET, testValue);
  }

  @Test
  public void putInt_putsAndCommitsWhenExpected() {
    final int testValue = 87245;
    testPutMethod(PreferenceType.INT, testValue);
  }

  @Test
  public void putLong_putsAndCommitsWhenExpected() {
    final long testValue = 3141235425L;
    testPutMethod(PreferenceType.LONG, testValue);
  }

  @Test
  public void putFloat_putsAndCommitsWhenExpected() {
    final float testValue = 141.1341f;
    testPutMethod(PreferenceType.FLOAT, testValue);
  }

  @Test
  public void putBoolean_putsAndCommitsWhenExpected() {
    final boolean testValue = true;
    testPutMethod(PreferenceType.BOOLEAN, testValue);
  }

  private void testPutMethod(PreferenceType type, Object value) {
    assertNotNull(type);
    assertNotNull(value);
    final String valueTypeFailureMessage = "Testing #put method with type " + type + " requires a value of type "
        + type.valueClass.getSimpleName() + ", but the provided value was of type " + value.getClass().getSimpleName();
    assertTrue(valueTypeFailureMessage, type.valueClass.isAssignableFrom(value.getClass()));

    final String testKey = "Test " + type + " key";

    final Disposable subscription = putOnTestScheduler(type, testKey, value);
    subscriptions.add(subscription);

    verifyPutBeforeSubscribe(type, testKey, value, subscription);

    advanceScheduler();
    verifyCommitAfterSubscribe(subscription);
  }

  private Disposable putOnTestScheduler(PreferenceType type, String key, Object value) {
    return put(rxPreferences.edit(), type, key, value)
        .commit()
        .subscribeOn(testScheduler)
        .subscribe();
  }

  private void verifyPutBeforeSubscribe(PreferenceType type, String key, Object value, Disposable subscription) {
    // Before subscribing, edits are pushed to the internal editor, but not committed:
    verify(mockSharedPreferences).edit();
    verifyNoMoreInteractions(mockSharedPreferences);
    verifyPut(mockSharedPreferencesEditor, type, key, value);
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

  private static RxPreferences.Editor put(RxPreferences.Editor editor, PreferenceType type, String key, Object value) {
    switch (type) {
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
        fail("Unknown preference type: " + type);
        throw new UnsupportedOperationException();
    }
  }

  private static void verifyPut(SharedPreferences.Editor mockSharedPreferencesEditor, PreferenceType type, String key,
      Object value) {
    switch (type) {
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
