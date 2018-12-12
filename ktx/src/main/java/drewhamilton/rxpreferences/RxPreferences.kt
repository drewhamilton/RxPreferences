package drewhamilton.rxpreferences

/**
 * Apply a series of edits to [this] and then commit them.
 *
 * Note that calls to [RxPreferences.Editor.remove] and [RxPreferences.Editor.clear] are executed first, regardless of
 * what order they appear in [edits].
 */
inline fun RxPreferences.edit(edits: RxPreferences.Editor.() -> RxPreferences.Editor) =
    edits.invoke(this.edit()).commit()
