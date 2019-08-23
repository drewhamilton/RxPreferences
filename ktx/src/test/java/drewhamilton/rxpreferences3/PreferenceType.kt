package drewhamilton.rxpreferences3

enum class PreferenceType(val valueClass: Class<*>) {
    STRING(String::class.java),
    STRING_SET(Set::class.java),
    INT(Int::class.java),
    LONG(Long::class.java),
    FLOAT(Float::class.java),
    BOOLEAN(Boolean::class.java)
}
