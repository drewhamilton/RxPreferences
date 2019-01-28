package drewhamilton.rxpreferences.example

import android.content.Context
import drewhamilton.rxpreferences.RxPreferences
import drewhamilton.rxpreferences.example.edit.EditingViewModel
import drewhamilton.rxpreferences.example.edit.MutableExampleRepository
import drewhamilton.rxpreferences.example.observe.ExampleRepository
import drewhamilton.rxpreferences.example.observe.ObservationViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

object Modules {

  private lateinit var application: ExampleApplication

  val applicationModule = module {
    single { application }
  }

  val persistenceModule = module {
    single { RxPreferences(get<Context>().sharedPreferences) }
  }

  val observeModule = module {
    single { ExampleRepository(get()) }
    viewModel { ObservationViewModel(get()) }
  }

  val editModule = module {
    single { MutableExampleRepository(get()) }
    viewModel { EditingViewModel(get()) }
  }

  private const val sharedPreferencesName = "drewhamilton.rxpreferences.example.SharedPreferences"

  private val Context.sharedPreferences
    get() = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)
}
