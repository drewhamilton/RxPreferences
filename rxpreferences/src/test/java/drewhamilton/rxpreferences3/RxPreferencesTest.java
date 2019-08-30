package drewhamilton.rxpreferences3;

import android.content.SharedPreferences;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyFloat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
    public void getAllOnce_emitsMapFromInternalPreferences() {
        final Map<String, ?> testMap = Collections.singletonMap("Made up map key", 23498);

        //noinspection unchecked: Not sure why this cast is needed but it works
        when(mockSharedPreferences.getAll()).thenReturn((Map) testMap);

        final TestObserver<Map<String, ?>> subscription = rxPreferences.getAllOnce()
                .test();
        subscriptions.add(subscription);

        subscription
                .assertComplete()
                .assertValueCount(1)
                .assertValue(testMap);
    }

    @Test
    public void getAllOnce_getsAfterSubscribe() {
        final Map<String, ?> testMap = Collections.singletonMap("Made up map key", 234234);
        //noinspection unchecked: Not sure why this cast is needed but it works
        when(mockSharedPreferences.getAll()).thenReturn((Map) testMap);

        final Disposable subscription = rxPreferences.getAllOnce()
                .subscribeOn(testScheduler)
                .subscribe();
        subscriptions.add(subscription);

        verifyNoMoreInteractions(mockSharedPreferences);

        advanceScheduler();
        verify(mockSharedPreferences).getAll();
        verifyNoMoreInteractions(mockSharedPreferences);
    }

    @Test
    public void getAllStream_emitsOnListenerUpdate() {
        final Map<String, ?> returnedMap = Collections.singletonMap("Test dummy key", 234);
        //noinspection unchecked
        when(mockSharedPreferences.getAll()).thenReturn((Map) returnedMap);

        final TestObserver<Map<String, ?>> subscription = rxPreferences.getAllStream().test();
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
    public void getAllStream_emitsCurrentValueOnSubscribe() {
        final Map<String, ?> returnedMap = Collections.singletonMap("Test dummy long key", 23453534523L);
        //noinspection unchecked
        when(mockSharedPreferences.getAll()).thenReturn((Map) returnedMap);

        final Disposable subscription = rxPreferences.getAllStream()
                .subscribeOn(testScheduler)
                .subscribe();
        subscriptions.add(subscription);

        verifyGetStreamBeforeSubscribe(subscription);

        advanceScheduler();

        verifyNoMoreInteractions(mockSharedPreferencesEditor);
        verify(mockSharedPreferences).getAll();
        verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA(RxAllPreferencesListener.class));
        verifyNoMoreInteractions(mockSharedPreferences);
        assertFalse(subscription.isDisposed());
    }

    @Test
    public void getAllStream_unregistersListenerOnUnsubscribe() {
        final Map<String, ?> returnedMap = Collections.singletonMap("Test dummy long key", 23453534523L);
        //noinspection unchecked
        when(mockSharedPreferences.getAll()).thenReturn((Map) returnedMap);

        final TestObserver<Map<String, ?>> subscription = rxPreferences.getAllStream().test();
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

    //region get preference once
    @Test
    public void getStringOnce_emitsFromInternalPreferences() {
        testGetOnceMethod_emitsFromInternalPreferences(PreferenceType.STRING, "Test value", "Test default");
    }

    @Test
    public void getStringOnce_getsAfterSubscribe() {
        testGetOnceMethod_getsAfterSubscribe(PreferenceType.STRING, "Test value", "Test default");
    }

    @Test
    public void getStringSetOnce_emitsFromInternalPreferences() {
        testGetOnceMethod_emitsFromInternalPreferences(PreferenceType.STRING_SET, Collections.singleton("Test value"),
                Collections.singleton("Test default"));
    }

    @Test
    public void getStringSetOnce_getsAfterSubscribe() {
        testGetOnceMethod_getsAfterSubscribe(PreferenceType.STRING_SET, Collections.singleton("Test value"),
                Collections.singleton("Test default"));
    }

    @Test
    public void getIntOnce_emitsFromInternalPreferences() {
        testGetOnceMethod_emitsFromInternalPreferences(PreferenceType.INT, 2332, -987);
    }

    @Test
    public void getIntOnce_getsAfterSubscribe() {
        testGetOnceMethod_getsAfterSubscribe(PreferenceType.INT, 2332, -987);
    }

    @Test
    public void getLongOnce_emitsFromInternalPreferences() {
        testGetOnceMethod_emitsFromInternalPreferences(PreferenceType.LONG, 342342342343L, -38948985934859L);
    }

    @Test
    public void getLongOnce_getsAfterSubscribe() {
        testGetOnceMethod_getsAfterSubscribe(PreferenceType.LONG, 342342342343L, -38948985934859L);
    }

    @Test
    public void getFloatOnce_emitsFromInternalPreferences() {
        testGetOnceMethod_emitsFromInternalPreferences(PreferenceType.FLOAT, 234.432f, -987.654f);
    }

    @Test
    public void getFloatOnce_getsAfterSubscribe() {
        testGetOnceMethod_getsAfterSubscribe(PreferenceType.FLOAT, 234.432f, -987.654f);
    }

    @Test
    public void getBooleanOnce_emitsFromInternalPreferences() {
        testGetOnceMethod_emitsFromInternalPreferences(PreferenceType.BOOLEAN, true, false);
    }

    @Test
    public void getBooleanOnce_getsAfterSubscribe() {
        testGetOnceMethod_getsAfterSubscribe(PreferenceType.BOOLEAN, true, false);
    }

    private void testGetOnceMethod_emitsFromInternalPreferences(PreferenceType type, Object returnedValue,
            Object defaultValue) {
        assertNotNull(type);
        assertNotNull(returnedValue);
        assertNotNull(defaultValue);
        final String valueTypeFailureMessage = "Testing #getOnce method of type " + type + " requires a value of type "
                + type.valueClass.getSimpleName() + ", but the provided value was of type "
                + returnedValue.getClass().getSimpleName();
        assertTrue(valueTypeFailureMessage, type.valueClass.isAssignableFrom(returnedValue.getClass()));

        final String testKey = "Test " + type + " key";
        mockGet(type, testKey, returnedValue);

        final TestObserver<Object> subscription = getOnce(rxPreferences, type, testKey, defaultValue)
                .test();
        subscriptions.add(subscription);

        subscription
                .assertComplete()
                .assertValueCount(1)
                .assertValue(returnedValue);
    }

    private void testGetOnceMethod_getsAfterSubscribe(PreferenceType type, Object returnedValue, Object defaultValue) {
        assertNotNull(type);
        assertNotNull(returnedValue);
        assertNotNull(defaultValue);
        final String valueTypeFailureMessage = "Testing #getOnce method of type " + type + " requires a value of type "
                + type.valueClass.getSimpleName() + ", but the provided value was of type "
                + returnedValue.getClass().getSimpleName();
        assertTrue(valueTypeFailureMessage, type.valueClass.isAssignableFrom(returnedValue.getClass()));

        final String testKey = "Test " + type + " key";
        mockGet(type, testKey, returnedValue);

        final Disposable subscription = getOnceOnTestScheduler(type, testKey, defaultValue);
        subscriptions.add(subscription);

        verifyGetBeforeSubscribe(subscription);

        advanceScheduler();
        verifyGetAfterSubscribe(type, testKey, defaultValue, subscription);
    }

    private Disposable getOnceOnTestScheduler(PreferenceType type, String key, Object defaultValue) {
        return getOnce(rxPreferences, type, key, defaultValue)
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

    private static <T> Single<T> getOnce(RxPreferences rxPreferences, PreferenceType type, String key,
            Object defaultValue) {
        final Single preferenceSingle;
        switch (type) {
            case STRING:
                preferenceSingle = rxPreferences.getStringOnce(key, (String) defaultValue);
                break;
            case STRING_SET:
                //noinspection unchecked
                preferenceSingle = rxPreferences.getStringSetOnce(key, (Set<String>) defaultValue);
                break;
            case INT:
                preferenceSingle = rxPreferences.getIntOnce(key, (int) defaultValue);
                break;
            case LONG:
                preferenceSingle = rxPreferences.getLongOnce(key, (long) defaultValue);
                break;
            case FLOAT:
                preferenceSingle = rxPreferences.getFloatOnce(key, (float) defaultValue);
                break;
            case BOOLEAN:
                preferenceSingle = rxPreferences.getBooleanOnce(key, (boolean) defaultValue);
                break;
            default:
                fail("Unknown preference type: " + type);
                throw new UnsupportedOperationException();
        }

        //noinspection unchecked: the caller needs to use the correct type
        return preferenceSingle;
    }
    //endregion

    //region get preference stream
    @Test
    public void getStringStream_emitsOnListenerUpdate() {
        testGetStream_emitsOnListenerUpdate(PreferenceType.STRING, "String value", "Default string");
    }

    @Test
    public void getStringStream_emitsCurrentValueOnSubscribe() {
        testGetStream_emitsCurrentValueOnSubscribe(PreferenceType.STRING, "Current value", "Default value");
    }

    @Test
    public void getStringStream_unregistersListenerOnUnsubscribe() {
        testGetStream_unregistersListenerOnUnsubscribe(PreferenceType.STRING, "Dummy value", "Default value");
    }

    @Test
    public void getStringSetStream_emitsOnListenerUpdate() {
        testGetStream_emitsOnListenerUpdate(PreferenceType.STRING_SET, Collections.singleton("String value"),
                Collections.singleton("Default string"));
    }

    @Test
    public void getStringSetStream_emitsCurrentValueOnSubscribe() {
        testGetStream_emitsCurrentValueOnSubscribe(PreferenceType.STRING_SET, Collections.singleton("Current value"),
                Collections.singleton("Default value"));
    }

    @Test
    public void getStringSetStream_unregistersListenerOnUnsubscribe() {
        testGetStream_unregistersListenerOnUnsubscribe(PreferenceType.STRING_SET, Collections.singleton("Dummy value"),
                Collections.singleton("Default value"));
    }

    @Test
    public void getIntStream_emitsOnListenerUpdate() {
        testGetStream_emitsOnListenerUpdate(PreferenceType.INT, 123, -321);
    }

    @Test
    public void getIntStream_emitsCurrentValueOnSubscribe() {
        testGetStream_emitsCurrentValueOnSubscribe(PreferenceType.INT, 123, -321);
    }

    @Test
    public void getIntStream_unregistersListenerOnUnsubscribe() {
        testGetStream_unregistersListenerOnUnsubscribe(PreferenceType.INT, 123, -321);
    }

    @Test
    public void getLongStream_emitsOnListenerUpdate() {
        testGetStream_emitsOnListenerUpdate(PreferenceType.LONG, 12345678900L, -9876543210L);
    }

    @Test
    public void getLongStream_emitsCurrentValueOnSubscribe() {
        testGetStream_emitsCurrentValueOnSubscribe(PreferenceType.LONG, 12345678900L, -9876543210L);
    }

    @Test
    public void getLongStream_unregistersListenerOnUnsubscribe() {
        testGetStream_unregistersListenerOnUnsubscribe(PreferenceType.LONG, 12345678900L, -9876543210L);
    }

    @Test
    public void getFloatStream_emitsOnListenerUpdate() {
        testGetStream_emitsOnListenerUpdate(PreferenceType.FLOAT, 123.456f, -321.987f);
    }

    @Test
    public void getFloatStream_emitsCurrentValueOnSubscribe() {
        testGetStream_emitsCurrentValueOnSubscribe(PreferenceType.FLOAT, 123.456f, -321.987f);
    }

    @Test
    public void getFloatStream_unregistersListenerOnUnsubscribe() {
        testGetStream_unregistersListenerOnUnsubscribe(PreferenceType.FLOAT, 123.456f, -321.987f);
    }

    @Test
    public void getBooleanStream_emitsOnListenerUpdate() {
        testGetStream_emitsOnListenerUpdate(PreferenceType.BOOLEAN, true, false);
    }

    @Test
    public void getBooleanStream_emitsCurrentValueOnSubscribe() {
        testGetStream_emitsCurrentValueOnSubscribe(PreferenceType.BOOLEAN, true, false);
    }

    @Test
    public void getBooleanStream_unregistersListenerOnUnsubscribe() {
        testGetStream_unregistersListenerOnUnsubscribe(PreferenceType.BOOLEAN, true, false);
    }

    private void testGetStream_emitsOnListenerUpdate(PreferenceType type, Object returnedValue, Object defaultValue) {
        final String testKey = "Test " + type + " key";
        mockGet(type, testKey, returnedValue);

        final TestObserver<Object> subscription = getStream(rxPreferences, type, testKey, defaultValue).test();
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

    private void testGetStream_emitsCurrentValueOnSubscribe(PreferenceType type, Object returnedValue,
            Object defaultValue) {
        final String testKey = "Test " + type + " key";
        mockGet(type, testKey, returnedValue);

        final Disposable subscription = getStream(rxPreferences, type, testKey, defaultValue)
                .subscribeOn(testScheduler)
                .subscribe();
        subscriptions.add(subscription);

        verifyGetStreamBeforeSubscribe(subscription);

        advanceScheduler();
        verifyGetStreamAfterSubscribe(type, testKey, defaultValue, subscription);
    }

    private void testGetStream_unregistersListenerOnUnsubscribe(PreferenceType type, Object returnedValue,
            Object defaultValue) {
        final String testKey = "Test " + type + " key";
        mockGet(type, testKey, returnedValue);

        final TestObserver<Object> subscription = getStream(rxPreferences, type, testKey, defaultValue).test();
        subscriptions.add(subscription);

        final ArgumentCaptor<SharedPreferences.OnSharedPreferenceChangeListener> listenerCaptor =
                ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener.class);
        verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture());

        final SharedPreferences.OnSharedPreferenceChangeListener listener = listenerCaptor.getValue();

        verify(mockSharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(any());
        subscription.dispose();

        verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void verifyGetStreamBeforeSubscribe(Disposable subscription) {
        verifyGetBeforeSubscribe(subscription);
    }

    private void verifyGetStreamAfterSubscribe(PreferenceType type, String key, Object defaultValue,
            Disposable subscription) {
        verifyNoMoreInteractions(mockSharedPreferencesEditor);

        // After subscribing, the preference is retrieved from the internal preferences:
        verifyGet(type, key, defaultValue);
        verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA(RxPreferenceChangeListener.class));
        verifyNoMoreInteractions(mockSharedPreferences);
        assertFalse(subscription.isDisposed());
    }

    private static <T> Observable<T> getStream(RxPreferences rxPreferences, PreferenceType type, String key,
            Object defaultValue) {
        final Observable preferenceObservable;
        switch (type) {
            case STRING:
                preferenceObservable = rxPreferences.getStringStream(key, (String) defaultValue);
                break;
            case STRING_SET:
                //noinspection unchecked
                preferenceObservable = rxPreferences.getStringSetStream(key, (Set<String>) defaultValue);
                break;
            case INT:
                preferenceObservable = rxPreferences.getIntStream(key, (int) defaultValue);
                break;
            case LONG:
                preferenceObservable = rxPreferences.getLongStream(key, (long) defaultValue);
                break;
            case FLOAT:
                preferenceObservable = rxPreferences.getFloatStream(key, (float) defaultValue);
                break;
            case BOOLEAN:
                preferenceObservable = rxPreferences.getBooleanStream(key, (boolean) defaultValue);
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
    public void containsOnce_emitsFromInternalPreferences() {
        final String testKey = "Test key";
        when(mockSharedPreferences.contains(testKey)).thenReturn(true);

        final TestObserver<Boolean> subscription = rxPreferences.containsOnce(testKey)
                .test();
        subscriptions.add(subscription);

        subscription
                .assertComplete()
                .assertValueCount(1)
                .assertValue(true);
    }

    @Test
    public void containsOnce_getsAfterSubscribe() {
        final String testKey = "Test key";
        when(mockSharedPreferences.contains(testKey)).thenReturn(true);

        final Disposable subscription = rxPreferences.containsOnce(testKey)
                .subscribeOn(testScheduler)
                .subscribe();
        subscriptions.add(subscription);

        verifyNoMoreInteractions(mockSharedPreferences);

        advanceScheduler();
        verify(mockSharedPreferences).contains(testKey);
        verifyNoMoreInteractions(mockSharedPreferences);
    }

    @Test
    public void containsStream_emitsOnListenerUpdate() {
        final String testKey = "Test contains key";
        when(mockSharedPreferences.contains(testKey)).thenReturn(true);

        final TestObserver<Boolean> subscription = rxPreferences.containsStream(testKey).test();
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
    public void containsStream_emitsCurrentValueOnSubscribe() {
        final String testKey = "Test contains key";
        when(mockSharedPreferences.contains(testKey)).thenReturn(true);

        final Disposable subscription = rxPreferences.containsStream(testKey)
                .subscribeOn(testScheduler)
                .subscribe();
        subscriptions.add(subscription);

        verifyGetStreamBeforeSubscribe(subscription);

        advanceScheduler();
        // After subscribing, the value is retrieved from the internal preferences:
        verify(mockSharedPreferences).contains(testKey);
        verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA(RxPreferenceContainsListener.class));
        verifyNoMoreInteractions(mockSharedPreferences);
        assertFalse(subscription.isDisposed());
    }

    @Test
    public void containsStream_unregistersListenerOnUnsubscribe() {
        final String testKey = "Test contains key";
        when(mockSharedPreferences.contains(testKey)).thenReturn(true);

        final TestObserver<Boolean> subscription = rxPreferences.containsStream(testKey).test();
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
