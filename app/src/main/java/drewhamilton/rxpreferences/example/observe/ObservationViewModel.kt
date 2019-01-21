package drewhamilton.rxpreferences.example.observe

import androidx.lifecycle.ViewModel

class ObservationViewModel(private val exampleRepository: ExampleRepository) : ViewModel() {

  fun observeExampleString() = exampleRepository.observeExampleString()

  fun observeExampleInt() = exampleRepository.observeExampleInt()
}
