package dev.drewhamilton.rxpreferences

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

/**
 * Get a [Single] that will emit the enum value associated with [key] once. If there is no value associated with [key],
 * [defaultValue] will be returned.
 *
 * Throws [ClassCastException] if there is a preference with this key that is not a string, e.g. if [putEnumByOrdinal]
 * was used to save the enum. Throws [IllegalArgumentException] if the stored string does not resolve to a valid name
 * for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.getEnumOnce(key: String, defaultValue: E): Single<E> =
    getStringOnce(key, defaultValue.name)
        .map { name -> enumValueOf<E>(name) }

/**
 * Observe enum values associated with [key]. The returned [Observable] emits [defaultValue] if the preference does not
 * exist upon subscription and if the preference is ever cleared.
 *
 * Throws [ClassCastException] if there is a preference with this key that is not a string, e.g. if [putEnumByOrdinal]
 * was used to save the enum. Throws [IllegalArgumentException] if the stored string does not resolve to a valid
 * name for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.getEnumStream(key: String, defaultValue: E): Observable<E> =
    getStringStream(key, defaultValue.name)
        .map { name -> enumValueOf<E>(name) }!!

/**
 * Get a [Single] that will emit the enum value associated with [key] once. If there is no value associated with [key],
 * [defaultValue] will be returned.
 *
 * Throws [ClassCastException] if there is a preference with this key that is not an int, e.g. if [putEnum] was used to
 * save the enum instead of [putEnumByOrdinal]. Throws [IndexOutOfBoundsException] if the stored int does not resolve to
 * a valid ordinal for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.getEnumByOrdinalOnce(key: String, defaultValue: E): Single<E> =
    getIntOnce(key, defaultValue.ordinal)
        .map { ordinal -> enumValues<E>()[ordinal] }

/**
 * Observe enum values associated with [key]. The returned [Observable] emits [defaultValue] if the preference does not
 * exist upon subscription and if the preference is ever cleared.
 *
 * Throws [ClassCastException] if there is a preference with this key that is not an int, e.g. if [putEnum] was used to
 * save the enum instead of [putEnumByOrdinal]. Throws [IndexOutOfBoundsException] if the stored int does not resolve to
 * a valid ordinal for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.getEnumByOrdinalStream(key: String, defaultValue: E): Observable<E> =
    getIntStream(key, defaultValue.ordinal)
        .map { ordinal -> enumValues<E>()[ordinal] }!!

/**
 * Apply a series of edits to [this] and then commit them.
 *
 * Note that calls to [RxPreferences.Editor.remove] and [RxPreferences.Editor.clear] are executed first, regardless of
 * what order they appear in the series of edits.
 */
inline fun RxPreferences.edit(edits: RxPreferences.Editor.() -> RxPreferences.Editor): Completable =
    edits.invoke(this.edit()).commit()

/**
 * Set an enum [value] in the preferences editor, to be written as a name string and associated with [key] once
 * [RxPreferences.Editor.commit] is called.
 *
 * Returns a reference to the same [RxPreferences.Editor] object, so you can chain calls together.
 */
inline fun <reified E : Enum<E>> RxPreferences.Editor.putEnum(key: String, value: E): RxPreferences.Editor =
    putString(key, value.name)

/**
 * Set an enum [value] in the preferences editor, to be written as an ordinal int and associated with [key] once
 * [RxPreferences.Editor.commit] is called.
 *
 * Returns a reference to the same [RxPreferences.Editor] object, so you can chain calls together.
 */
inline fun <reified E : Enum<E>> RxPreferences.Editor.putEnumByOrdinal(key: String, value: E): RxPreferences.Editor =
    putInt(key, value.ordinal)
