package drewhamilton.rxpreferences.example.edit

import androidx.lifecycle.ViewModel
import drewhamilton.rxpreferences.example.GlobalProvider

class EditingViewModel(private val exampleRepository: MutableExampleRepository) : ViewModel() {

  // Required for default ViewModelProvider:
  constructor() : this(GlobalProvider.instance.mutableExampleRepository)

  fun getExampleString() = exampleRepository.getExampleString()

  fun getExampleInteger() = exampleRepository.getExampleInt()

  fun setExampleValues(string: String, int: Int) = exampleRepository.changeExampleValues(string, int)

  fun removeExampleValues() = exampleRepository.removeExampleValues()
}
