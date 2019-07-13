# RxPreferences
An RxJava wrapper for Android's SharedPreferences.

Written in Java, with Kotlin extensions added in the `ktx` module.

## Download
[ ![Download](https://api.bintray.com/packages/drewhamilton/RxPreferences/RxPreferences/images/download.svg) ](https://bintray.com/drewhamilton/RxPreferences)

RxPreferences is available in JCenter.

```groovy
// RxPreferences:
implementation "drewhamilton.rxpreferences:rxpreferences:$version"
// With Kotlin extensions:
implementation "drewhamilton.rxpreferences:rxpreferences-ktx:$version"
// Dagger component:
implementation "drewhamilton.rxpreferences:rxpreferences-dagger:$version"
```

## Usage

### RxPreferences
Get the current value of any preference:
```java
rxPreferences.getIntOnce("Language count", 0)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(count -> displayCount(count));
```

Observe the initial value plus any changes to a preference:
```java
rxPreferences.getStringStream("Favorite language", "None")
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(name -> displayBestFriend(name));
```

Edit preferences and monitor the completion of committing those changes:
```java
rxPreferences.edit()
        .putInt("Language count", 1)
        .putString("Favorite language", "Java")
        .commit()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() -> showSavedIndicator(), error -> displayError(error));
```

### Kotlin extensions
With Kotlin extensions, use the `Editor` as a receiver:
```kotlin
rxPreferences.edit {
    putInt("Language count", 2)
    putString("Favorite language", "Kotlin")
}.subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe({ showSavedIndicator() }, { error -> displayError(error) })
```

### Dagger component
Easily provide and access `RxPreferences` instances using `RxPreferencesComponent`:
```kotlin
@Module
object PersistenceModule {

    private const val sharedPreferencesName = "example.SharedPreferences"

    private val ExampleApplication.sharedPreferences
        get() = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)

    @JvmStatic
    @Provides
    fun preferencesComponent(application: ExampleApplication) =
        RxPreferencesComponent.create(application.sharedPreferences)

    @JvmStatic
    @Provides
    @Reusable
    fun preferences(preferencesComponent: RxPreferencesComponent): RxPreferences = preferencesComponent.rxPreferences()
}
```

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
