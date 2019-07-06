package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

import java.util.Map;
import java.util.Set;

/**
 * A wrapper for {@link SharedPreferences} that converts all methods into RxJava. For each getter provided by
 * {@link SharedPreferences}, {@link RxPreferences} provides two options: one returning a {@link Single} and another
 * returning an {@link Observable}. The {@link Observable} version emits the current value of the preference on
 * subscription, and then emits an update any time that value is edited.
 */
public final class RxPreferences {

  @NonNull private final SharedPreferences preferences;

  /**
   * Construct an instance of {@link RxPreferences} wrapping the provided instance of {@link SharedPreferences}.
   * @param preferences the {@link SharedPreferences} instance to wrap.
   */
  public RxPreferences(@NonNull SharedPreferences preferences) {
    this.preferences = preferences;
  }

  /**
   * Retrieve all values from the preferences.
   * <p>
   * Note that you must not modify the collection returned by this method, or alter any of its contents. The consistency
   * of your stored data is not guaranteed if you do.
   * @return a map containing a list of pairs key/value representing the preferences.
   */
  @NonNull
  public Single<Map<String, ?>> getAllOnce() {
    return Single.fromCallable(preferences::getAll);
  }

  /**
   * Observe all values from the preferences.
   * <p>
   * Note that you must not modify the collection returned by this method, or alter any of its contents. The consistency
   * of your stored data is not guaranteed if you do.
   * @return an {@link Observable} that emits a map containing a list of pairs key/value representing the preferences
   * each time any of the preferences change.
   */
  @NonNull
  public Observable<Map<String, ?>> getAllStream() {
    return getAllOnce()
        .toObservable()
        .mergeWith(Observable.create(emitter -> {
          RxAllPreferencesListener listener = new RxAllPreferencesListener(emitter);
          registerRxPreferenceListener(listener, emitter);
        }));
  }

  /**
   * Retrieve a string value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   * @return the preference value if it exists, otherwise the given default value.
   * @throws ClassCastException if there is a preference with this name that is not a String.
   */
  @NonNull
  public Single<String> getStringOnce(@NonNull String key, @NonNull String defaultValue) {
    return Single.fromCallable(() -> preferences.getString(key, defaultValue));
  }

  /**
   * Observe a string value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to emit if this preference does not exist.
   * @return An {@link Observable} for the string stored with the given name. Emits the current value upon subscription,
   * and emits the new value each time it is updated. If the value is null or is later set to null or cleared, the
   * provided default value is emitted.
   * @throws ClassCastException if there is a preference with this name that is not a String.
   */
  @NonNull
  public Observable<String> getStringStream(@NonNull String key, @NonNull String defaultValue) {
    return getStringOnce(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(key, defaultValue, SharedPreferences::getString));
  }

  /**
   * Retrieve a set of String values from the preferences.
   * <p>
   * Note that you must not modify the set instance returned by this call. The consistency of the stored data is not
   * guaranteed if you do, nor is your ability to modify the instance at all.
   * @param key The name of the preference to retrieve.
   * @param defaultValues Values to return if this preference does not exist.
   * @return the preference value if it exists, otherwise the given default value.
   * @throws ClassCastException if there is a preference with this name that is not a {@link Set}.
   */
  @NonNull
  public Single<Set<String>> getStringSetOnce(@NonNull String key, @NonNull Set<String> defaultValues) {
    return Single.fromCallable(() -> preferences.getStringSet(key, defaultValues));
  }

  /**
   * Observe a string value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to emit if this preference does not exist.
   * @return An {@link Observable} for the set stored with the given name. Emits the current value upon subscription,
   * and emits the new value each time it is updated. If the value is null or is later set to null or cleared, the
   * provided default value is emitted.
   * @throws ClassCastException if there is a preference with this name that is not a {@link Set}.
   */
  @NonNull
  public Observable<Set<String>> getStringSetStream(@NonNull String key, @NonNull Set<String> defaultValue) {
    return getStringSetOnce(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(key, defaultValue, SharedPreferences::getStringSet));
  }

  /**
   * Retrieve an int value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   * @return the preference value if it exists, otherwise the given default value.
   * @throws ClassCastException if there is a preference with this name that is not an int.
   */
  @NonNull
  public Single<Integer> getIntOnce(@NonNull String key, int defaultValue) {
    return Single.fromCallable(() -> preferences.getInt(key, defaultValue));
  }

  /**
   * Observe an int value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to emit if this preference does not exist.
   * @return An {@link Observable} for the int stored with the given name. Emits the current value upon subscription,
   * and emits the new value each time it is updated. If the value is null or is later set to null or cleared, the
   * provided default value is emitted.
   * @throws ClassCastException if there is a preference with this name that is not an int.
   */
  @NonNull
  public Observable<Integer> getIntStream(@NonNull String key, int defaultValue) {
    return getIntOnce(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(key, defaultValue, SharedPreferences::getInt));
  }

