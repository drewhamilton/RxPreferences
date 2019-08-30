@file:Suppress("NOTHING_TO_INLINE")

package drewhamilton.rxpreferences3.example

import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

inline fun <reified T> Observable<T>.observeOn(scheduler: io.reactivex.Scheduler): Observable<T> =
    observeOn(RxJavaBridge.toV3Scheduler(scheduler))

inline fun <reified T> Single<T>.observeOn(scheduler: io.reactivex.Scheduler): Single<T> =
    observeOn(RxJavaBridge.toV3Scheduler(scheduler))

inline fun Completable.observeOn(scheduler: io.reactivex.Scheduler): Completable =
    observeOn(RxJavaBridge.toV3Scheduler(scheduler))

inline fun <reified T> Observable<T>.subscribeOn(scheduler: io.reactivex.Scheduler): Observable<T> =
    subscribeOn(RxJavaBridge.toV3Scheduler(scheduler))

inline fun <reified T> Single<T>.subscribeOn(scheduler: io.reactivex.Scheduler): Single<T> =
    subscribeOn(RxJavaBridge.toV3Scheduler(scheduler))

inline fun Completable.subscribeOn(scheduler: io.reactivex.Scheduler): Completable =
    subscribeOn(RxJavaBridge.toV3Scheduler(scheduler))
