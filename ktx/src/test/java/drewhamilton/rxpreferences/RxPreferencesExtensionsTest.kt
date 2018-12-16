package drewhamilton.rxpreferences

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
  @Test
  fun `getEnum emits from internal preferences`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.LONG
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnum(testKey, testDefault)
        .test()
        .trackUntilTearDown()

    subscription
        .assertComplete()
        .assertValueCount(1)
        .assertValue(testValue)
  }

  @Test
  fun `getEnum gets after subscribe`() {
    val testKey = "Test PreferenceType key"
    val testValue = PreferenceType.LONG
    val testDefault = PreferenceType.BOOLEAN
    mockGet(PreferenceType.STRING, testKey, testValue.name)

    val subscription = rxPreferences.getEnum(testKey, testDefault)
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