  /**
   * Retrieve a long value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   * @return the preference value if it exists, otherwise the given default value.
   * @throws ClassCastException if there is a preference with this name that is not a long.
   */
  @NonNull
  public Single<Long> getLongOnce(@NonNull String key, long defaultValue) {
    return Single.fromCallable(() -> preferences.getLong(key, defaultValue));
  }

  /**
   * Observe a long value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to emit if this preference does not exist.
   * @return An {@link Observable} for the long stored with the given name. Emits the current value upon subscription,
   * and emits the new value each time it is updated. If the value is null or is later set to null or cleared, the
   * provided default value is emitted.
   * @throws ClassCastException if there is a preference with this name that is not a long.
   */
  @NonNull
  public Observable<Long> getLongStream(@NonNull String key, long defaultValue) {
    return getLongOnce(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(key, defaultValue, SharedPreferences::getLong));
  }

  /**
   * Retrieve a float value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   * @return the preference value if it exists, otherwise the given default value.
   * @throws ClassCastException if there is a preference with this name that is not a float.
   */
  @NonNull
  public Single<Float> getFloatOnce(@NonNull String key, float defaultValue) {
    return Single.fromCallable(() -> preferences.getFloat(key, defaultValue));
  }

  /**
   * Observe a float value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to emit if this preference does not exist.
   * @return An {@link Observable} for the float stored with the given name. Emits the current value upon subscription,
   * and emits the new value each time it is updated. If the value is null or is later set to null or cleared, the
   * provided default value is emitted.
   * @throws ClassCastException if there is a preference with this name that is not a float.
   */
  @NonNull
  public Observable<Float> getFloatStream(@NonNull String key, float defaultValue) {
    return getFloatOnce(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(key, defaultValue, SharedPreferences::getFloat));
  }

  /**
   * Retrieve a boolean value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   * @return the preference value if it exists, otherwise the given default value.
   * @throws ClassCastException if there is a preference with this name that is not a boolean.
   */
  @NonNull
  public Single<Boolean> getBooleanOnce(@NonNull String key, boolean defaultValue) {
    return Single.fromCallable(() -> preferences.getBoolean(key, defaultValue));
  }

  /**
   * Observe a boolean value from the preferences.
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to emit if this preference does not exist.
   * @return An {@link Observable} for the boolean stored with the given name. Emits the current value upon
   * subscription, and emits the new value each time it is updated. If the value is null or is later set to null or
   * cleared, the provided default value is emitted.
   * @throws ClassCastException if there is a preference with this name that is not a boolean.
   */
  @NonNull
  public Observable<Boolean> getBooleanStream(@NonNull String key, boolean defaultValue) {
    return getBooleanOnce(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(key, defaultValue, SharedPreferences::getBoolean));
  }

  /**
   * Check whether the preferences contains a preference.
   * @param key The name of the preference to check.
   * @return true if the preference exists in the preferences, otherwise false.
   */
  @NonNull
  public Single<Boolean> containsOnce(@NonNull String key) {
    return Single.fromCallable(() -> preferences.contains(key));
  }

  /**
   * Observe whether the preferences contains a preference.
   * @param key The name of the preference to check.
   * @return An {@link Observable} that emits whether the preference with the given name exists. Emits upon subscription
   * as well as each time the preference is changed.
   * @throws ClassCastException if there is a preference with this name that is not a long.
   */
  @NonNull
  public Observable<Boolean> containsStream(@NonNull String key) {
    return containsOnce(key)
        .toObservable()
        .mergeWith(Observable.create(emitter -> {
          RxPreferenceContainsListener listener = new RxPreferenceContainsListener(key, emitter);
          registerRxPreferenceListener(listener, emitter);
        }));
  }

  /**
   * Create a new Editor for these preferences, through which you can make modifications to the data in the preferences
   * and atomically commit those changes back to the SharedPreferences object.
   * <p>
   * Note that you must call {@link Editor#commit} to have any changes you perform in the actually show up in the
   * preferences.
   * @return a new instance of {@link Editor}.
   */
  @NonNull
  public Editor edit() {
    return new Editor(preferences.edit());
  }

  @NonNull
  private <T> Observable<T> createPreferenceObservable(@NonNull String key, @NonNull T defaultValue,
      @NonNull GetPreference<T> getPreference) {
    return Observable.create(emitter -> {
      final RxPreferenceChangeListener<T> listener =
          new RxPreferenceChangeListener<>(key, emitter, defaultValue, getPreference);
      registerRxPreferenceListener(listener, emitter);
    });
  }

