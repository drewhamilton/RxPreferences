package drewhamilton.rxpreferences.example.edit

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class EditingViewModel @Inject constructor(private val exampleRepository: MutableExampleRepository) : ViewModel() {

  fun getExampleStringOnce() = exampleRepository.getExampleStringOnce()

  fun getExampleIntegerOnce() = exampleRepository.getExampleIntOnce()

  fun setExampleValues(string: String, int: Int) = exampleRepository.changeExampleValues(string, int)

  fun removeExampleValues() = exampleRepository.removeExampleValues()
}
