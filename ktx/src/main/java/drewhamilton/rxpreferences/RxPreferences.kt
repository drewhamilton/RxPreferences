package drewhamilton.rxpreferences

import io.reactivex.Observable

/**
 * Retrieve an enum value from the preferences that was saved with [putEnum].
 * @param key The name of the preference to retrieve.
 * @param defaultValue Value to return if this preference does not exist.
 * @return the preference value if it exists, or [defaultValue].
 * @throws [ClassCastException] if there is a preference with this key that is not
 * a string, e.g. if [putEnumByOrdinal] was used to save the enum.
 * @throws [IllegalArgumentException] if the stored string does not resolve to a valid
 * name for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.getEnum(key: String, defaultValue: E) =
    getString(key, defaultValue.name)
        .map { enumValueOf<E>(it) }

/**
 * Observe an enum value from the preferences that was saved with [putEnum].
 * @param key The name of the preference to retrieve.
 * @param defaultValue Value to return if this preference does not exist. This value
 * is emitted if it does not exist upon subscription and if the preference is ever
 * cleared.
 * @return an [Observable] that emits the saved preference.
 * @throws [ClassCastException] if there is a preference with this key that is not
 * a string, e.g. if [putEnumByOrdinal] was used to save the enum.
 * @throws [IllegalArgumentException] if the stored string does not resolve to a valid
 * name for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.observeEnum(key: String, defaultValue: E) =
    observeString(key, defaultValue.name)
        .map { enumValueOf<E>(it) }!!

/**
 * Retrieve an enum value from the preferences that was saved with [putEnumByOrdinal].
 * @param key The name of the preference to retrieve.
 * @param defaultValue Value to return if this preference does not exist.
 * @return the enum if its ordinal exists under the given key, or [defaultValue].
 * @throws [ClassCastException] if there is a preference with this key that is not
 * an int, e.g. if [putEnum] was used to save the enum.
 * @throws [IndexOutOfBoundsException] if the stored int does not resolve to a valid
 * ordinal for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.getEnumByOrdinal(key: String, defaultValue: E) =
    getInt(key, defaultValue.ordinal)
        .map { enumValues<E>()[it] }

/**
 * Observe an enum value from the preferences that was saved with [putEnumByOrdinal].
 * @param key The name of the preference to retrieve.
 * @param defaultValue Value to return if this preference does not exist. This value is emitted if it does not exist
 * upon subscription and if the preference is ever cleared.
 * @return an [Observable] that emits the saved preference.
 * @throws [ClassCastException] if there is a preference with this key that is not
 * a string, e.g. if [putEnum] was used to save the enum.
 * @throws [IndexOutOfBoundsException] if the stored int does not resolve to a valid
 * ordinal for a value of type [E].
 */
inline fun <reified E : Enum<E>> RxPreferences.observeEnumByOrdinal(key: String, defaultValue: E) =
    observeInt(key, defaultValue.ordinal)
        .map { enumValues<E>()[it] }!!

/**
 * Apply a series of edits to [this] and then commit them.
 *
 * Note that calls to [RxPreferences.Editor.remove] and [RxPreferences.Editor.clear] are executed first, regardless of
 * what order they appear in the series of edits.
 */
inline fun RxPreferences.edit(edits: RxPreferences.Editor.() -> RxPreferences.Editor) =
    edits.invoke(this.edit()).commit()

/**
 * Set an enum value in the preferences editor, to be written as a name string once [RxPreferences.Editor.commit] is
 * called.
 * @param key The name of the preference to modify.
 * @param value The new value for the preference.
 * @return a reference to the same [RxPreferences.Editor] object, so you can chain calls together.
 */
inline fun <reified E : Enum<E>> RxPreferences.Editor.putEnum(key: String, value: E) = putString(key, value.name)

/**
 * Set an enum value in the preferences editor, to be written as an ordinal int once [RxPreferences.Editor.commit] is
 * called.
 * @param key The name of the preference to modify.
 * @param value The new value for the preference.
 * @return a reference to the same [RxPreferences.Editor] object, so you can chain calls together.
 */
inline fun <reified E : Enum<E>> RxPreferences.Editor.putEnumByOrdinal(key: String, value: E) =
    putInt(key, value.ordinal)
