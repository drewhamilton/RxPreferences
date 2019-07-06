package drewhamilton.rxpreferences.example.observe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.get
import drewhamilton.rxpreferences.example.ExampleApplication
import drewhamilton.rxpreferences.example.R
import drewhamilton.rxpreferences.example.base.ui.GenericViewModelFactory
import drewhamilton.rxpreferences.example.base.ui.RxFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.observe.*
import javax.inject.Inject

class ObservationFragment : RxFragment() {

  @Suppress("ProtectedInFinal")
  @Inject protected lateinit var viewModelFactory: GenericViewModelFactory<ObservationViewModel>

  private lateinit var observationViewModel: ObservationViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    ExampleApplication.applicationComponent.inject(this)
    observationViewModel = ViewModelProviders.of(this, viewModelFactory).get()
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater.inflate(R.layout.observe, container)!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observationViewModel.getExampleStringStream()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { stringValue.text = it }
        .trackUntilDestroyView()
    observationViewModel.getExampleIntStream()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { integerValue.text = it.toString() }
        .trackUntilDestroyView()
  }
}
