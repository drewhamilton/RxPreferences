package drewhamilton.rxpreferences.example.observe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import drewhamilton.rxpreferences.example.R
import drewhamilton.rxpreferences.example.base.ui.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.observe.*

class ObservationFragment : RxFragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.observe, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val observationViewModel = ViewModelProviders.of(this).get<ObservationViewModel>()

    observationViewModel.observeExampleString()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { stringValue.text = it }
        .trackUntilDestroyView()
    observationViewModel.observeExampleInt()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { integerValue.text = it.toString() }
        .trackUntilDestroyView()
  }
}
