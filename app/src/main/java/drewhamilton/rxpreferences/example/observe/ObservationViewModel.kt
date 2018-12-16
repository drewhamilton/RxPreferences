package drewhamilton.rxpreferences.example.observe

import androidx.lifecycle.ViewModel
import drewhamilton.rxpreferences.example.GlobalProvider

class ObservationViewModel(private val exampleRepository: ExampleRepository) : ViewModel() {

  // Required for default ViewModelProvider:
  constructor() : this(GlobalProvider.instance.exampleRepository)

  fun observeExampleString() = exampleRepository.observeExampleString()

  fun observeExampleInt() = exampleRepository.observeExampleInt()
}
