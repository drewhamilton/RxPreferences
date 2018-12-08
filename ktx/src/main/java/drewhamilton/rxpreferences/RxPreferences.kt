package drewhamilton.rxpreferences

/**
 * Apply a series of edits to this [RxPreferences] instance and then commit them.
 */
inline fun RxPreferences.edit(edits: RxPreferences.Editor.() -> RxPreferences.Editor) =
    edits.invoke(this.edit()).commit()
