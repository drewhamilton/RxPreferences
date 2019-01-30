package drewhamilton.rxpreferences.example

import dagger.BindsInstance
import dagger.Component
import drewhamilton.rxpreferences.example.edit.EditingFragment
import drewhamilton.rxpreferences.example.observe.ObservationFragment

@Component(modules = [
  ApplicationModule::class,
  PersistenceModule::class
])
interface ApplicationComponent {

  fun inject(editingFragment: EditingFragment)

  fun inject(observationFragment: ObservationFragment)

  @Component.Builder
  interface Builder {
    @BindsInstance fun application(application: ExampleApplication): Builder
    fun build(): ApplicationComponent
  }

  companion object {
    fun builder(): Builder = DaggerApplicationComponent.builder()
  }
}
