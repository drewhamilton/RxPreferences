package drewhamilton.rxpreferences.example.observe

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ObservationViewModel @Inject constructor(private val exampleRepository: ExampleRepository) : ViewModel() {

  fun observeExampleString() = exampleRepository.observeExampleString()

  fun observeExampleInt() = exampleRepository.observeExampleInt()
}
