# RxPreferences
An RxJava wrapper for Android's SharedPreferences.

Written in Java, with Kotlin extensions added in the `ktx` module.

## Download
[ ![Download](https://api.bintray.com/packages/drewhamilton/RxPreferences/RxPreferences/images/download.svg) ](https://bintray.com/drewhamilton/RxPreferences)

RxPreferences is available in JCenter.

To use RxPreferences in your application, include the following in your app's `build.gradle`:
```groovy
implementation "drewhamilton.rxpreferences:rxpreferences:$version"
```

To use RxPreferences along with Kotlin extensions, add "-ktx":
```groovy
implementation "drewhamilton.rxpreferences:rxpreferences-ktx:$version"
```

## Usage
Get the current value of any preference:
```java
rxPreferences.getInt("Number of examples", 0)
    .subscribeOn(Schedulers.single())
    .map(size -> newListOfExamples(size))
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(list -> display(list));
```

Observe the initial value plus any changes to a preference:
```java
rxPreferences.observeString("Type of second example", "Float")
    .subscribeOn(Schedulers.single())
    .map(type -> toActualExample(type))
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(example -> updateSecondExample(example));
```

Edit preferences and monitor the completion of committing those changes:
```java
rxPreferences.edit()
    .putInt("Number of examples", 4)
    .putString("Second example type", "String")
    .commit()
    .subscribeOn(Schedulers.single())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(() -> updateAllExamples(), error -> displayError(error));
```

With Kotlin extensions, use the `Editor` as a receiver:
```kotlin
rxPreferences.edit {
  putInt("Number of examples", 4)
  putString("Second example type", "String")
}.subscribeOn(Schedulers.single())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({ updateAllExamples() }, { displayError(it) })
```

## To-do
* More extensions
* Reduce duplication in release scripts

## License
```
Copyright 2018 Drew Hamilton

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
