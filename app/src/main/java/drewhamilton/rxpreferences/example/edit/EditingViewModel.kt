package drewhamilton.rxpreferences.example.edit

import androidx.lifecycle.ViewModel

class EditingViewModel(private val exampleRepository: MutableExampleRepository) : ViewModel() {

  fun getExampleString() = exampleRepository.getExampleString()

  fun getExampleInteger() = exampleRepository.getExampleInt()

  fun setExampleValues(string: String, int: Int) = exampleRepository.changeExampleValues(string, int)

  fun removeExampleValues() = exampleRepository.removeExampleValues()
}
