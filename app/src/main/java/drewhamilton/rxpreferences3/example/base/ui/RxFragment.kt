package drewhamilton.rxpreferences3.example.base.ui

import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class RxFragment : Fragment() {

    private val subscriptionsOnViewCreated: CompositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        subscriptionsOnViewCreated.clear()
        super.onDestroyView()
    }

    protected fun Disposable.trackUntilDestroyView() = subscriptionsOnViewCreated.add(this)
}
