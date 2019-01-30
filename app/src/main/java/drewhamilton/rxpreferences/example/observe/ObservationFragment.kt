package drewhamilton.rxpreferences.example.observe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import drewhamilton.rxpreferences.example.ExampleApplication
import drewhamilton.rxpreferences.example.R
import drewhamilton.rxpreferences.example.base.ui.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.observe.*
import javax.inject.Inject

class ObservationFragment : RxFragment() {

  @Inject protected lateinit var observationViewModel: ObservationViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    ExampleApplication.applicationComponent.inject(this)
    return inflater.inflate(R.layout.observe, container)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

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
