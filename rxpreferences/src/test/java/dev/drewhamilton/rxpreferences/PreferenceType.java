package dev.drewhamilton.rxpreferences;

import java.util.Set;

enum PreferenceType {
    STRING(String.class),
    STRING_SET(Set.class),
    INT(Integer.class),
    LONG(Long.class),
    FLOAT(Float.class),
    BOOLEAN(Boolean.class);

    final Class<?> valueClass;

    PreferenceType(Class<?> valueClass) {
        this.valueClass = valueClass;
    }
}
