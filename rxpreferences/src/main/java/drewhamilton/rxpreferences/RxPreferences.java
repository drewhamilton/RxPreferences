package drewhamilton.rxpreferences;

import android.content.SharedPreferences;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

import java.util.Map;
import java.util.Set;

public final class RxPreferences {

  @NonNull private final SharedPreferences preferences;

  public RxPreferences(@NonNull SharedPreferences preferences) {
    this.preferences = preferences;
  }

  /**
   * Retrieve all values from the preferences.
   *
   * <p>Note that you <em>must not</em> modify the collection returned
   * by this method, or alter any of its contents.  The consistency of your
   * stored data is not guaranteed if you do.
   *
   * @return Returns a map containing a list of pairs key/value representing
   * the preferences.
   */
  @NonNull
  public Single<Map<String, ?>> getAll() {
    return Single.fromCallable(preferences::getAll);
  }

  /**
   * Retrieve a String value from the preferences.
   *
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   *
   * @return Returns the preference value if it exists, or {@param defaultValue}.  Throws
   * ClassCastException if there is a preference with this name that is not
   * a String.
   */
  @NonNull
  public Single<String> getString(@NonNull String key, @NonNull String defaultValue) {
    return Single.fromCallable(() -> preferences.getString(key, defaultValue));
  }

  /**
   * @return An {@link Observable} for the string stored with the given {@param key}. If the
   * string is null or is later set to {@code null}, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<String> observeString(@NonNull String key, @NonNull String defaultValue) {
    return getString(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(preferences, key, defaultValue, SharedPreferences::getString));
  }

  /**
   * Retrieve a set of String values from the preferences.
   *
   * <p>Note that you <em>must not</em> modify the set instance returned
   * by this call.  The consistency of the stored data is not guaranteed
   * if you do, nor is your ability to modify the instance at all.
   *
   * @param key The name of the preference to retrieve.
   * @param defaultValues Values to return if this preference does not exist.
   *
   * @return Returns the preference values if they exist, or {@param defaultValues}.
   * Throws ClassCastException if there is a preference with this name
   * that is not a Set.
   */
  @NonNull
  public Single<Set<String>> getStringSet(@NonNull String key, @NonNull Set<String> defaultValues) {
    return Single.fromCallable(() -> preferences.getStringSet(key, defaultValues));
  }

  /**
   * @return An {@link Observable} for the string set stored with the given {@param key}. If the
   * string set is null or is later set to {@code null}, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<Set<String>> observeStringSet(@NonNull String key, @NonNull Set<String> defaultValue) {
    return getStringSet(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(preferences, key, defaultValue, SharedPreferences::getStringSet));
  }

  /**
   * Retrieve an int value from the preferences.
   *
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   *
   * @return Returns the preference value if it exists, or {@param defaultValue}. Throws
   * ClassCastException if there is a preference with this name that is not
   * an int.
   */
  @NonNull
  public Single<Integer> getInt(@NonNull String key, int defaultValue) {
    return Single.fromCallable(() -> preferences.getInt(key, defaultValue));
  }

  /**
   * @return An {@link Observable} for the int stored with the given {@param key}. If the
   * int is not saved or later cleared, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<Integer> observeInt(@NonNull String key, int defaultValue) {
    return getInt(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(preferences, key, defaultValue, SharedPreferences::getInt));
  }

  /**
   * Retrieve a long value from the preferences.
   *
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   *
   * @return Returns the preference value if it exists, or {@param defaultValue}.  Throws
   * ClassCastException if there is a preference with this name that is not
   * a long.
   */
  @NonNull
  public Single<Long> getLong(@NonNull String key, long defaultValue) {
    return Single.fromCallable(() -> preferences.getLong(key, defaultValue));
  }

  /**
   * @return An {@link Observable} for the long stored with the given {@param key}. If the
   * long is not saved or later cleared, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<Long> observeLong(@NonNull String key, long defaultValue) {
    return getLong(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(preferences, key, defaultValue, SharedPreferences::getLong));
  }

  /**
   * Retrieve a float value from the preferences.
   *
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   *
   * @return Returns the preference value if it exists, or {@param defaultValue}.  Throws
   * ClassCastException if there is a preference with this name that is not
   * a float.
   */
  @NonNull
  public Single<Float> getFloat(@NonNull String key, float defaultValue) {
    return Single.fromCallable(() -> preferences.getFloat(key, defaultValue));
  }

  /**
   * @return An {@link Observable} for the float stored with the given {@param key}. If the
   * float is not saved or later cleared, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<Float> observeFloat(@NonNull String key, float defaultValue) {
    return getFloat(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(preferences, key, defaultValue, SharedPreferences::getFloat));
  }

  /**
   * Retrieve a boolean value from the preferences.
   *
   * @param key The name of the preference to retrieve.
   * @param defaultValue Value to return if this preference does not exist.
   *
   * @return Returns the preference value if it exists, or {@param defaultValue}.  Throws
   * ClassCastException if there is a preference with this name that is not
   * a boolean.
   */
  @NonNull
  public Single<Boolean> getBoolean(@NonNull String key, boolean defaultValue) {
    return Single.fromCallable(() -> preferences.getBoolean(key, defaultValue));
  }

