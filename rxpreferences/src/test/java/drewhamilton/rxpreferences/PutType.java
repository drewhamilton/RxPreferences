package drewhamilton.rxpreferences;

import java.util.Set;

enum PutType {
  STRING(String.class),
  STRING_SET(Set.class),
  INT(Integer.class),
  LONG(Long.class),
  FLOAT(Float.class),
  BOOLEAN(Boolean.class);

  final Class<?> valueClass;

  PutType(Class<?> valueClass) {
    this.valueClass = valueClass;
  }
}
