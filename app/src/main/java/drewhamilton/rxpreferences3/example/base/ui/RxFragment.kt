package drewhamilton.rxpreferences.example.base.ui

import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxFragment : Fragment() {

    private val subscriptionsOnViewCreated: CompositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        subscriptionsOnViewCreated.clear()
        super.onDestroyView()
    }

    protected fun Disposable.trackUntilDestroyView() = subscriptionsOnViewCreated.add(this)
}
