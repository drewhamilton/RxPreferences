package drewhamilton.rxpreferences.example.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Lazy
import javax.inject.Inject

class GenericViewModelFactory<VM : ViewModel> @Inject constructor(
    private val lazyViewModel: Lazy<VM>
) : ViewModelProvider.Factory {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T = lazyViewModel.get() as T
}
