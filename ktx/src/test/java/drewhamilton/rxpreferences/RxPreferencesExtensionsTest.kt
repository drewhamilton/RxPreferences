package drewhamilton.rxpreferences

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class RxPreferencesExtensionsTest {

  @Mock private lateinit var mockSharedPreferences: SharedPreferences
  @Mock private lateinit var mockSharedPreferencesEditor: SharedPreferences.Editor

  @InjectMocks private lateinit var rxPreferences: RxPreferences

  private lateinit var testScheduler: TestScheduler

  private val disposable = CompositeDisposable()

  @Before
  fun setUp() {
    whenever(mockSharedPreferences.edit()).thenReturn(mockSharedPreferencesEditor)
    whenever(mockSharedPreferencesEditor.commit()).thenReturn(true)

    testScheduler = TestScheduler()
  }

  @After
  fun tearDown() {
    disposable.clear()
  }

  private fun <D : Disposable> D.trackUntilTearDown(): D {
    disposable.add(this)
    return this
  }

  private fun advanceScheduler() {
    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
  }

  //region RxPreferences
  //region get preference once
  @Test
  fun `getEnumOnce emits from internal preferences`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.LONG
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnumOnce(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    subscription
        .assertComplete()
        .assertValueCount(1)
        .assertValue(testValue)
  }

  @Test
  fun `getEnumOnce gets after subscribe`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.LONG
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnumOnce(testKey, testDefault)
        .subscribeOn(testScheduler)
        .subscribe()
        .trackUntilTearDown()

    // Before subscribing, there are no interactions with the internal preferences:
    verifyNoMoreInteractions(mockSharedPreferences)
    verifyNoMoreInteractions(mockSharedPreferencesEditor)
    assertFalse(subscription.isDisposed)

    advanceScheduler()

    verifyNoMoreInteractions(mockSharedPreferencesEditor)

    // After subscribing, the preference is retrieved from the internal preferences:
    verifyGet(PreferenceType.STRING, testKey, testDefault.name)
    verifyNoMoreInteractions(mockSharedPreferences)
    assertTrue(subscription.isDisposed)
  }

  @Test
  fun `getEnumByOrdinalOnce emits from internal preferences`() {
    val testKey = "Test PreferenceType ordinal key"
    val testValue = PreferenceType.FLOAT
    val testDefault = PreferenceType.STRING
    mockGet(PreferenceType.INT, testKey, testValue.ordinal)

    val subscription = rxPreferences.getEnumByOrdinalOnce(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    subscription
        .assertComplete()
        .assertValueCount(1)
        .assertValue(testValue)
  }

  @Test
  fun `getEnumByOrdinalOnce gets after subscribe`() {
    val testKey = "Test PreferenceType ordinal key"
    val testValue = PreferenceType.FLOAT
    val testDefault = PreferenceType.STRING
    mockGet(PreferenceType.INT, testKey, testValue.ordinal)

    val subscription = rxPreferences.getEnumByOrdinalOnce(testKey, testDefault)
        .subscribeOn(testScheduler)
        .subscribe()
        .trackUntilTearDown()

    // Before subscribing, there are no interactions with the internal preferences:
    verifyNoMoreInteractions(mockSharedPreferences)
    verifyNoMoreInteractions(mockSharedPreferencesEditor)
    assertFalse(subscription.isDisposed)

    advanceScheduler()

    verifyNoMoreInteractions(mockSharedPreferencesEditor)

    // After subscribing, the preference is retrieved from the internal preferences:
    verifyGet(PreferenceType.INT, testKey, testDefault.ordinal)
    verifyNoMoreInteractions(mockSharedPreferences)
    assertTrue(subscription.isDisposed)
  }

  private fun mockGet(type: PreferenceType, key: String, returnedValue: Any) {
    when (type) {
      PreferenceType.STRING ->
        whenever(mockSharedPreferences.getString(eq(key), any())).thenReturn(returnedValue as String)
      PreferenceType.STRING_SET ->
        @Suppress("UNCHECKED_CAST")
        whenever(mockSharedPreferences.getStringSet(eq(key), any())).thenReturn(returnedValue as Set<String>)
      PreferenceType.INT -> whenever(mockSharedPreferences.getInt(eq(key), any())).thenReturn(returnedValue as Int)
      PreferenceType.LONG -> whenever(mockSharedPreferences.getLong(eq(key), any())).thenReturn(returnedValue as Long)
      PreferenceType.FLOAT ->
        whenever(mockSharedPreferences.getFloat(eq(key), any())).thenReturn(returnedValue as Float)
      PreferenceType.BOOLEAN ->
        whenever(mockSharedPreferences.getBoolean(eq(key), any())).thenReturn(returnedValue as Boolean)
    }
  }

  private fun verifyGet(type: PreferenceType, key: String, defaultValue: Any) {
    when (type) {
      PreferenceType.STRING -> verify(mockSharedPreferences).getString(key, defaultValue as String)
      PreferenceType.STRING_SET ->
        @Suppress("UNCHECKED_CAST")
        verify(mockSharedPreferences).getStringSet(key, defaultValue as Set<String>)
      PreferenceType.INT -> verify(mockSharedPreferences).getInt(key, defaultValue as Int)
      PreferenceType.LONG -> verify(mockSharedPreferences).getLong(key, defaultValue as Long)
      PreferenceType.FLOAT -> verify(mockSharedPreferences).getFloat(key, defaultValue as Float)
      PreferenceType.BOOLEAN -> verify(mockSharedPreferences).getBoolean(key, defaultValue as Boolean)
    }
  }
  //endregion

  //region get preference stream
  @Test
  fun `getEnumStream emits on listener update`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.INT
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnumStream(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    subscription
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(testValue)

    val listenerCaptor = ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener::class.java)
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture())

    val listener = listenerCaptor.value
    listener.onSharedPreferenceChanged(mockSharedPreferences, testKey)

    subscription
        .assertNotComplete()
        .assertValueCount(2)
        .assertValues(testValue, testValue)
  }

  @Test
  fun `getEnumStream emits current value on subscribe`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.INT
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnumStream(testKey, testDefault)
        .subscribeOn(testScheduler)
        .subscribe()
        .trackUntilTearDown()

    verifyGetStreamBeforeSubscribe(subscription)

    advanceScheduler()
    verifyGetStreamAfterSubscribe(PreferenceType.STRING, testKey, testDefault.name, subscription)
  }

  @Test
  fun `getEnumStream unregisters listener on unsubscribe`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.INT
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnumStream(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    val listenerCaptor = ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener::class.java)
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture())

    val listener = listenerCaptor.value

    verify(mockSharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(ArgumentMatchers.any())
    subscription.dispose()

    verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(listener)
  }

  @Test
  fun `getEnumByOrdinalStream emits on listener update`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.STRING
    val testDefault = PreferenceType.LONG
    mockGet(PreferenceType.INT, testKey, testValue.ordinal)

    val subscription = rxPreferences.getEnumByOrdinalStream(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    subscription
        .assertNotComplete()
        .assertValueCount(1)
        .assertValue(testValue)

    val listenerCaptor = ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener::class.java)
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture())

    val listener = listenerCaptor.value
    listener.onSharedPreferenceChanged(mockSharedPreferences, testKey)

    subscription
        .assertNotComplete()
        .assertValueCount(2)
        .assertValues(testValue, testValue)
  }

  @Test
  fun `getEnumByOrdinalStream emits current value on subscribe`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.STRING
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.INT, testKey, testValue.ordinal)

    val subscription = rxPreferences.getEnumByOrdinalStream(testKey, testDefault)
        .subscribeOn(testScheduler)
        .subscribe()
        .trackUntilTearDown()

    verifyGetStreamBeforeSubscribe(subscription)

    advanceScheduler()
    verifyGetStreamAfterSubscribe(PreferenceType.INT, testKey, testDefault.ordinal, subscription)
  }

  @Test
  fun `getEnumByOrdinalStream unregisters listener on unsubscribe`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.FLOAT
    val testDefault = PreferenceType.STRING
    mockGet(PreferenceType.INT, testKey, testValue.ordinal)

    val subscription = rxPreferences.getEnumByOrdinalStream(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    val listenerCaptor = ArgumentCaptor.forClass(SharedPreferences.OnSharedPreferenceChangeListener::class.java)
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(listenerCaptor.capture())

    val listener = listenerCaptor.value

    verify(mockSharedPreferences, never()).unregisterOnSharedPreferenceChangeListener(ArgumentMatchers.any())
    subscription.dispose()

    verify(mockSharedPreferences).unregisterOnSharedPreferenceChangeListener(listener)
  }

  private fun verifyGetStreamBeforeSubscribe(subscription: Disposable) {
    // Before subscribing, there are no interactions with the internal preferences:
    verifyNoMoreInteractions(mockSharedPreferences)
    verifyNoMoreInteractions(mockSharedPreferencesEditor)
    assertFalse(subscription.isDisposed)
  }

  private fun verifyGetStreamAfterSubscribe(
      type: PreferenceType,
      key: String,
      defaultValue: Any,
      subscription: Disposable
  ) {
    verifyNoMoreInteractions(mockSharedPreferencesEditor)

    // After subscribing, the preference is retrieved from the internal preferences:
    verifyGet(type, key, defaultValue)
    verify(mockSharedPreferences).registerOnSharedPreferenceChangeListener(isA<RxPreferenceChangeListener<*>>())
    verifyNoMoreInteractions(mockSharedPreferences)
    assertFalse(subscription.isDisposed)
  }
  //endregion
  //endregion

  //region Editor
  @Test
  fun `edit commits edits in order`() {
    val testStringKey = "Test string key"
    val testStringValue = "Test string value"
    val testIntKey = "Test int key"
    val testIntValue = 8348

    rxPreferences.edit {
      putString(testStringKey, testStringValue)
      putInt(testIntKey, testIntValue)
    }.subscribeOn(testScheduler)
        .subscribe()
        .trackUntilTearDown()

    val editingOrder = inOrder(mockSharedPreferencesEditor)

    verify(mockSharedPreferences).edit()
    verifyNoMoreInteractions(mockSharedPreferences)
    editingOrder.verify(mockSharedPreferencesEditor).putString(testStringKey, testStringValue)
    editingOrder.verify(mockSharedPreferencesEditor).putInt(testIntKey, testIntValue)
    editingOrder.verifyNoMoreInteractions()

    testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS)
    verifyNoMoreInteractions(mockSharedPreferences)
    editingOrder.verify(mockSharedPreferencesEditor).commit()
    editingOrder.verifyNoMoreInteractions()
  }
  //endregion
}