  private <T> void registerRxPreferenceListener(@NonNull RxPreferenceListener<T> listener,
      @NonNull ObservableEmitter<T> emitter) {
    emitter.setCancellable(() -> preferences.unregisterOnSharedPreferenceChangeListener(listener));
    preferences.registerOnSharedPreferenceChangeListener(listener);
  }

  /**
   * A wrapper for {@link SharedPreferences.Editor} that converts all methods into RxJava. For each {@code #put} method,
   * {@link Editor} pushes the passed value to the wrapped {@link SharedPreferences.Editor} and returns itself. Note
   * that you must call {@link #commit()} for any changes to actually show up in the preferences.
   * Unlike {@link SharedPreferences.Editor}, an {@code #apply()} method is not provided because it does not map to
   * RxJava well.
   */
  public static final class Editor {

    @NonNull private final SharedPreferences.Editor preferencesEditor;

    Editor(@NonNull SharedPreferences.Editor preferencesEditor) {
      this.preferencesEditor = preferencesEditor;
    }

    /**
     * Set a string value in the preferences editor, to be written back once {@link #commit} is called.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.  Passing null for this argument is equivalent to calling
     * {@link #remove(String)} with the same key.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor putString(@NonNull String key, @NonNull String value) {
      preferencesEditor.putString(key, value);
      return this;
    }

    /**
     * Set a set of string values in the preferences editor, to be written back once {@link #commit} is called.
     * @param key The name of the preference to modify.
     * @param values The new set of values for the preference. Passing null for this argument is equivalent to calling
     * {@link #remove(String)} with the same key.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor putStringSet(@NonNull String key, @NonNull Set<String> values) {
      preferencesEditor.putStringSet(key, values);
      return this;
    }

    /**
     * Set an int value in the preferences editor, to be written back once {@link #commit} is called.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.  Passing null for this argument is equivalent to calling
     * {@link #remove(String)} with the same key.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor putInt(@NonNull String key, int value) {
      preferencesEditor.putInt(key, value);
      return this;
    }

    /**
     * Set a long value in the preferences editor, to be written back once {@link #commit} is called.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.  Passing null for this argument is equivalent to calling
     * {@link #remove(String)} with the same key.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor putLong(@NonNull String key, long value) {
      preferencesEditor.putLong(key, value);
      return this;
    }

    /**
     * Set a float value in the preferences editor, to be written back once {@link #commit} is called.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.  Passing null for this argument is equivalent to calling
     * {@link #remove(String)} with the same key.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor putFloat(@NonNull String key, float value) {
      preferencesEditor.putFloat(key, value);
      return this;
    }

    /**
     * Set a boolean value in the preferences editor, to be written back once {@link #commit} is called.
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.  Passing null for this argument is equivalent to calling
     * {@link #remove(String)} with the same key.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor putBoolean(@NonNull String key, boolean value) {
      preferencesEditor.putBoolean(key, value);
      return this;
    }

    /**
     * Mark in the editor that a preference value should be removed, which will be done in the actual preferences once
     * {@link #commit} is called.
     * <p>
     * Note that when committing back to the preferences, all removals are done first, regardless of whether you called
     * {@code #remove} before or after {@code #put} methods on this editor.
     * @param key The name of the preference to remove.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor remove(@NonNull String key) {
      preferencesEditor.remove(key);
      return this;
    }

    /**
     * Mark in the editor to remove all values from the preferences. Once {@link #commit()} is called, the only
     * remaining preferences will be any that you have defined in this editor.
     * <p>
     * Note that when committing back to the preferences, the clear is done first, regardless of whether you called
     * {@code #clear} before or after {@code #put} methods on this editor.
     * @return a reference to the same {@link Editor} object, so you can chain calls together.
     */
    @NonNull
    public Editor clear() {
      preferencesEditor.clear();
      return this;
    }

    /**
     * Commit your preferences changes back from this {@link Editor} to the {@link RxPreferences} object it is editing.
     * This atomically performs the requested modifications, replacing whatever is currently in the preferences.
     * <p>
     * Note that when two editors are modifying preferences at the same time, the last one to call commit wins.
     * @return a {@link Completable} that commits the changes upon subscription.
     */
    @NonNull
    public Completable commit() {
      return Completable.fromAction(() -> {
        final boolean success = preferencesEditor.commit();
        if (!success) {
          throw new CommitException();
        }
      });
    }

    /**
     * An exception thrown if {@link #commit()} fails.
     */
    public static final class CommitException extends RuntimeException {
      CommitException() {
        super("Failed to commit the desired preference changes");
      }
    }
  }
}
