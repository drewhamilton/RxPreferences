package drewhamilton.rxpreferences.example.edit

import androidx.lifecycle.ViewModel
import drewhamilton.rxpreferences.example.GlobalProvider

class EditingViewModel(private val exampleRepository: MutableExampleRepository) : ViewModel() {

  // Required for default ViewModelProvider:
  constructor() : this(GlobalProvider.instance.mutableExampleRepository)

  fun getExampleString() = exampleRepository.getExampleString()

  fun setExampleString(value: String) = exampleRepository.changeExampleString(value)

  fun removeExampleString() = exampleRepository.removeExampleString()

  fun getExampleInteger() = exampleRepository.getExampleInt()

  fun setExampleInteger(value: Int) = exampleRepository.changeExampleInt(value)

  fun removeExampleInteger() = exampleRepository.removeExampleInt()
}