  /**
   * @return An {@link Observable} for the boolean stored with the given {@param key}. If the
   * boolean is not saved or later cleared, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<Boolean> observeBoolean(@NonNull String key, boolean defaultValue) {
    return getBoolean(key, defaultValue)
        .toObservable()
        .mergeWith(createPreferenceObservable(preferences, key, defaultValue, SharedPreferences::getBoolean));
  }

  /**
   * Checks whether the preferences contains a preference.
   *
   * @param key The name of the preference to check.
   * @return Returns true if the preference exists in the preferences,
   *         otherwise false.
   */
  @NonNull
  public Single<Boolean> contains(@NonNull String key) {
    return Single.fromCallable(() -> preferences.contains(key));
  }

  /**
   * @return An {@link Observable} for the int stored with the given {@param key}. If the
   * int is not saved or later cleared, {@param defaultValue} is emitted.
   */
  @NonNull
  public Observable<Boolean> observeContains(@NonNull String key) {
    return contains(key)
        .toObservable()
        .mergeWith(Observable.create(emitter -> {
          RxPreferenceContainsListener listener = new RxPreferenceContainsListener(key, emitter);
          registerRxPreferenceListener(preferences, listener, emitter);
        }));
  }

  /**
   * Create a new Editor for these preferences, through which you can make
   * modifications to the data in the preferences and atomically commit those
   * changes back to the SharedPreferences object.
   *
   * <p>Note that you <em>must</em> call {@link SharedPreferences.Editor#commit} to have any
   * changes you perform in the Editor actually show up in the
   * SharedPreferences.
   *
   * @return Returns a new instance of the {@link SharedPreferences.Editor} interface, allowing
   * you to modify the values in this SharedPreferences object.
   */
  @NonNull
  public Editor edit() {
    return new Editor(preferences.edit());
  }

  @NonNull
  private static <T> Observable<T> createPreferenceObservable(@NonNull SharedPreferences preferences,
      @NonNull String key, @NonNull T defaultValue, @NonNull GetPreference<T> getPreference) {
    return Observable.create(emitter -> {
      RxPreferenceChangeListener<T> listener =
          new RxPreferenceChangeListener<T>(key, emitter, defaultValue, getPreference);
      registerRxPreferenceListener(preferences, listener, emitter);
    });
  }

  private static <T> void registerRxPreferenceListener(SharedPreferences preferences,
                                                       RxPreferenceListener<T> listener, ObservableEmitter<T> emitter) {
    emitter.setCancellable(() -> preferences.unregisterOnSharedPreferenceChangeListener(listener));
    preferences.registerOnSharedPreferenceChangeListener(listener);
  }

  public static final class Editor {

    @NonNull private final SharedPreferences.Editor preferencesEditor;

    Editor(@NonNull SharedPreferences.Editor preferencesEditor) {
      this.preferencesEditor = preferencesEditor;
    }

    /**
     * Set a String value in the preferences editor, to be written back once
     * {@link #commit} is called.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.  Passing {@code null}
     *    for this argument is equivalent to calling {@link #remove(String)} with
     *    this key.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor putString(@NonNull String key, @NonNull String value) {
      preferencesEditor.putString(key, value);
      return this;
    }

    /**
     * Set a set of String values in the preferences editor, to be written
     * back once {@link #commit} is called.
     *
     * @param key The name of the preference to modify.
     * @param values The set of new values for the preference.  Passing {@code null}
     *    for this argument is equivalent to calling {@link #remove(String)} with
     *    this key.
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor putStringSet(@NonNull String key, @NonNull Set<String> values) {
      preferencesEditor.putStringSet(key, values);
      return this;
    }

    /**
     * Set an int value in the preferences editor, to be written back once
     * {@link #commit} is called.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor putInt(@NonNull String key, int value) {
      preferencesEditor.putInt(key, value);
      return this;
    }

    /**
     * Set a long value in the preferences editor, to be written back once
     * {@link #commit} is called.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor putLong(@NonNull String key, long value) {
      preferencesEditor.putLong(key, value);
      return this;
    }

    /**
     * Set a float value in the preferences editor, to be written back once
     * {@link #commit} is called.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor putFloat(@NonNull String key, float value) {
      preferencesEditor.putFloat(key, value);
      return this;
    }

    /**
     * Set a boolean value in the preferences editor, to be written back
     * once {@link #commit} is called.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor putBoolean(@NonNull String key, boolean value) {
      preferencesEditor.putBoolean(key, value);
      return this;
    }

    /**
     * Mark in the editor that a preference value should be removed, which
     * will be done in the actual preferences once {@link #commit} is
     * called.
     *
     * <p>Note that when committing back to the preferences, all removals
     * are done first, regardless of whether you called remove before
     * or after put methods on this editor.
     *
     * @param key The name of the preference to remove.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor remove(@NonNull String key) {
      preferencesEditor.remove(key);
      return this;
    }

    /**
     * Mark in the editor to remove <em>all</em> values from the
     * preferences.  Once commit is called, the only remaining preferences
     * will be any that you have defined in this editor.
     *
     * <p>Note that when committing back to the preferences, the clear
     * is done first, regardless of whether you called clear before
     * or after put methods on this editor.
     *
     * @return Returns a reference to the same Editor object, so you can
     * chain put calls together.
     */
    @NonNull
    public Editor clear() {
      preferencesEditor.clear();
      return this;
    }

    /**
     * Commit your preferences changes back from this Editor to the
     * {@link SharedPreferences} object it is editing.  This atomically
     * performs the requested modifications, replacing whatever is currently
     * in the SharedPreferences.
     *
     * <p>Note that when two editors are modifying preferences at the same
     * time, the last one to call commit wins.
     *
     * @return a {@link Completable} that commits the changes when subscribed to.
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

    public static final class CommitException extends RuntimeException {
      CommitException() {
        super("Failed to commit the desired preference changes");
      }
    }
  }
}
